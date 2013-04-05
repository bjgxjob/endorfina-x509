package cavani.endorfina.authority.core.engine;

import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.security.auth.x500.X500PrivateCredential;

import cavani.endorfina.authority.core.data.CredentialStore;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AuthorityEngine
{

	@Inject
	Logger systemLog;

	@Inject
	AuthorityCredential authorityCredential;

	@Inject
	CredentialStore credentialStore;

	@Inject
	CredentialFactory credentialFactory;

	private char[] generatePassword()
	{
		final String time = String.valueOf(System.currentTimeMillis());
		return time.substring(time.length() - 8).toCharArray();
	}

	private KeyPair generateKeys() throws Exception
	{
		final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");

		kpg.initialize(2048, new SecureRandom());

		return kpg.generateKeyPair();
	}

	private byte[] pkcs12(final String id, final char[] pw, final X500PrivateCredential credential, final X509Certificate issuerCertificate) throws Exception
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

	private void generate(final String id) throws Exception
	{
		if (id == null)
		{
			return;
		}

		final KeyPair keys = generateKeys();

		final X500PrivateCredential authority = authorityCredential.getCredential();

		final X500PrivateCredential credential = credentialFactory.createCredential(id, authority, keys);

		final char[] pw = generatePassword();
		final byte[] p12 = pkcs12(id, pw, credential, authority.getCertificate());
		final byte[] cert = credential.getCertificate().getEncoded();

		systemLog.info(id + "/pw = " + String.valueOf(pw));
		systemLog.info(id + "/p12 = " + p12.length);
		systemLog.info(id + "/cert = " + cert.length);

		final String dn = credentialStore.persist(id, p12, cert, pw);

		systemLog.info(id + "/dn = " + dn);
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
}
