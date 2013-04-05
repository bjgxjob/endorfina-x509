package cavani.endorfina.authority.api.model;

import java.io.Serializable;

public class CredentialData implements Serializable
{

	private static final long serialVersionUID = 1L;

	String id;

	String pw;

	String created;

	public CredentialData()
	{
	}

	public CredentialData(final String id, final String pw, final String created)
	{
		this.id = id;
		this.pw = pw;
		this.created = created;
	}

	public String getId()
	{
		return id;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public String getPw()
	{
		return pw;
	}

	public void setPw(final String pw)
	{
		this.pw = pw;
	}

	public String getCreated()
	{
		return created;
	}

	public void setCreated(final String created)
	{
		this.created = created;
	}

}
