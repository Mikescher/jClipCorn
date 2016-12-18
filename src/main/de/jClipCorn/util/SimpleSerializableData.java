package de.jClipCorn.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.jClipCorn.util.exceptions.XMLFormatException;
import de.jClipCorn.util.helper.SimpleFileUtils;

@SuppressWarnings("nls")
public class SimpleSerializableData {

	private static final Decoder B64_DECODER = Base64.getDecoder();
	private static final Encoder B64_ENCODER = Base64.getEncoder();
	
	private final Map<String, SimpleSerializableData> children = new HashMap<>();
	private final Map<String, String> dataString = new HashMap<>();
	private final Map<String, Integer> dataInt = new HashMap<>();
	private final Map<String, Boolean> dataBool = new HashMap<>();
	private final Map<String, Byte[]> dataRaw = new HashMap<>();
	
	private SimpleSerializableData() {
		
	}
	
	public String getStr(String id) {
		return dataString.get(id);
	}
	
	public int getInt(String id) {
		return dataInt.get(id);
	}
	
	public byte[] getRaw(String id) {
		return ArrayUtils.toPrimitive(dataRaw.get(id));
	}

	public boolean getBool(String id) {
		return dataBool.get(id);
	}
	
	public void set(String id, String val) {
		dataString.put(id, val);
	}
	
	public void set(String id, int val) {
		dataInt.put(id, val);
	}
	
	public void set(String id, boolean val) {
		dataBool.put(id, val);
	}
	
	public void set(String id, byte[] val) {
		dataRaw.put(id, ArrayUtils.toObject(val));
	}

	public boolean containsChild(String id) {
		return children.containsKey(id);
	}

	public SimpleSerializableData getChild(String id) {
		return children.get(id);
	}
	
	public SimpleSerializableData addChild(String id) {
		SimpleSerializableData r = new SimpleSerializableData();
		children.put(id, r);
		return r;
	}

	public static SimpleSerializableData createEmpty() {
		return new SimpleSerializableData();
	}
	
	public static SimpleSerializableData load(String filename) throws XMLFormatException {
		try {
			Document doc = new SAXBuilder().build(new StringReader(SimpleFileUtils.readUTF8TextFile(filename)));
			
			Element root = doc.getRootElement();
			
			if (! root.getName().equals("root")) throw new XMLFormatException("found unsupported element: <" + root.getName() + ">");
			
			return deserialize(root);
		} catch (Exception e) {
			throw new XMLFormatException(e.getMessage(), e);
		}
	}
	
	public void save(String filepath) throws IOException {
		Document xml = new Document(new Element("root"));
		
		Element root = xml.getRootElement();
		
		serialize(root);
		
		XMLOutputter xout = new XMLOutputter();
		xout.setFormat(Format.getPrettyFormat());
		
		FileOutputStream ostream = null;
		try {
			ostream = new FileOutputStream(filepath);
			xout.output(xml, ostream);
		} finally {
			if (ostream != null) {
				ostream.close();
			}
		}
	}
	
	private static SimpleSerializableData deserialize(Element el) throws XMLFormatException, DataConversionException, NumberFormatException {	
		SimpleSerializableData r = new SimpleSerializableData();
		
		for (Attribute attr : el.getAttributes()) {
			if (attr.getName().equals("id")) {
				// ignore
			} else if (attr.getName().startsWith("str_")) {
				r.dataString.put(attr.getName().substring(4), attr.getValue());
			} else if (attr.getName().startsWith("int_")) {
				r.dataInt.put(attr.getName().substring(4), attr.getIntValue());
			} else if (attr.getName().startsWith("raw_")) {
				r.dataRaw.put(attr.getName().substring(4), ArrayUtils.toObject(B64_DECODER.decode(attr.getValue())));
			} else if (attr.getName().startsWith("bool_")) {
				r.dataBool.put(attr.getName().substring(5), attr.getValue().equals("true"));
			} else {
				throw new XMLFormatException("found unsupported attribute-type: " + attr.getName());
			}
		}
		
		for (Element child : el.getChildren()) {
			if (child.getName().equals("element"))
				r.children.put(child.getAttributeValue("id"), deserialize(child));
			else if (child.getName().equals("attr_str"))
				r.dataString.put(child.getAttributeValue("name"), new String(B64_DECODER.decode(child.getText()), Charset.forName("UTF-8")));
			else if (child.getName().equals("attr_int"))
				r.dataInt.put(child.getAttributeValue("name"), Integer.parseInt(child.getText()));
			else if (child.getName().equals("attr_bool"))
				r.dataBool.put(child.getAttributeValue("name"), child.getText().equals("true"));
			else if (child.getName().equals("attr_raw"))
				r.dataRaw.put(child.getAttributeValue("name"), ArrayUtils.toObject(B64_DECODER.decode(child.getText())));
			else 
				throw new XMLFormatException("found unsupported element: <" + child.getName() + ">");
		}
		
		return r;
	}
	
	private void serialize(Element e) {
		for (Entry<String, Integer> data : dataInt.entrySet()) {
			e.setAttribute("int_"+data.getKey(), Integer.toString(data.getValue()));
		}

		for (Entry<String, Boolean> data : dataBool.entrySet()) {
			e.setAttribute("bool_"+data.getKey(), data.getValue() ? "true" : "false");
		}

		for (Entry<String, String> data : dataString.entrySet()) {
			if (data.getValue().length() < 48) {
				e.setAttribute("str_"+data.getKey(), data.getValue());				
			} else {
				Element sub = new Element("attr_str");
				sub.setText(B64_ENCODER.encodeToString(Charset.forName("UTF-8").encode(data.getValue()).array()));
				e.addContent(sub);
			}
		}

		for (Entry<String, Byte[]> data : dataRaw.entrySet()) {
			String dataValue = B64_ENCODER.encodeToString(ArrayUtils.toPrimitive(data.getValue()));
			if (dataValue.length() < 32) {
				e.setAttribute("raw_"+data.getKey(), dataValue);				
			} else {
				Element sub = new Element("attr_raw");
				sub.setText(dataValue);
				e.addContent(sub);
			}
		}
		
		for (Entry<String, SimpleSerializableData> child : children.entrySet()) {
			Element sub = new Element("element");
			child.getValue().serialize(sub);
			sub.setAttribute("id", child.getKey());
			e.addContent(sub);
		}
	}
}
