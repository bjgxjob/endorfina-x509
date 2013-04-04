package cavani.endorfina.authority.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.security.auth.x500.X500PrivateCredential;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import cavani.endorfina.authority.api.CredentialData;
import cavani.endorfina.authority.api.CredentialModel;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class Authority
{

	@Inject
	Logger systemLog;

	@Inject
	AuthorityConfiguration config;

	@Inject
	Instance<DirectoryConnection> directory;

	@Inject
	CredentialFactory factory;

	protected String rootdn()
	{
		return config.getCredentialRootDN();
	}

	protected String dn(final String id)
	{
		return "uid=" + id + "," + rootdn();
	}

	protected X509Certificate getCertificate(final String id) throws Exception
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

	protected X500PrivateCredential authority() throws Exception
	{
		final String _key = config.getPrivatekeyFile();
		final String _cert = config.getCertificateFile();
		final char[] _pw = config.getPrivatekeyPassword().toCharArray();

		final PrivateKey key = loadAuthorityKey(_key, _pw);
		final X509Certificate cert = loadAuthorityCert(_cert);

		return new X500PrivateCredential(cert, key);
	}

	protected PrivateKey loadAuthorityKey(final String path, final char[] pw) throws Exception
	{
		try (
			FileReader file = new FileReader(path);
			PEMParser pem = new PEMParser(file))
		{
			final Object object = pem.readObject();
			final PEMDecryptorProvider dec = new JcePEMDecryptorProviderBuilder().build(pw);
			final PEMEncryptedKeyPair key = (PEMEncryptedKeyPair) object;
			final PrivateKeyInfo privInfo = key.decryptKeyPair(dec).getPrivateKeyInfo();

			final JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
			return converter.getPrivateKey(privInfo);
		}
	}

	protected X509Certificate loadAuthorityCert(final String path) throws Exception
	{
		try (
			FileReader file = new FileReader(path);
			PEMParser pem = new PEMParser(file))
		{
			final Object object = pem.readObject();
			final X509CertificateHolder holder = (X509CertificateHolder) object;

			final JcaX509CertificateConverter converter = new JcaX509CertificateConverter().setProvider("BC");
			return converter.getCertificate(holder);
		}
	}

	protected char[] generatePassword()
	{
		final String time = String.valueOf(System.currentTimeMillis());
		return time.substring(time.length() - 8).toCharArray();
	}

	protected KeyPair generateKeys() throws Exception
	{
		final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");

		kpg.initialize(2048, new SecureRandom());

		return kpg.generateKeyPair();
	}

	protected byte[] pkcs12(final String id, final char[] pw, final X500PrivateCredential credential, final X509Certificate issuerCertificate) throws Exception
	{
		final KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");

		keyStore.load(null, null);

		keyStore.setKeyEntry(id, credential.getPrivateKey(), pw, new Certificate[]
		{
			credential.getCertificate(),
			issuerCertificate
		});

		try (
			ByteArrayOutputStream out = new ByteArrayOutputStream())
		{
			keyStore.store(out, pw);
			return out.toByteArray();
		}
	}

	protected void generate(final String id) throws Exception
	{
		if (id == null)
		{
			return;
		}

		final KeyPair keys = generateKeys();

		final X500PrivateCredential authority = authority();

		final X500PrivateCredential credential = factory.createCredential(id, authority, keys);

		final char[] pw = generatePassword();
		final byte[] p12 = pkcs12(id, pw, credential, authority.getCertificate());
		final byte[] cert = credential.getCertificate().getEncoded();

		systemLog.info(id + "/pw = " + String.valueOf(pw));
		systemLog.info(id + "/p12 = " + p12.length);
		systemLog.info(id + "/cert = " + cert.length);

		final String dn = dn(id);

		final CredentialEntry entry = new CredentialEntry(id);

		entry.password(pw);
		entry.pkcs12(p12);
		entry.certificate(cert);

		directory.get().persist(dn, entry);

		systemLog.info(id + "/dn = " + dn);
	}

	public boolean validate(final String id, final X509Certificate certificate) throws Exception
	{
		if (id == null || id.trim().isEmpty() || certificate == null)
		{
			return false;
		}

		final X509Certificate _certificate = getCertificate(id);

		return _certificate != null && certificate.equals(_certificate);
	}

	@Asynchronous
	public void request(final String id)
	{
		try
		{
			systemLog.info("Gerando credencial: " + id);

			generate(id);

			systemLog.info("Credencial gerada: " + id);
		}
		catch (final Throwable e)
		{
			systemLog.log(Level.INFO, "Erro criando credencial: " + id, e);
		}
	}

	public void revoke(final String id) throws Exception
	{
		directory.get().remove(dn(id));
	}

	public byte[] raw(final String id) throws Exception
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

}
