package cavani.endorfina.authority.core.auth;

import java.security.cert.X509Certificate;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import cavani.endorfina.authority.api.auth.AuthenticationService;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AuthenticationServiceBean implements AuthenticationService
{

	@Inject
	AuthorityAuthenticator authenticator;

	@Override
	public boolean validateCredential(final String id, final X509Certificate certificate)
	{
		try
		{
			return authenticator.validate(id, certificate);
		}
		catch (final Exception e)
		{
			throw new EJBException("Erro validando credencial!", e);
		}
	}

}
