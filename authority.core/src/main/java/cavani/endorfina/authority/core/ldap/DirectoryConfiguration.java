package cavani.endorfina.authority.core.ldap;

import static cavani.endorfina.authority.core.ldap.DirectoryConstants.DIRECTORY_CONFIG_BINDDN;
import static cavani.endorfina.authority.core.ldap.DirectoryConstants.DIRECTORY_CONFIG_BINDPW;
import static cavani.endorfina.authority.core.ldap.DirectoryConstants.DIRECTORY_CONFIG_CREDENTIAL_ROOT_DN;
import static cavani.endorfina.authority.core.ldap.DirectoryConstants.DIRECTORY_CONFIG_URL;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Singleton
@Startup
@ApplicationScoped
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class DirectoryConfiguration
{

	private static final String DEFAULT_CONFIG_PROPERTIES = "META-INF/directory.properties";

	@Inject
	Logger systemLog;

	String url;

	String binddn;

	String bindpw;

	String credentialRootDN;

	@PostConstruct
	void setup() throws Exception
	{
		systemLog.info("DirectoryConfiguration setup...");

		final Properties config = new Properties();
		try (
			InputStream in = getConfigProperties())
		{
			config.load(in);
		}
		url = getString(config, DIRECTORY_CONFIG_URL);
		binddn = getString(config, DIRECTORY_CONFIG_BINDDN);
		bindpw = getString(config, DIRECTORY_CONFIG_BINDPW);
		credentialRootDN = getString(config, DIRECTORY_CONFIG_CREDENTIAL_ROOT_DN);
	}

	String getString(final Properties config, final String key)
	{
		final String value = config.getProperty(key);
		systemLog.info(key + "=" + value);
		return value;
	}

	InputStream getConfigProperties() throws IOException
	{
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_CONFIG_PROPERTIES);
	}

	public String getUrl()
	{
		return url;
	}

	public String getBinddn()
	{
		return binddn;
	}

	public String getBindpw()
	{
		return bindpw;
	}

	public String getCredentialRootDN()
	{
		return credentialRootDN;
	}

}
