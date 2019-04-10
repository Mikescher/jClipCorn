package de.jClipCorn.util.xml;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.StringReader;

@SuppressWarnings("nls")
public class CCXMLParser {

	private final String _original;
	private final String _content;
	private final Document _document;

	private CCXMLParser(String raw, Document doc) {
		_original = raw;
		_document = doc;
		_content = fmtXML(doc);
	}

	private static String fmtXML(Document d) {
		XMLOutputter xout = new XMLOutputter();
		xout.setFormat(Format.getPrettyFormat());
		return xout.outputString(d);
	}

	public static CCXMLParser parse(String xml) throws CCXMLException {
		try {
			return new CCXMLParser(xml, new SAXBuilder().build(new StringReader(xml)));
		} catch (JDOMException | IOException e) {
			throw new CCXMLException("Could not parse XML: " + ExceptionUtils.getMessage(e), xml);
		}
	}

	public CCXMLElement getRoot() {
		Element r = _document.getRootElement();
		return new CCXMLElement(this, "/"+r.getName(), r);
	}

	public String getXMLString() {
		return _content;
	}

	public String getOriginalXMLString() {
		return _original;
	}
}
