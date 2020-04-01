package de.jClipCorn.util.xml;

import de.jClipCorn.util.exceptions.BooleanFormatException;
import de.jClipCorn.util.lambda.Func1to0WithGenericException;
import de.jClipCorn.util.stream.CCStream;
import org.jdom2.Attribute;
import org.jdom2.Element;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.stream.CCStreams;

@SuppressWarnings("nls")
public class CCXMLElement
{
	private final CCXMLParser _owner;
	private final String _path;
	private final Element _element;

	//####################################################################################

	public CCXMLElement(CCXMLParser parser, String path, Element elem) {
		_owner = parser;
		_path = path;
		_element = elem;
	}

	//####################################################################################

	public String getContent() {
		return _element.getValue();
	}

	public int getChildrenCount() {
		return _element.getChildren().size();
	}

	public String getName() {
		return _element.getName();
	}

	public boolean hasAttribute(String attrName) {
		return getAttributeValueOrDefault(attrName, null) != null;
	}

	public boolean hasAllAttributes(String s0, String... s) {
		return hasAttribute(s0) && CCStreams.iterate(s).all(this::hasAttribute);
	}

	public CCXMLParser getOwner() {
		return _owner;
	}

	//####################################################################################

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

	public CCStream<CCXMLElement> getAllChildren() {
		return CCStreams
				.iterate(_element.getChildren())
				.map(c -> new CCXMLElement(_owner, _path+"/"+c.getName(), c));
	}

	public CCStream<CCXMLElement> getAllChildren(String name) {
		return CCStreams
				.iterate(_element.getChildren())
				.filter(c -> c.getName().equalsIgnoreCase(name))
				.map(c -> new CCXMLElement(_owner, _path+"/"+c.getName(), c));
	}

	public CCStream<CCXMLElement> getAllChildren(String[] names) {
		return CCStreams
				.iterate(_element.getChildren())
				.filter(c ->
				{
					for (String name : names) if (c.getName().equalsIgnoreCase(name)) return true;
					return false;
				})
				.map(c -> new CCXMLElement(_owner, _path+"/"+c.getName(), c));
	}

	public CCXMLElement getFirstChildByAttrOrThrow(String nodename, String attrname, String attrval) throws CCXMLException {
		for (Element e : _element.getChildren()) {
			if (e.getName().equals(nodename) && Str.equals(attrval, e.getAttributeValue(attrname)) ) return new CCXMLElement(_owner, _path + "/" + e.getName(), e);
		}
		throw new CCXMLException(Str.format("Could not find child <{0}[{1}={2}]> in {1}", nodename, attrname, attrval, _path), _owner.getXMLString());
	}

	public CCXMLElement getFirstChildByAttrOrNull(String nodename, String attrname, String attrval) {
		for (Element e : _element.getChildren()) {
			if (e.getName().equals(nodename) && Str.equals(attrval, e.getAttributeValue(attrname)) ) return new CCXMLElement(_owner, _path + "/" + e.getName(), e);
		}
		return null;
	}

	//####################################################################################

	public String getAttributeValueOrThrow(String attrName) throws CCXMLException {
		for (Attribute e : _element.getAttributes()) {
			if (e.getName().equals(attrName)) return e.getValue();
		}
		throw new CCXMLException(Str.format("Could not find attribute ''{0}'' in element {1}", attrName, _path), _owner.getXMLString());
	}

	public String getAttributeValueOrDefault(String attrName, String defaultValue) {
		for (Attribute e : _element.getAttributes()) {
			if (e.getName().equals(attrName)) return e.getValue();
		}
		return defaultValue;
	}

	public int getAttributeIntValueOrThrow(String attrName) throws CCXMLException {
		String v = getAttributeValueOrThrow(attrName);
		try {
			return Integer.parseInt(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the attribute ''{1}'' in {2} is not an integer", v, attrName, _path), _owner.getXMLString());
		}
	}

	public short getAttributeShortValueOrThrow(String attrName) throws CCXMLException {
		String v = getAttributeValueOrThrow(attrName);
		try {
			return Short.parseShort(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the attribute ''{1}'' in {2} is not an integer", v, attrName, _path), _owner.getXMLString());
		}
	}

	public long getAttributeLongValueOrThrow(String attrName) throws CCXMLException {
		String v = getAttributeValueOrThrow(attrName);
		try {
			return Long.parseLong(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the attribute ''{1}'' in {2} is not a long", v, attrName, _path), _owner.getXMLString());
		}
	}

	public double getAttributeDoubleValueOrThrow(String attrName) throws CCXMLException {
		String v = getAttributeValueOrThrow(attrName);
		try {
			return Double.parseDouble(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the attribute ''{1}'' in {2} is not a double", v, attrName, _path), _owner.getXMLString());
		}
	}

	public int getAttributeIntValueOrDefault(String attrName, int defaultValue) throws CCXMLException {
		String v = getAttributeValueOrDefault(attrName, null);
		if (v == null) return defaultValue;
		try {
			return Integer.parseInt(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the attribute ''{1}'' in {2} is not an integer", v, attrName, _path), _owner.getXMLString());
		}
	}

	public boolean getAttributeBoolValueOrThrow(String attrName) throws CCXMLException {
		String v = getAttributeValueOrThrow(attrName);
		try {
			return parseBool(v);
		} catch (BooleanFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the attribute ''{1}'' in {2} is not a boolean", v, attrName, _path), _owner.getXMLString());
		}
	}

	public boolean getAttributeBoolValueOrDefault(String attrName, boolean defaultValue) throws CCXMLException {
		String v = getAttributeValueOrDefault(attrName, null);
		if (v == null) return defaultValue;
		try {
			return parseBool(v);
		} catch (BooleanFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the attribute ''{1}'' in {2} is not an boolean", v, attrName, _path), _owner.getXMLString());
		}
	}

	//####################################################################################

	public String getFirstChildValueOrThrow(String name) throws CCXMLException {
		return getFirstChildOrThrow(name).getContent();
	}

	public int getFirstChildIntValueOrThrow(String name) throws CCXMLException {
		String v = getFirstChildValueOrThrow(name);
		try {
			return Integer.parseInt(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the child <{1}> in {2} is not an integer", v, name, _path), _owner.getXMLString());
		}
	}

	public int getFirstChildIntValueOrDefault(String name, int defaultValue) throws CCXMLException {
		String v = getFirstChildValueOrDefault(name, null);
		if (v == null) return defaultValue;
		try {
			return Integer.parseInt(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the child <{1}> in {2} is not an integer", v, name, _path), _owner.getXMLString());
		}
	}

	public double getFirstChildDoubleValueOrThrow(String name) throws CCXMLException {
		String v = getFirstChildValueOrThrow(name);
		try {
			return Double.parseDouble(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the child <{1}> in {2} is not a double", v, name, _path), _owner.getXMLString());
		}
	}

	public double getFirstChildDoubleValueOrDefault(String name, int defaultValue) throws CCXMLException {
		String v = getFirstChildValueOrDefault(name, null);
		if (v == null) return defaultValue;
		try {
			return Double.parseDouble(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the child <{1}> in {2} is not a double", v, name, _path), _owner.getXMLString());
		}
	}

	public long getFirstChildLongValueOrThrow(String name) throws CCXMLException {
		String v = getFirstChildValueOrThrow(name);
		try {
			return Long.parseLong(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the child <{1}> in {2} is not a long", v, name, _path), _owner.getXMLString());
		}
	}

	public long getFirstChildLongValueOrDefault(String name, int defaultValue) throws CCXMLException {
		String v = getFirstChildValueOrDefault(name, null);
		if (v == null) return defaultValue;
		try {
			return Long.parseLong(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the child <{1}> in {2} is not a long", v, name, _path), _owner.getXMLString());
		}
	}

	public String getFirstChildValueOrDefault(String name, String defaultValue) {
		CCXMLElement e = getFirstChildOrNull(name);
		if (e == null) return defaultValue;
		return e.getContent();
	}

	public String getFirstChildValueByAttrOrDefault(String nodename, String attrname, String attrval, String defaultValue) {
		CCXMLElement e = getFirstChildByAttrOrNull(nodename, attrname, attrval);
		if (e == null) return defaultValue;
		return e.getContent();
	}

	//####################################################################################

	public <TE extends Throwable>  void execIfAttrExists(String attrName, Func1to0WithGenericException<String, TE> func) throws TE {
		String v = getAttributeValueOrDefault(attrName, null);
		if (v == null) return;

		func.invoke(v);
	}

	public <TE extends Throwable> void execIfIntAttrExists(String attrName, Func1to0WithGenericException<Integer, TE> func) throws CCXMLException, TE {
		String v = getAttributeValueOrDefault(attrName, null);
		if (v == null) return;

		int cv;
		try {
			cv = Integer.parseInt(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the attribute ''{1}'' in {2} is not an integer", v, attrName, _path), _owner.getXMLString());
		}

		func.invoke(cv);
	}

	public <TE extends Throwable> void execIfShortAttrExists(String attrName, Func1to0WithGenericException<Short, TE> func) throws CCXMLException, TE {
		String v = getAttributeValueOrDefault(attrName, null);
		if (v == null) return;

		short cv;
		try {
			cv = Short.parseShort(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the attribute ''{1}'' in {2} is not an short", v, attrName, _path), _owner.getXMLString());
		}

		func.invoke(cv);
	}

	public <TE extends Throwable> void execIfLongAttrExists(String attrName, Func1to0WithGenericException<Long, TE> func) throws CCXMLException, TE {
		String v = getAttributeValueOrDefault(attrName, null);
		if (v == null) return;

		long cv;
		try {
			cv = Long.parseLong(v);
		} catch (NumberFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the attribute ''{1}'' in {2} is not a long", v, attrName, _path), _owner.getXMLString());
		}

		func.invoke(cv);
	}

	public <TE extends Throwable> void execIfBoolAttrExists(String attrName, Func1to0WithGenericException<Boolean, TE> func) throws CCXMLException, TE {
		String v = getAttributeValueOrDefault(attrName, null);
		if (v == null) return;

		boolean cv;
		try {
			cv = parseBool(v);
		} catch (BooleanFormatException e) {
			throw new CCXMLException(Str.format("The value \"{0}\" in the attribute ''{1}'' in {2} is not a long", v, attrName, _path), _owner.getXMLString());
		}

		func.invoke(cv);
	}

	//####################################################################################

	private static boolean parseBool(String v) throws BooleanFormatException {
		if (v.equals(true+"")) return true;
		if (v.equals(false+"")) return false;
		throw new BooleanFormatException(v);
	}
}
