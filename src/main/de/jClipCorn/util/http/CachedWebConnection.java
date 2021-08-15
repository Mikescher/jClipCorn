package de.jClipCorn.util.http;

import de.jClipCorn.Main;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.SimpleSerializableData;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.exceptions.HTTPErrorCodeException;
import de.jClipCorn.util.exceptions.XMLFormatException;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.codec.digest.DigestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"nls", "Duplicates"})
public class CachedWebConnection extends WebConnectionLayer {
	private final static float DELAY_FACTOR = 0.01f;
	
	private final WebConnectionLayer conn;
	
	private FSPath cachePath;
	private FSPath cacheDatabasePath;
	
	private final Object dblock = new Object();
	private SimpleSerializableData db;
	private HashMap<String, String> dbref;
	
	public CachedWebConnection(WebConnectionLayer realConnection) {
		conn = realConnection;
	}

	@Override
	public void init() {
		synchronized(dblock) {
			cachePath = Main.getCurrentGlobalCCProperties().PROP_DEBUG_HTTPCACHE_PATH.getValue();
			cacheDatabasePath = cachePath.append("cache.xml");

			try {
				cacheDatabasePath.createFolders();

				CCLog.addDebug("Start loading HTTP-Cache from drive");

				if (cacheDatabasePath.fileExists()) {
					db = SimpleSerializableData.load(cacheDatabasePath, false);
					dbref = new HashMap<>();
					//for (SimpleSerializableData dat : db.enumerateChildren()) {
					//	try {
					//		if (dat.getInt("type") == 0)
					//			dbref.put(dat.getStr("result"), SimpleFileUtils.readUTF8TextFile(PathFormatter.combine(cachePath, dat.getStr("result"))));
					//	} catch (Exception e) {
					//		CCLog.addError(e);
					//	}
					//}
				} else {
					db = SimpleSerializableData.createEmpty(false);
					dbref = new HashMap<>();
				}
			} catch (XMLFormatException | IOException e) {
				CCLog.addError(e);
			}
	
			CCLog.addDebug("Finished loading HTTP-Cache from drive");
			
			CCLog.addWarning("You are using a cached web connection - if you see this message and are no developer you're fucked...");
		}
	}

	private String getResult(SimpleSerializableData d) throws IOException {
		if (dbref.containsKey(d.getStr("result"))) {
			return dbref.get(d.getStr("result"));
		}
		else {
			String data = cachePath.append(d.getStr("result")).readAsUTF8TextFile();
			dbref.put(d.getStr("result"), data);
			return data;
		}
	}

	@Override
	public String getUncaughtHTML(URL url, boolean stripLineBreaks) throws IOException, HTTPErrorCodeException {
		try {
			String id = "getUncaughtHTML|" + url.toExternalForm() + "|" + stripLineBreaks;

			WebConnectionLayer.RequestCountGet.incrementAndGet();

			if (db.containsChild(id)) {
				CCLog.addDebug("[HTTP-CACHE] load from Cache: " + id);
				
				SimpleSerializableData d = db.getChild(id);
				sleep(d.getInt("time"));
				
				if (d.getInt("type") == 0) {
					return getResult(d);
				} else if (d.getInt("type") == 1) {
					throw new HTTPErrorCodeException(d.getInt("statuscode"));
				} else if (d.getInt("type") == 2) {
					throw new IOException(d.getStr("exceptionmessage"));
				} else {
					CCLog.addError("http cache db invalid state (56)");
					return null;
				}
			} else {
				CCLog.addDebug("[HTTP-CACHE] load from real connection: " + id);
				
				SimpleSerializableData d = db.addChild(id);
				
				long start = System.currentTimeMillis();
				try {
					String r = conn.getUncaughtHTML(url, stripLineBreaks);
					String fn = UUID.randomUUID().toString();
					cachePath.append(fn).writeAsUTF8TextFile(r);
					d.set("type", 0);
					d.set("result", fn);
					d.set("time", (int)(System.currentTimeMillis() - start));
					return r;
				} catch (HTTPErrorCodeException e) {
					d.set("type", 1);
					d.set("statuscode", e.Errorcode);
					d.set("time", (int)(System.currentTimeMillis() - start));
					throw e;
				} catch (IOException e) {
					d.set("type", 2);
					d.set("exceptionmessage", e.getMessage());
					d.set("time", (int)(System.currentTimeMillis() - start));
					throw e;
				}
			}
		} finally {
			try {
				synchronized(dblock) { db.save(cacheDatabasePath);}
			} catch (IOException e) {
				CCLog.addError(e);
			}
		}
	}

	@Override
	public Tuple<String, List<Tuple<String, String>>> getUncaughtPostContent(URL url, String body) throws IOException, HTTPErrorCodeException {
		try {
			String id1 = "getUncaughtPostContent<1>|" + url.toExternalForm() + "|" + DigestUtils.md5Hex(body);
			String id2 = "getUncaughtPostContent<2>|" + url.toExternalForm() + "|" + DigestUtils.md5Hex(body);

			WebConnectionLayer.RequestCountPost.incrementAndGet();

			if (db.containsChild(id1) && db.containsChild(id2)) {
				CCLog.addDebug("[HTTP-CACHE] load from Cache: " + id1);

				SimpleSerializableData d1 = db.getChild(id1);
				SimpleSerializableData d2 = db.getChild(id2);

				sleep(d1.getInt("time"));

				if (d1.getInt("type") == 0) {
					String r1 = getResult(d1);
					List<Tuple<String, String>> r2 = CCStreams.iterate(getResult(d2).split("\n")).map(p -> Tuple.Create(p.split("\t")[0], p.split("\t")[1])).enumerate();
					return Tuple.Create(r1, r2);
				} else if (d1.getInt("type") == 1) {
					throw new HTTPErrorCodeException(d1.getInt("statuscode"));
				} else if (d1.getInt("type") == 2) {
					throw new IOException(d1.getStr("exceptionmessage"));
				} else {
					CCLog.addError("http cache db invalid state (56)");
					return null;
				}
			} else {
				CCLog.addDebug("[HTTP-CACHE] load from real connection: " + id1);

				SimpleSerializableData d1 = db.addChild(id1);
				SimpleSerializableData d2 = db.addChild(id2);

				long start = System.currentTimeMillis();
				try {
					Tuple<String, List<Tuple<String, String>>> r = conn.getUncaughtPostContent(url, body);

					String fn1 = UUID.randomUUID().toString();
					cachePath.append(fn1).writeAsUTF8TextFile(r.Item1);
					d1.set("type", 0);
					d1.set("result", fn1);
					d1.set("time", (int)(System.currentTimeMillis() - start));

					String fn2 = UUID.randomUUID().toString();
					cachePath.append(fn1).writeAsUTF8TextFile(CCStreams.iterate(r.Item2).stringjoin(b -> b.Item1+"\t"+b.Item2, "\n"));
					d2.set("type", 0);
					d2.set("result", fn2);
					d2.set("time", (int)(System.currentTimeMillis() - start));

					return r;
				} catch (HTTPErrorCodeException e) {
					d1.set("type", 1);
					d1.set("statuscode", e.Errorcode);
					d1.set("time", (int)(System.currentTimeMillis() - start));

					d2.set("type", 1);
					d2.set("statuscode", e.Errorcode);
					d2.set("time", (int)(System.currentTimeMillis() - start));
					throw e;
				} catch (IOException e) {
					d1.set("type", 2);
					d1.set("exceptionmessage", e.getMessage());
					d1.set("time", (int)(System.currentTimeMillis() - start));

					d2.set("type", 2);
					d2.set("exceptionmessage", e.getMessage());
					d2.set("time", (int)(System.currentTimeMillis() - start));
					throw e;
				}
			}
		} finally {
			try {
				synchronized(dblock) { db.save(cacheDatabasePath);}
			} catch (IOException e) {
				CCLog.addError(e);
			}
		}
	}

	@Override
	public BufferedImage getImage(String url) {
		try {
			String id = "getImage|" + url;

			WebConnectionLayer.RequestCountImage.incrementAndGet();

			if (db.containsChild(id)) {
				CCLog.addDebug("[HTTP-CACHE] load from Cache: " + id);
				
				SimpleSerializableData d = db.getChild(id);
				sleep(d.getInt("time"));
				
				if (d.getBool("null")) {
					return null;
				} else {
					try {

						return ImageIO.read(cachePath.append(d.getStr("result")).toFile());
					} catch (IOException e) {
						CCLog.addError(e);
						return null;
					}
				}
			} else {
				CCLog.addDebug("[HTTP-CACHE] load from real: " + id);
				
				SimpleSerializableData d = db.addChild(id);

				long start = System.currentTimeMillis();
				try {
					BufferedImage r = conn.getImage(url);
					String fn = UUID.randomUUID().toString();
					ImageIO.write(r, "PNG", cachePath.append(fn).toFile());
					d.set("type", 99);
					d.set("null", false);
					d.set("result", fn);
					d.set("time", (int)(System.currentTimeMillis() - start));
					return r;
				} catch (IOException e) {
					d.set("type", 99);
					d.set("null", true);
					d.set("time", (int)(System.currentTimeMillis() - start));
					return null;
				}
			}
		} finally {
			try {
				synchronized(dblock) { db.save(cacheDatabasePath); }
			} catch (IOException e) {
				CCLog.addError(e);
			}
		}
	}
	
	private void sleep(int ms) {
		ms = (int)(ms * DELAY_FACTOR);
		
		if (ms == 0) return;
		
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			CCLog.addError(e);
		}
	}
}
