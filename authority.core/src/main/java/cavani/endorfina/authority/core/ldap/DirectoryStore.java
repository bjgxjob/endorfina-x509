package cavani.endorfina.authority.core.ldap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import cavani.endorfina.authority.api.model.CredentialData;
import cavani.endorfina.authority.api.model.CredentialModel;
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
	public List<CredentialData> list() throws Exception
	{
		final List<Map<String, Object>> list = directory.get().search(rootdn(), new String[]
		{
			CredentialModel.ID.value,
			CredentialModel.PW.value,
			CredentialModel.CREATED.value
		});

		final List<CredentialData> data = new ArrayList<>(list.size());
		for (final Map<String, Object> value : list)
		{
			final String id = (String) value.get(CredentialModel.ID.value);
			final String pw = new String((byte[]) value.get(CredentialModel.PW.value));
			final String created = (String) value.get(CredentialModel.CREATED.value);

			data.add(new CredentialData(id, pw, created));
		}

		return Collections.unmodifiableList(data);
	}

	@Override
	public X509Certificate getCertificate(final String id) throws Exception
	{
		final byte[] result = (byte[]) directory.get().query(new DirectoryQuery(dn(id), CredentialModel.CERTIFICATE.value)
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

		try (
			InputStream in = new ByteArrayInputStream(result))
		{
			final CertificateFactory factory = CertificateFactory.getInstance("X.509", "BC");
			return (X509Certificate) factory.generateCertificate(in);
		}
	}

	@Override
	public byte[] getP12Raw(final String id) throws Exception
	{
		return (byte[]) directory.get().query(new DirectoryQuery(dn(id), CredentialModel.PKCS12.value)
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

}
