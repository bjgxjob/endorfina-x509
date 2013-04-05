package cavani.endorfina.authority.core.ldap;


public class CredentialValue extends AbstractValue
{

	public static final String CREDENTIAL_ID = "uid";

	public static final String CREDENTIAL_PW = "userPassword";

	public static final String CREDENTIAL_PKCS12 = "userPKCS12";

	public static final String CREDENTIAL_CERTIFICATE = "userCertificate;binary";

	public static final String CREDENTIAL_CREATED = "createTimestamp";

	public CredentialValue(final String id)
	{
		objectClasses.add("extensibleObject");
		objectClasses.add("account");
		objectClasses.add("top");

		attributes.put(objectClasses);
		attributes.put(CREDENTIAL_ID, id);
	}

	public void password(final char[] value)
	{
		attributes.put(CREDENTIAL_PW, new String(value));
	}

	public void pkcs12(final byte[] value)
	{
		attributes.put(CREDENTIAL_PKCS12, value);
	}

	public void certificate(final byte[] value)
	{
		attributes.put(CREDENTIAL_CERTIFICATE, value);
	}

}
