package cavani.endorfina.authority.core.ldap;

import cavani.endorfina.authority.api.model.CredentialModel;

public class CredentialValue extends AbstractValue
{

	public CredentialValue(final String id)
	{
		objectClasses.add("extensibleObject");
		objectClasses.add("account");
		objectClasses.add("top");

		attributes.put(objectClasses);
		attributes.put(CredentialModel.ID.value, id);
	}

	public void password(final char[] value)
	{
		attributes.put(CredentialModel.PW.value, new String(value));
	}

	public void pkcs12(final byte[] value)
	{
		attributes.put(CredentialModel.PKCS12.value, value);
	}

	public void certificate(final byte[] value)
	{
		attributes.put(CredentialModel.CERTIFICATE.value, value);
	}

}
