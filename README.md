Endorfina X.509
===============

An Identity Management system based on X.509, TLS and Client Certificate.

(GitHub)

https://github.com/cirocavani/endorfina-x509

Gears
-----

**X.509**

* Authority key pair generation (self-signed)
* Client key pair generation (Authority signed)
* Client Certificate validation
* Directory backend (LDAP)

**Authentication**

* JBoss Login Module adapter
* Server Application client-cert
* Client Application TLS client-cert

Featuring
---------

* BouncyCastle 1.48
* JBoss AS 7.1

Missing
-------

* Tests

Configuration
-------------

**Application**

`/authority.core/src/main/resources/META-INF/authority.properties`

	privatekey_file=(path to cakey.pem)
	privatekey_password=(cakey.pem password)
	certificate_file=(path to cacert.pem)
	credential_root_dn=(ldap dn for credentials)

`/authority.core/src/main/resources/META-INF/directory.properties`

	url=(ldap server)
	binddn=(ldap auth user)
	bindpw=(ldap auth password)

**Java 7**

Oracle JDK requires update with Unlimited Strength Policy (key size) - not required for OpenJDK (IcedTea) afaik.

http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html

	tar xzf jdk-7u17-linux-x64.tar.gz --directory=/srv/Software
	ln -s /srv/Software/jdk1.7.0_17 /srv/Software/Java7
	unzip -jo UnlimitedJCEPolicyJDK7.zip -d /srv/Software/Java7/jre/lib/security/

(Ubuntu)

	sudo apt-get install openjdk-7-jre-headless --no-install-recommends

**JBoss AS 7**

Using JBoss AS 7.1.1.Final.

	wget -t inf -c http://download.jboss.org/jbossas/7.1/jboss-as-7.1.1.Final/jboss-as-7.1.1.Final.tar.gz
	
	sha1sum jboss-as-7.1.1.Final.tar.gz
	fcec1002dce22d3281cc08d18d0ce72006868b6f
	
	tar xzf jboss-as-7.1.1.Final.tar.gz --directory=/srv/Software --xform s/jboss-as-7.1.1.Final/AuthorityServer/1

(Java)

This procedure describes JAVA_HOME being set only for server, otherwise it can be set on environment.

standalone.conf

	nano -w /srv/Software/AuthorityServer/bin/standalone.conf
	...
	
	JAVA_HOME="/srv/Software/Java7"

jboss-cli.sh

	nano -w /srv/Software/AuthorityServer/bin/jboss-cli.sh
	...
	
	#!/bin/sh
	
	DIRNAME=`dirname "$0"`
	
	JAVA_HOME="/srv/Software/Java7"

add-user.sh
	
	nano -w /srv/Software/AuthorityServer/bin/add-user.sh
	...
	
	#!/bin/sh
	
	DIRNAME=`dirname "$0"`
	
	JAVA_HOME="/srv/Software/Java7"

(...)

(start)

	/srv/Software/AuthorityServer/bin/standalone.sh > /dev/null 2>&1 &

(stop)

	/srv/Software/AuthorityServer/bin/jboss-cli.sh --connect command=:shutdown

(log)

	tail -n 200 -f /srv/Software/AuthorityServer/standalone/log/server.log

...

Admin Console Credential

	/srv/Software/AuthorityServer/bin/add-user.sh
	(ENTER, ENTER, 'system', 'secret' x 2, 'yes')


Tools
-----

**AuthoritySetup**

Generates Authority key-pair.

`/authority.tools/src/main/java/cavani/endorfina/authority/tools/AuthoritySetup.java`

	Principal "CN=Authority, OU=Endorfina, O=Cavani"
	Algorithm RSA 2048
	Signature SHA1withRSA
	
Generated files:

	(key-pair, pw='secret')
	Authority.p12
	
	(Self-signed certificate)
	cacert.pem
	
	(Encrypted private key, pw='secret')
	cakey.pem

**CredentialFactory**

Generates Client key-pair.

`/authority.tools/src/main/java/cavani/endorfina/authority/tools/CredentialFactory.java`

	Principal "CN=Credential, OU=Endorfina, O=Cavani"
	Algorithm RSA 2048
	Signature SHA1withRSA
	(Requires Authority.p12)
	
Generated files:

	(key-pair, pw='secret')
	Credential.p12
	
	(Authority-signed certificate)
	Credential_cert.pem
	
	(Private key, no pw)
	Credential_key.pem

**ServerCredential**

Generates Server key-pair.

`/authority.tools/src/main/java/cavani/endorfina/authority/tools/ServerCredential.java`

	Principal "CN=appserver, OU=Endorfina, O=Cavani"
	Algorithm RSA 2048
	Signature SHA1withRSA
	(Requires Authority.p12)
	
Generated files:

	(Private key, chain Authority-signed certificated Authority certificate)
	keystore.jks
	
	(Authority certificate)
	truststore.jks

**FakeLdap38900**

Binds to port 38900, implements LDAPv3 protocol and stores in `data` folder.

`/authority.fakeldap/src/main/java/cavani/endorfina/authority/tools/FakeLdap38900.java`

Deploy
------

On root repo folder.

	mkdir authority
	sudo ln -s `pwd`/authority /srv/

(generates jar/war for JBoss in `deploy` folder) 

	gradle build deploy
	ln -s ../deploy authority

(generates `cakey.pem` and `cacert.pem` in `/srv/authority` folder)
(generates `keystore.jks` and `truststore.jks` in `/srv/authority/server` folder)

	gradle :authority.tools:runAuthoritySetup
	mkdir authority/private
	mv authority.tools/cakey.pem authority/private/
	mv authority.tools/cacert.pem authority/
	
	gradle :authority.tools:runServerCredential
	mkdir authority/server
	mv authority.tools/{key,trust}store.jks authority/server/

LDAP server (new Terminal must stay open)

	gradle :authority.fakeldap:run

JBoss server (new Terminal must stay open)

	/srv/Software/AuthorityServer/bin/standalone.sh

(JBoss setup)

	/srv/Software/AuthorityServer/bin/jboss-cli.sh --connect
	
	/subsystem=web/virtual-server=default-host:write-attribute(name="alias", value=["appserver"])
	/subsystem=web/connector=https:add(socket-binding=https, scheme=https, protocol="HTTP/1.1", secure=true, enabled=true)
	/subsystem=web/connector=https/ssl=configuration:add(certificate-key-file="/srv/authority/server/keystore.jks",ca-certificate-file="/srv/authority/server/truststore.jks",password="secret",key-alias="server",protocol="SSLv3",verify-client="true")
	:reload
	
	/subsystem=security/security-domain=EndorfinaAuthority:add(cache-type="default")
	
	/subsystem=security/security-domain=EndorfinaAuthority/authentication=classic:add( \
		login-modules=[{"code" => "cavani.endorfina.authority.adapter.LoginModule", "flag" => "required"}]) \
		{allow-resource-service-restart=true}

	/subsystem=deployment-scanner/scanner=authority:add(scan-interval=10000,path="/srv/authority/deploy")
		