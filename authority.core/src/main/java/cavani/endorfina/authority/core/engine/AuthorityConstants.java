package cavani.endorfina.authority.core.engine;

public final class AuthorityConstants
{

	public static final String AUTHORITY_CONFIG_CERTIFICATE_FILE = "certificate_file";

	public static final String AUTHORITY_CONFIG_PRIVATEKEY_FILE = "privatekey_file";

	public static final String AUTHORITY_CONFIG_PRIVATEKEY_PASSWORD = "privatekey_password";

	public static final String CREDENTIAL_KEY_ALGORITHM = "RSA";

	public static final int CREDENTIAL_KEY_SIZE = 2048;

	public static final String CREDENTIAL_PRINCIPAL_FORMAT = "CN=%s, OU=Endorfina, O=Cavani";

	public static final String CREDENTIAL_SIGNATURE_ALGORITHM = "SHA1withRSA";

	public static final String CREDENTIAL_KEYSTORE_TYPE_PKCS12 = "PKCS12";

	private AuthorityConstants()
	{
	}

}
