package cavani.endorfina.authority.core.data;

public abstract class DirectoryQuery
{

	final String dn;

	final String[] attrs;

	public DirectoryQuery(final String dn, final String... attrs)
	{
		this.dn = dn;
		this.attrs = attrs;
	}

	public abstract boolean eval(String id, Object value);

	public abstract Object result();

}
