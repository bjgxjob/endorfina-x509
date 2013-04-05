package cavani.endorfina.authority.core.auth;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.inject.Inject;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import cavani.endorfina.authority.core.data.CredentialStore;

public class AuthorityAuthenticator
{

	private static final String CERTIFICATE_TYPE_X509 = "X.509";

	@Inject
	CredentialStore store;

	public boolean validate(final String id, final X509Certificate certificate) throws Exception
	{
		if (id == null || id.trim().isEmpty() || certificate == null)
		{
			return false;
		}

		final X509Certificate _certificate = getCertificate(id);

		return _certificate != null && certificate.equals(_certificate);
	}

	X509Certificate getCertificate(final String id) throws Exception
	{
		final byte[] raw = store.getCertificate(id);
		try (
			InputStream in = new ByteArrayInputStream(raw))
		{
			final CertificateFactory factory = CertificateFactory.getInstance(CERTIFICATE_TYPE_X509, BouncyCastleProvider.PROVIDER_NAME);
			return (X509Certificate) factory.generateCertificate(in);
		}

	}
}
