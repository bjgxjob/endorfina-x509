package cavani.endorfina.authority.tools;

import java.io.FileReader;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

public class PEMReadTest
{

	private PEMReadTest()
	{
	}

	public static void main(final String[] args) throws Exception
	{
		bc();
		key();
		cert();
	}

	static void bc()
	{
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
		{
			final Provider provider = new BouncyCastleProvider();
			Security.addProvider(provider);
		}
	}

	static void key() throws Exception
	{
		try (
			FileReader file = new FileReader("cakey.pem");
			PEMParser pem = new PEMParser(file))
		{
			final Object object = pem.readObject();
			final PEMDecryptorProvider dec = new JcePEMDecryptorProviderBuilder().build("secret".toCharArray());
			final PEMEncryptedKeyPair key = (PEMEncryptedKeyPair) object;
			final PrivateKeyInfo privInfo = key.decryptKeyPair(dec).getPrivateKeyInfo();

			final JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
			final PrivateKey priv = converter.getPrivateKey(privInfo);

			System.out.println(priv);
		}
	}

	static void cert() throws Exception
	{
		try (
			FileReader file = new FileReader("cacert.pem");
			PEMParser pem = new PEMParser(file))
		{
			final Object object = pem.readObject();
			final X509CertificateHolder holder = (X509CertificateHolder) object;

			final JcaX509CertificateConverter converter = new JcaX509CertificateConverter().setProvider("BC");
			final X509Certificate cert = converter.getCertificate(holder);

			System.out.println(cert);
		}
	}
}
