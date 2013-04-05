package cavani.endorfina.authority.core.ldap;

import static cavani.endorfina.authority.core.ldap.CredentialValue.CREDENTIAL_CERTIFICATE;
import static cavani.endorfina.authority.core.ldap.CredentialValue.CREDENTIAL_CREATED;
import static cavani.endorfina.authority.core.ldap.CredentialValue.CREDENTIAL_ID;
import static cavani.endorfina.authority.core.ldap.CredentialValue.CREDENTIAL_PKCS12;
import static cavani.endorfina.authority.core.ldap.CredentialValue.CREDENTIAL_PW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import cavani.endorfina.authority.api.model.CredentialData;
import cavani.endorfina.authority.core.data.CredentialStore;

public class DirectoryStore implements CredentialStore
{

	@Inject
	DirectoryConfiguration config;

	@Inject
	Instance<DirectoryConnection> directory;

	private String rootdn()
	{
		return config.getCredentialRootDN();
	}

	private String dn(final String id)
	{
		return "uid=" + id + "," + rootdn();
	}

	@Override
	public String persist(final String id, final byte[] p12, final byte[] cert, final char[] pw) throws Exception
	{
		final String dn = dn(id);

		final CredentialValue entry = new CredentialValue(id);

		entry.password(pw);
		entry.pkcs12(p12);
		entry.certificate(cert);

		directory.get().persist(dn, entry);

		return dn;
	}

	@Override
	public String remove(final String id) throws Exception
	{
		final String dn = dn(id);
		directory.get().remove(dn);
		return dn;
	}

	@Override
	public CredentialData getData(final String id) throws Exception
	{
		return (CredentialData) directory.get().query(new DirectoryQuery(dn(id), new String[]
		{
			CREDENTIAL_ID,
			CREDENTIAL_PW,
			CREDENTIAL_CREATED
		})
		{

			CredentialData data = null;

			CredentialData data()
			{
				return data == null ? (data = new CredentialData()) : data;
			}

			@Override
			public boolean eval(final String id, final Object value)
			{
				switch (id)
				{
					case CREDENTIAL_ID:
						data().setId((String) value);
						break;
					case CREDENTIAL_PW:
						data().setPw(new String((byte[]) value));
						break;
					case CREDENTIAL_CREATED:
						data().setCreated((String) value);
						break;
				}

				return true;
			}

			@Override
			public Object result()
			{
				return data;
			}

		});
	}

	@Override
	public byte[] getCertificate(final String id) throws Exception
	{
		return (byte[]) directory.get().query(new DirectoryQuery(dn(id), CREDENTIAL_CERTIFICATE)
		{

			byte[] raw;

			@Override
			public boolean eval(final String id, final Object value)
			{
				if (value instanceof byte[])
				{
					raw = (byte[]) value;
					return false;
				}

				return true;
			}

			@Override
			public Object result()
			{
				return raw;
			}

		});
	}

	@Override
	public byte[] getP12(final String id) throws Exception
	{
		return (byte[]) directory.get().query(new DirectoryQuery(dn(id), CREDENTIAL_PKCS12)
		{

			byte[] raw;

			@Override
			public boolean eval(final String id, final Object value)
			{
				if (value instanceof byte[])
				{
					raw = (byte[]) value;
					return false;
				}

				return true;
			}

			@Override
			public Object result()
			{
				return raw;
			}

		});
	}

	@Override
	public List<CredentialData> list() throws Exception
	{
		final List<Map<String, Object>> list = directory.get().search(rootdn(), new String[]
		{
			CREDENTIAL_ID,
			CREDENTIAL_PW,
			CREDENTIAL_CREATED
		});

		final List<CredentialData> data = new ArrayList<>(list.size());
		for (final Map<String, Object> value : list)
		{
			final String id = (String) value.get(CREDENTIAL_ID);
			final String pw = new String((byte[]) value.get(CREDENTIAL_PW));
			final String created = (String) value.get(CREDENTIAL_CREATED);

			data.add(new CredentialData(id, pw, created));
		}

		return Collections.unmodifiableList(data);
	}

}
