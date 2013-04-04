package cavani.endorfina.authority.tools;

import java.io.OutputStream;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
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
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public final class AuthoritySetup
{

	private AuthoritySetup()
	{
	}

	public static void main(final String[] args) throws Exception
	{
		System.out.println("Authority key-pair generator!");

		bc();

		final String cn = "Authority";

		final X500Principal principal = new X500Principal("CN=" + cn + ", OU=Endorfina, O=Cavani");

		final KeyPair keys = generateKeys("RSA", 2048);

		final X500PrivateCredential credential = createAuthority(principal, keys, "SHA1withRSA");

		storePKCS12(cn, credential, cn + ".p12", "secret".toCharArray());
		storePEM(credential.getCertificate(), "cacert.pem");
		storePEM(credential.getPrivateKey(), "cakey.pem", "secret".toCharArray());

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

	static X500PrivateCredential createAuthority(final X500Principal principal, final KeyPair keys, final String signatureAlgorithm) throws Exception
	{
		final BigInteger serial = BigInteger.valueOf(System.nanoTime());

		final Calendar date = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		date.add(Calendar.DAY_OF_YEAR, -1);

		final Date startDate = date.getTime();

		date.add(Calendar.YEAR, 10);

		final Date endDate = date.getTime();

		final X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(principal, serial, startDate, endDate, principal, keys.getPublic());

		final JcaX509ExtensionUtils x509Extension = new JcaX509ExtensionUtils();
		certificateBuilder.addExtension(X509Extension.subjectKeyIdentifier, false, x509Extension.createSubjectKeyIdentifier(keys.getPublic()));
		certificateBuilder.addExtension(X509Extension.authorityKeyIdentifier, false, x509Extension.createAuthorityKeyIdentifier(keys.getPublic()));

		certificateBuilder.addExtension(X509Extension.basicConstraints, false, new BasicConstraints(true));

		final ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).setProvider(BouncyCastleProvider.PROVIDER_NAME).build(keys.getPrivate());

		final X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

		final X509Certificate certificate = new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getCertificate(certificateHolder);

		return new X500PrivateCredential(certificate, keys.getPrivate());
	}

	static void storePKCS12(final String alias, final X500PrivateCredential credential, final String file, final char[] pw) throws Exception
	{
		final KeyStore keyStore = KeyStore.getInstance("PKCS12", BouncyCastleProvider.PROVIDER_NAME);

		keyStore.load(null, null);

		keyStore.setKeyEntry(alias, credential.getPrivateKey(), pw, new Certificate[]
		{
			credential.getCertificate()
		});

		try (
			OutputStream out = Files.newOutputStream(Paths.get(file)))
		{
			keyStore.store(out, pw);
		}
	}

	static void storePEM(final Object object, final String file) throws Exception
	{
		try (
			Writer out = Files.newBufferedWriter(Paths.get(file), Charset.forName("UTF-8"), StandardOpenOption.CREATE);
			PEMWriter pem = new PEMWriter(out);)
		{
			pem.writeObject(object);
		}
	}

	static void storePEM(final Object object, final String file, final char[] pw) throws Exception
	{
		try (
			Writer out = Files.newBufferedWriter(Paths.get(file), Charset.forName("UTF-8"), StandardOpenOption.CREATE);
			PEMWriter pem = new PEMWriter(out);)
		{
			final PEMEncryptor enc = new JcePEMEncryptorBuilder("DES-EDE3").build(pw);
			pem.writeObject(object, enc);

		}
	}

}
