package cavani.endorfina.authority.core.auth;

import java.security.cert.X509Certificate;

import javax.inject.Inject;

import cavani.endorfina.authority.core.data.CredentialStore;

public class AuthorityAuthenticator
{

	@Inject
	CredentialStore store;

	public boolean validate(final String id, final X509Certificate certificate) throws Exception
	{
		if (id == null || id.trim().isEmpty() || certificate == null)
		{
			return false;
		}

		final X509Certificate _certificate = store.getCertificate(id);

		return _certificate != null && certificate.equals(_certificate);
	}

}
