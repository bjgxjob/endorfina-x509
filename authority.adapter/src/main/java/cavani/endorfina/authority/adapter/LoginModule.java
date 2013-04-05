package cavani.endorfina.authority.adapter;

import java.security.Principal;
import java.security.acl.Group;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.spi.BaseCertLoginModule;

import cavani.endorfina.authority.api.auth.AuthenticationService;

public class LoginModule extends BaseCertLoginModule
{

	private static final String AUTHENTICATION_SERVICE = "java:global/authority.core-1.0/AuthenticationServiceBean!cavani.endorfina.authority.api.auth.AuthenticationService";

	Logger systemLog = Logger.getLogger(LoginModule.class.getName());

	@Override
	protected boolean validateCredential(final String alias, final X509Certificate cert)
	{
		try (
			JndiConnection jndi = new JndiConnection())
		{
			final AuthenticationService auth = jndi.lookup(AuthenticationService.class, AUTHENTICATION_SERVICE);
			return auth.validateCredential(alias, cert);
		}
		catch (final Exception e)
		{
			systemLog.log(Level.INFO, e.getMessage(), e);
		}

		return false;
	}

	@Override
	protected Group[] getRoleSets() throws LoginException
	{
		try
		{
			final Group group = new SimpleGroup("Roles");

			final Principal principal = createIdentity("user");
			group.addMember(principal);

			return new Group[]
			{
				group
			};
		}
		catch (final Exception e)
		{
			throw new LoginException(e.getMessage());
		}
	}

	@Override
	protected Object[] getAliasAndCert() throws LoginException
	{
		final Object[] data = super.getAliasAndCert();
		final String name = (String) data[0];

		final int i = name.toLowerCase().indexOf("cn=");
		final int j = name.indexOf(",", i);

		final String _name = name.substring(i + 3, j).trim();

		data[0] = _name;

		return data;
	}

}
