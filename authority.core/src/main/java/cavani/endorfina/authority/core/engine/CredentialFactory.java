package cavani.endorfina.authority.core.engine;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class CredentialFactory
{

	X500PrivateCredential createCredential(final String id, final X500PrivateCredential issuer, final KeyPair keys) throws Exception
	{
		final String signatureAlgorithm = "SHA1withRSA";
		final X500Principal principal = new X500Principal("CN=" + id + ", OU=Endorfina, O=Cavani");
		final X500Principal issuerPrincipal = issuer.getCertificate().getSubjectX500Principal();

		final BigInteger serial = BigInteger.valueOf(System.nanoTime());

		final Calendar date = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		date.add(Calendar.DAY_OF_YEAR, -1);

		final Date startDate = date.getTime();

		date.add(Calendar.YEAR, 1);

		final Date endDate = date.getTime();

		final X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(issuerPrincipal, serial, startDate, endDate, principal, keys.getPublic());

		certificateBuilder.addExtension(X509Extension.basicConstraints, false, new BasicConstraints(false));

		final JcaX509ExtensionUtils x509Extension = new JcaX509ExtensionUtils();
		certificateBuilder.addExtension(X509Extension.subjectKeyIdentifier, false, x509Extension.createSubjectKeyIdentifier(keys.getPublic()));
		certificateBuilder.addExtension(X509Extension.authorityKeyIdentifier, false, x509Extension.createAuthorityKeyIdentifier(issuer.getCertificate()));

		final ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).setProvider(BouncyCastleProvider.PROVIDER_NAME).build(issuer.getPrivateKey());

		final X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

		final X509Certificate certificate = new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getCertificate(certificateHolder);

		return new X500PrivateCredential(certificate, keys.getPrivate(), id);
	}

}
