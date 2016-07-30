package de.jClipCorn.util.exceptions;

public class HTTPErrorCodeException extends Exception {
	private static final long serialVersionUID = 1L;

	public final int Errorcode;
	
	public HTTPErrorCodeException(int c) {
		super("Server returned HTTP response code: " + c); //$NON-NLS-1$
		Errorcode = c;
	}
}
