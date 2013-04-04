package cavani.endorfina.authority.core;

import static cavani.endorfina.authority.core.AuthorityConstants.AUTHORITY_CONFIG_CERTIFICATE_FILE;
import static cavani.endorfina.authority.core.AuthorityConstants.AUTHORITY_CONFIG_CREDENTIAL_ROOT_DN;
import static cavani.endorfina.authority.core.AuthorityConstants.AUTHORITY_CONFIG_PRIVATEKEY_FILE;
import static cavani.endorfina.authority.core.AuthorityConstants.AUTHORITY_CONFIG_PRIVATEKEY_PASSWORD;

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
public class AuthorityConfiguration
{

	private static final String DEFAULT_CONFIG_PROPERTIES = "META-INF/authority.properties";

	String certificateFile;

	String privatekeyFile;

	String privatekeyPassword;

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
		certificateFile = config.getProperty(AUTHORITY_CONFIG_CERTIFICATE_FILE);
		privatekeyFile = config.getProperty(AUTHORITY_CONFIG_PRIVATEKEY_FILE);
		privatekeyPassword = config.getProperty(AUTHORITY_CONFIG_PRIVATEKEY_PASSWORD);
		credentialRootDN = config.getProperty(AUTHORITY_CONFIG_CREDENTIAL_ROOT_DN);
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

	public String getCredentialRootDN()
	{
		return credentialRootDN;
	}

}
