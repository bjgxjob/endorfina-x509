package cavani.endorfina.authority.core;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500PrivateCredential;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class CredentialFactory
{

	X500PrivateCredential createCredential(final String id, final X500PrivateCredential issuer, final KeyPair credential) throws Exception
	{
		final X500NameBuilder name = new X500NameBuilder(BCStyle.INSTANCE);

		name.addRDN(BCStyle.O, "Disruptive");
		name.addRDN(BCStyle.OU, "Concept");

		name.addRDN(BCStyle.CN, id);

		final X500Name issuerName = X500Name.getInstance(issuer.getCertificate().getSubjectX500Principal().getEncoded());

		final ContentSigner sigGen = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(issuer.getPrivateKey());

		final Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
		final Date endDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);

		final X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerName, BigInteger.ONE, startDate, endDate, name.build(), credential.getPublic());
		certGen.addExtension(X509Extension.keyUsage, true, new X509KeyUsage(X509KeyUsage.nonRepudiation | X509KeyUsage.digitalSignature | X509KeyUsage.keyEncipherment));
		certGen.addExtension(X509Extension.extendedKeyUsage, true, new DERSequence(KeyPurposeId.anyExtendedKeyUsage));

		final JcaX509ExtensionUtils x509ext = new JcaX509ExtensionUtils();

		certGen.addExtension(X509Extension.authorityKeyIdentifier, false, x509ext.createAuthorityKeyIdentifier(issuer.getCertificate()));
		certGen.addExtension(X509Extension.subjectKeyIdentifier, false, x509ext.createSubjectKeyIdentifier(credential.getPublic()));

		final X509CertificateHolder certHolder = certGen.build(sigGen);

		final X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

		return new X500PrivateCredential(cert, credential.getPrivate(), id);
	}

}
