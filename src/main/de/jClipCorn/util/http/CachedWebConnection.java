package de.jClipCorn.util.http;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import javax.imageio.ImageIO;

import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.SimpleSerializableData;
import de.jClipCorn.util.exceptions.HTTPErrorCodeException;
import de.jClipCorn.util.exceptions.XMLFormatException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.SimpleFileUtils;

@SuppressWarnings("nls")
public class CachedWebConnection extends WebConnectionLayer {

	private final WebConnectionLayer conn;
	
	private String cachePath;
	private String cacheDatabasePath;
	
	private SimpleSerializableData db;
	
	public CachedWebConnection(WebConnectionLayer realConnection) {
		conn = realConnection;
	}

	@Override
	public void init() {
		cachePath = CCProperties.getInstance().PROP_DEBUG_HTTPCACHE_PATH.getValue();
		cachePath = PathFormatter.appendSeparator(cachePath);
		cacheDatabasePath = PathFormatter.combine(cachePath, "cache.xml");
		
		PathFormatter.createFolders(cacheDatabasePath);
		
		try {
			if (PathFormatter.fileExists(cacheDatabasePath)) {
				db = SimpleSerializableData.load(cacheDatabasePath);
			} else {
				db = SimpleSerializableData.createEmpty();
			}
		} catch (XMLFormatException e) {
			CCLog.addError(e);
		}
		
		CCLog.addWarning("You are using a cached web connection - if you see this message and are no developer you're fucked...");
	}

	@Override
	public String getUncaughtHTML(URL url, boolean stripLineBreaks) throws IOException, HTTPErrorCodeException {
		try {
			String id = "getUncaughtHTML|" + url.toExternalForm() + "|" + stripLineBreaks;
			
			if (db.containsChild(id)) {
				CCLog.addDebug("[HTTP-CACHE] load from Cache: " + id);
				
				SimpleSerializableData d = db.getChild(id);
				sleep(d.getInt("time"));
				
				if (d.getInt("type") == 0) {
					return SimpleFileUtils.readUTF8TextFile(PathFormatter.combine(cachePath, d.getStr("result")));
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
					SimpleFileUtils.writeTextFile(PathFormatter.combine(cachePath, fn), r);
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
				db.save(cacheDatabasePath);
			} catch (IOException e) {
				CCLog.addError(e);
			}
		}
	}

	@Override
	public BufferedImage getImage(String url) {
		try {
			String id = "getImage|" + url;
	
			if (db.containsChild(id)) {
				CCLog.addDebug("[HTTP-CACHE] load from Cache: " + id);
				
				SimpleSerializableData d = db.getChild(id);
				sleep(d.getInt("time"));
				
				if (d.getBool("null")) {
					return null;
				} else {
					try {
						return ImageIO.read(new File(d.getStr("result")));
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
					ImageIO.write(r, "PNG", new File(PathFormatter.combine(cachePath, fn)));
					d.set("null", false);
					d.set("result", fn);
					d.set("time", (int)(System.currentTimeMillis() - start));
					return r;
				} catch (IOException e) {
					d.set("null", true);
					d.set("time", (int)(System.currentTimeMillis() - start));
					return null;
				}
			}
		} finally {
			try {
				db.save(cacheDatabasePath);
			} catch (IOException e) {
				CCLog.addError(e);
			}
		}
	}
	
	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			CCLog.addError(e);
		}
	}
}