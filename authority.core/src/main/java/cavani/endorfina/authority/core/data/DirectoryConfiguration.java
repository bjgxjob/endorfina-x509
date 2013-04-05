package cavani.endorfina.authority.core.data;

import static cavani.endorfina.authority.core.data.DirectoryConstants.DIRECTORY_CONFIG_BINDDN;
import static cavani.endorfina.authority.core.data.DirectoryConstants.DIRECTORY_CONFIG_BINDPW;
import static cavani.endorfina.authority.core.data.DirectoryConstants.DIRECTORY_CONFIG_CREDENTIAL_ROOT_DN;
import static cavani.endorfina.authority.core.data.DirectoryConstants.DIRECTORY_CONFIG_URL;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;

@Singleton
@Startup
@ApplicationScoped
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class DirectoryConfiguration
{

	private static final String DEFAULT_CONFIG_PROPERTIES = "META-INF/directory.properties";

	String url;

	String binddn;

	String bindpw;

	String credentialRootDN;

	@PostConstruct
	void setup() throws Exception
	{
		final Properties config = new Properties();
		try (
			InputStream in = getConfigProperties())
		{
			config.load(in);
		}
		url = config.getProperty(DIRECTORY_CONFIG_URL);
		binddn = config.getProperty(DIRECTORY_CONFIG_BINDDN);
		bindpw = config.getProperty(DIRECTORY_CONFIG_BINDPW);
		credentialRootDN = config.getProperty(DIRECTORY_CONFIG_CREDENTIAL_ROOT_DN);
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
