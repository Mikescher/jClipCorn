package de.jClipCorn.util.xml;

import org.jdom2.Attribute;
import org.jdom2.Element;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.stream.CCStreams;

@SuppressWarnings("nls")
public class CCXMLElement {

	private final CCXMLParser _owner;
	private final String _path;
	private final Element _element;

	public CCXMLElement(CCXMLParser parser, String path, Element elem) {
		_owner = parser;
		_path = path;
		_element = elem;
	}

	public CCXMLElement getFirstChildOrThrow(String name) throws CCXMLException {
		for (Element e : _element.getChildren()) {
			if (e.getName().equals(name)) return new CCXMLElement(_owner, _path + "/" + name, e);
		}
		throw new CCXMLException(Str.format("Could not find child <{0}> in {1}", name, _path), _owner.getXMLString());
	}

	public CCXMLElement getFirstChildOrNull(String name) {
		for (Element e : _element.getChildren()) {
			if (e.getName().equals(name)) return new CCXMLElement(_owner, _path + "/" + name, e);
		}
		return null;
	}

	public String getAttributeValueOrThrow(String name) throws CCXMLException {
		for (Attribute e : _element.getAttributes()) {
			if (e.getName().equals(name)) return e.getValue();
		}
		throw new CCXMLException(Str.format("Could not find attribute '{0}' in element {1}", name, _path), _owner.getXMLString());
	}

	public String getFirstChildValueOrThrow(String name) throws CCXMLException {
		return getFirstChildOrThrow(name).getContent();
	}

	public int getFirstChildIntValueOrThrow(String name) throws CCXMLException {
		String v = getFirstChildValueOrThrow(name);
		try {
			return Integer.parseInt(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the child <{0}> in {1} is not an integer", name, _path), _owner.getXMLString());
		}
	}

	public int getFirstChildIntValueOrDefault(String name, int defaultValue) throws CCXMLException {
		String v = getFirstChildValueOrDefault(name, null);
		if (v == null) return defaultValue;
		try {
			return Integer.parseInt(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the child <{0}> in {1} is not an integer", name, _path), _owner.getXMLString());
		}
	}

	public double getFirstChildDoubleValueOrThrow(String name) throws CCXMLException {
		String v = getFirstChildValueOrThrow(name);
		try {
			return Double.parseDouble(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the child <{0}> in {1} is not a double", name, _path), _owner.getXMLString());
		}
	}

	public double getFirstChildDoubleValueOrDefault(String name, int defaultValue) throws CCXMLException {
		String v = getFirstChildValueOrDefault(name, null);
		if (v == null) return defaultValue;
		try {
			return Double.parseDouble(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the child <{0}> in {1} is not a double", name, _path), _owner.getXMLString());
		}
	}

	public long getFirstChildLongValueOrThrow(String name) throws CCXMLException {
		String v = getFirstChildValueOrThrow(name);
		try {
			return Long.parseLong(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the child <{0}> in {1} is not a long", name, _path), _owner.getXMLString());
		}
	}

	public long getFirstChildLongValueOrDefault(String name, int defaultValue) throws CCXMLException {
		String v = getFirstChildValueOrDefault(name, null);
		if (v == null) return defaultValue;
		try {
			return Long.parseLong(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the child <{0}> in {1} is not a long", name, _path), _owner.getXMLString());
		}
	}

	public String getFirstChildValueOrDefault(String name, String defaultValue) {
		CCXMLElement e = getFirstChildOrNull(name);
		if (e == null) return defaultValue;
		return e.getContent();
	}

	private String getContent() {
		return _element.getValue();
	}

	public Iterable<? extends CCXMLElement> getAllChildren(String name) {
		return CCStreams.iterate(_element.getChildren()).filter(c -> c.getName().equals(name)).map(c -> new CCXMLElement(_owner, _path+"/"+name, c));
	}
}
