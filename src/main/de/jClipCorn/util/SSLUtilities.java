package de.jClipCorn.util;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This class provide various static methods that relax X509 certificate and
 * hostname verification while using the SSL over the HTTP protocol.
 *
 * https://en.wikibooks.org/wiki/WebObjects/Web_Services/How_to_Trust_Any_SSL_Certificate
 *
 * @author    Francis Labrie
 */
public final class SSLUtilities {

	private static com.sun.net.ssl.HostnameVerifier __hostnameVerifier;
	private static com.sun.net.ssl.TrustManager[] __trustManagers;
	private static HostnameVerifier _hostnameVerifier;
	private static TrustManager[] _trustManagers;

	private static void __trustAllHostnames() {
		if(__hostnameVerifier == null) __hostnameVerifier = new _FakeHostnameVerifier();
		com.sun.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(__hostnameVerifier);
	}

	private static void __trustAllHttpsCertificates() {
		com.sun.net.ssl.SSLContext context;

		if(__trustManagers == null) __trustManagers = new com.sun.net.ssl.TrustManager[]{new _FakeX509TrustManager()};

		try {
			context = com.sun.net.ssl.SSLContext.getInstance("SSL");
			context.init(null, __trustManagers, new SecureRandom());
		} catch(GeneralSecurityException gse) {
			throw new IllegalStateException(gse.getMessage());
		}
		com.sun.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	}

	private static boolean isDeprecatedSSLProtocol() {
		return("com.sun.net.ssl.internal.www.protocol".equals(System.getProperty("java.protocol.handler.pkgs")));
	}

	private static void _trustAllHostnames() {
		if(_hostnameVerifier == null) _hostnameVerifier = new FakeHostnameVerifier();
		HttpsURLConnection.setDefaultHostnameVerifier(_hostnameVerifier);
	}

	private static void _trustAllHttpsCertificates() {
		SSLContext context;

		if(_trustManagers == null) _trustManagers = new TrustManager[] {new FakeX509TrustManager()};

		try {
			context = SSLContext.getInstance("SSL");
			context.init(null, _trustManagers, new SecureRandom());
		} catch(GeneralSecurityException gse) {
			throw new IllegalStateException(gse.getMessage());
		}

		HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	}

	public static void trustAllHostnames() {
		if(isDeprecatedSSLProtocol()) {
			__trustAllHostnames();
		} else {
			_trustAllHostnames();
		}
	}

	public static void trustAllHttpsCertificates() {
		if(isDeprecatedSSLProtocol()) {
			__trustAllHttpsCertificates();
		} else {
			_trustAllHttpsCertificates();
		}
	}

	public static class _FakeHostnameVerifier implements com.sun.net.ssl.HostnameVerifier {

		public boolean verify(String hostname, String session) {
			return(true);
		}
	}

	public static class _FakeX509TrustManager implements com.sun.net.ssl.X509TrustManager {

		private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

		public boolean isClientTrusted(X509Certificate[] chain) {
			return true;
		}

		public boolean isServerTrusted(X509Certificate[] chain) {
			return true;
		}

		public X509Certificate[] getAcceptedIssuers() {
			return(_AcceptedIssuers);
		}
	}

	public static class FakeHostnameVerifier implements HostnameVerifier {

		public boolean verify(String hostname, javax.net.ssl.SSLSession session) {
			return true;
		}
	}

	public static class FakeX509TrustManager implements X509TrustManager {

		private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

		public void checkClientTrusted(X509Certificate[] chain, String authType) {
			// .
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) {
			// .
		}

		public X509Certificate[] getAcceptedIssuers() {
			return(_AcceptedIssuers);
		}
	}
}