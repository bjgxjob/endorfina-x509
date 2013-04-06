package cavani.endorfina.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class Main
{

	private Main()
	{
	}

	public static void main(final String[] args) throws Exception
	{
		System.out.println("Client Application!");

		bc();

		final String p12 = args[0];
		final String pw = args[1];

		System.out.println("P12: " + p12);
		System.out.println("PW: " + pw);

		final URL url = new URL("https://127.0.0.1:8443/server/Service");

		final HttpsURLConnection cx = (HttpsURLConnection) url.openConnection();
		ssl(cx, p12, pw.toCharArray());

		try (
			final InputStream in = cx.getInputStream();
			final InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
			final BufferedReader reader = new BufferedReader(inReader))
		{

			for (String line = null; (line = reader.readLine()) != null;)
			{
				System.out.println("[Received] " + line);
			}
		}
	}

	static void bc()
	{
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
		{
			final Provider provider = new BouncyCastleProvider();
			Security.addProvider(provider);
		}
	}

	static void ssl(final HttpsURLConnection cx, final String id, final char[] pw) throws Exception
	{
		final KeyStore credential = credential(id, pw);

		final KeyManager[] kms = kms(credential, pw);

		final TrustManager[] tms = tms();

		final SSLContext context = SSLContext.getInstance("TLS");
		context.init(kms, tms, null);

		final SSLSocketFactory factory = context.getSocketFactory();
		cx.setSSLSocketFactory(factory);

		cx.setHostnameVerifier(new HostnameVerifier()
		{

			@Override
			public boolean verify(final String hostname, final SSLSession session)
			{
				return true;
			}

		});
	}

	static KeyManager[] kms(final KeyStore credential, final char[] pw) throws Exception
	{
		final String kmfx = KeyManagerFactory.getDefaultAlgorithm();
		final KeyManagerFactory kmf = KeyManagerFactory.getInstance(kmfx);
		kmf.init(credential, pw);

		return kmf.getKeyManagers();
	}

	static TrustManager[] tms()
	{
		final X509TrustManager xm = new X509TrustManager()
		{

			@Override
			public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException
			{
			}

			@Override
			public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException
			{
			}

			@Override
			public X509Certificate[] getAcceptedIssuers()
			{
				return null;
			}

		};

		return new TrustManager[]
		{
			xm,
		};
	}

	static KeyStore credential(final String path, final char[] secret) throws Exception
	{
		final KeyStore keyStore = KeyStore.getInstance("PKCS12", BouncyCastleProvider.PROVIDER_NAME);
		try (
			FileInputStream in = new FileInputStream(path))
		{
			keyStore.load(in, secret);
		}
		return keyStore;
	}

}
