package cavani.endorfina.authority.core.engine;

import java.io.FileReader;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.inject.Inject;
import javax.security.auth.x500.X500PrivateCredential;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

public class AuthorityCredential
{

	@Inject
	AuthorityConfiguration config;

	public X500PrivateCredential getCredential() throws Exception
	{
		final String _key = config.getPrivatekeyFile();
		final String _cert = config.getCertificateFile();
		final char[] _pw = config.getPrivatekeyPassword().toCharArray();

		final PrivateKey key = loadAuthorityKey(_key, _pw);
		final X509Certificate cert = loadAuthorityCert(_cert);

		return new X500PrivateCredential(cert, key);
	}

	private PrivateKey loadAuthorityKey(final String path, final char[] pw) throws Exception
	{
		try (
			FileReader file = new FileReader(path);
			PEMParser pem = new PEMParser(file))
		{
			final Object object = pem.readObject();
			final PEMDecryptorProvider dec = new JcePEMDecryptorProviderBuilder().build(pw);
			final PEMEncryptedKeyPair key = (PEMEncryptedKeyPair) object;
			final PrivateKeyInfo privInfo = key.decryptKeyPair(dec).getPrivateKeyInfo();

			final JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
			return converter.getPrivateKey(privInfo);
		}
	}

	private X509Certificate loadAuthorityCert(final String path) throws Exception
	{
		try (
			FileReader file = new FileReader(path);
			PEMParser pem = new PEMParser(file))
		{
			final Object object = pem.readObject();
			final X509CertificateHolder holder = (X509CertificateHolder) object;

			final JcaX509CertificateConverter converter = new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
			return converter.getCertificate(holder);
		}
	}

}
