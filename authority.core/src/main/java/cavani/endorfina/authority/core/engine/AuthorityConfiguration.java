package cavani.endorfina.authority.core.engine;

import static cavani.endorfina.authority.core.engine.AuthorityConstants.AUTHORITY_CONFIG_CERTIFICATE_FILE;
import static cavani.endorfina.authority.core.engine.AuthorityConstants.AUTHORITY_CONFIG_PRIVATEKEY_FILE;
import static cavani.endorfina.authority.core.engine.AuthorityConstants.AUTHORITY_CONFIG_PRIVATEKEY_PASSWORD;

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
public class AuthorityConfiguration
{

	private static final String DEFAULT_CONFIG_PROPERTIES = "META-INF/authority.properties";

	@Inject
	Logger systemLog;

	String certificateFile;

	String privatekeyFile;

	String privatekeyPassword;

	@PostConstruct
	void setup() throws Exception
	{
		systemLog.info("AuthorityConfiguration setup...");

		final Properties config = new Properties();
		try (
			InputStream in = getConfigProperties())
		{
			config.load(in);
		}
		certificateFile = getString(config, AUTHORITY_CONFIG_CERTIFICATE_FILE);
		privatekeyFile = getString(config, AUTHORITY_CONFIG_PRIVATEKEY_FILE);
		privatekeyPassword = getString(config, AUTHORITY_CONFIG_PRIVATEKEY_PASSWORD);
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

	public String getCertificateFile()
	{
		return certificateFile;
	}

	public String getPrivatekeyFile()
	{
		return privatekeyFile;
	}

	public String getPrivatekeyPassword()
	{
		return privatekeyPassword;
	}

}
