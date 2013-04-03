package cavani.endorfina.authority.tools;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public final class ServerCredential
{

	private ServerCredential()
	{
	}

	public static void main(final String[] args) throws Exception
	{
		System.out.println("Server Credential (SSL) generator!");

		bc();

		final String hostname = "appserver";

		final X500Principal principal = new X500Principal("CN=" + hostname + ", OU=Endorfina, O=Cavani");

		final KeyPair keys = generateKeys("RSA", 2048);

		final X500PrivateCredential issuer = loadPKCS12("Authority.p12", "secret".toCharArray(), "Authority");
		final X500PrivateCredential credential = createCredential(principal, keys, "SHA1withRSA", issuer);

		storeJKS("server", credential, issuer.getCertificate(), "secret".toCharArray());

		System.out.println("done!");
	}

	static void bc()
	{
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
		{
			final Provider provider = new BouncyCastleProvider();
			Security.addProvider(provider);
		}
	}

	static KeyPair generateKeys(final String algorithm, final int size) throws Exception
	{
		final KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm, BouncyCastleProvider.PROVIDER_NAME);

		kpg.initialize(size, new SecureRandom());

		return kpg.generateKeyPair();
	}

	static X500PrivateCredential createCredential(final X500Principal principal, final KeyPair keys, final String signatureAlgorithm, final X500PrivateCredential issuer) throws Exception
	{
		final X500Principal issuerPrincipal = issuer.getCertificate().getSubjectX500Principal();

		final BigInteger serial = BigInteger.valueOf(System.nanoTime());

		final Calendar date = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		date.add(Calendar.DAY_OF_YEAR, -1);

		final Date startDate = date.getTime();

		date.add(Calendar.YEAR, 1);

		final Date endDate = date.getTime();

		final X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(issuerPrincipal, serial, startDate, endDate, principal, keys.getPublic());

		certificateBuilder.addExtension(X509Extension.basicConstraints, false, new BasicConstraints(false));

		final JcaX509ExtensionUtils x509Extension = new JcaX509ExtensionUtils();
		certificateBuilder.addExtension(X509Extension.subjectKeyIdentifier, false, x509Extension.createSubjectKeyIdentifier(keys.getPublic()));
		certificateBuilder.addExtension(X509Extension.authorityKeyIdentifier, false, x509Extension.createAuthorityKeyIdentifier(issuer.getCertificate()));

		final ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).setProvider(BouncyCastleProvider.PROVIDER_NAME).build(issuer.getPrivateKey());

		final X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

		final X509Certificate certificate = new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getCertificate(certificateHolder);

		return new X500PrivateCredential(certificate, keys.getPrivate());
	}

	static X500PrivateCredential loadPKCS12(final String file, final char[] pw, final String id) throws Exception
	{
		final KeyStore keyStore = KeyStore.getInstance("PKCS12", BouncyCastleProvider.PROVIDER_NAME);

		try (
			InputStream in = Files.newInputStream(Paths.get(file)))
		{
			keyStore.load(in, pw);
		}

		final PrivateKey key = (PrivateKey) keyStore.getKey(id, pw);
		final X509Certificate cert = (X509Certificate) keyStore.getCertificate(id);

		return new X500PrivateCredential(cert, key);
	}

	static void storeJKS(final String alias, final X500PrivateCredential credential, final X509Certificate issuerCertificate, final char[] pw) throws Exception
	{
		final KeyStore keyStore = KeyStore.getInstance("JKS");

		keyStore.load(null, null);

		keyStore.setKeyEntry(alias, credential.getPrivateKey(), pw, new Certificate[]
		{
			credential.getCertificate(),
			issuerCertificate
		});

		try (
			OutputStream out = Files.newOutputStream(Paths.get("keystore.jks")))
		{
			keyStore.store(out, pw);
		}
		final KeyStore trustStore = KeyStore.getInstance("JKS");

		trustStore.load(null, null);

		trustStore.setCertificateEntry(alias + "_issuer", issuerCertificate);

		try (
			OutputStream out = Files.newOutputStream(Paths.get("truststore.jks")))
		{
			trustStore.store(out, pw);
		}
	}

}
