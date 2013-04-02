package cavani.endorfina.authority.api;

public enum CredentialModel
{

	ID("uid"),
	PW("userPassword"),
	PKCS12("userPKCS12"),
	CERTIFICATE("userCertificate;binary"),
	CREATED("createTimestamp");

	public final String value;

	private CredentialModel(final String value)
	{
		this.value = value;
	}

}
