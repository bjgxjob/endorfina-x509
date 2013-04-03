package cavani.endorfina.authority.tools;

import java.io.File;
import java.util.HashSet;

import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.api.ldap.schemaextractor.SchemaLdifExtractor;
import org.apache.directory.api.ldap.schemaextractor.impl.DefaultSchemaLdifExtractor;
import org.apache.directory.api.ldap.schemaloader.LdifSchemaLoader;
import org.apache.directory.api.ldap.schemamanager.impl.DefaultSchemaManager;
import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.api.CacheService;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.core.api.schema.SchemaPartition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmIndex;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.core.partition.ldif.LdifPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.server.xdbm.Index;

public class FakeLdap38900
{

	private FakeLdap38900()
	{
	}

	public static void main(final String[] args) throws Exception
	{
		System.out.println("Ldap Server starting...");

		final File dataFolder = new File("data");

		if (!dataFolder.exists())
		{
			dataFolder.mkdirs();
		}

		final DirectoryService service = createDirectoryService(dataFolder);

		setupDirectory(service);
		//		final LdapConnection ldap = new LdapCoreSessionConnection(service);
		//		final Entry result = ldap.lookup(new Dn("dc=endorfina,dc=com"));
		//		System.out.println("Found entry : " + result);

		final LdapServer server = new LdapServer();
		server.setTransports(new TcpTransport("127.0.0.1", 38900));
		server.setDirectoryService(service);

		server.start();

		System.out.println("Ready!");
		Runtime.getRuntime().addShutdownHook(new Thread()
		{

			@Override
			public void run()
			{
				System.out.println("Ldap Server stoping...");
				if (server != null && server.isStarted())
				{
					try
					{
						server.stop();
					}
					catch (final Exception e)
					{
						e.printStackTrace();
					}
				}
				if (service != null)
				{
					try
					{
						service.shutdown();
					}
					catch (final Exception e)
					{
						e.printStackTrace();
					}
				}
				System.out.println("Done!");
				System.out.flush();
			}
		});
	}

	static Partition createJdbmPartition(final SchemaManager schema, final String partitionId, final String partitionDn, final File dataFolder) throws Exception
	{
		final JdbmPartition partition = new JdbmPartition(schema);
		partition.setId(partitionId);
		partition.setPartitionPath(new File(dataFolder, partitionId).toURI());
		partition.setSuffixDn(new Dn(partitionDn));

		return partition;
	}

	static SchemaPartition createSchemaPartition(final SchemaManager schema, final File dataFolder) throws Exception
	{
		final SchemaPartition partition = new SchemaPartition(schema);

		final LdifPartition store = new LdifPartition(schema);
		final File schemaDir = new File(dataFolder, "schema");
		store.setPartitionPath(schemaDir.toURI());

		if (!schemaDir.exists())
		{
			final SchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor(dataFolder);
			extractor.extractOrCopy(true);
		}

		partition.setWrappedPartition(store);
		schema.setSchemaLoader(new LdifSchemaLoader(schemaDir));
		schema.loadAllEnabled();

		return partition;
	}

	static void addIndex(final Partition partition, final String... attrs)
	{
		final HashSet<Index<?, ?, String>> indexedAttributes = new HashSet<>();

		for (final String attribute : attrs)
		{
			indexedAttributes.add(new JdbmIndex<String, Entry>(attribute, false));
		}

		((JdbmPartition) partition).setIndexedAttributes(indexedAttributes);
	}

	static DirectoryService createDirectoryService(final File dataFolder) throws Exception
	{
		final DirectoryService service = new DefaultDirectoryService();
		final SchemaManager schema = new DefaultSchemaManager();
		service.setSchemaManager(schema);
		service.setInstanceLayout(new InstanceLayout(dataFolder));
		service.setCacheService(new CacheService());

		service.getChangeLog().setEnabled(false);
		service.setDenormalizeOpAttrsEnabled(true);

		final SchemaPartition schemaPartition = createSchemaPartition(schema, dataFolder);
		service.setSchemaPartition(schemaPartition);

		final Partition systemPartition = createJdbmPartition(schema, "system", ServerDNConstants.SYSTEM_DN, dataFolder);
		service.setSystemPartition(systemPartition);

		final Partition mainPartition = createJdbmPartition(schema, "main", "dc=endorfina,dc=com", dataFolder);
		service.addPartition(mainPartition);

		addIndex(mainPartition, "objectClass", "ou", "uid");

		service.startup();

		return service;
	}

	static void setupDirectory(final DirectoryService service) throws Exception
	{
		final Dn dn = new Dn("dc=endorfina,dc=com");
		if (!service.getAdminSession().exists(dn))
		{
			final Entry entry = service.newEntry(dn);
			entry.add("objectClass", "top", "domain", "extensibleObject");
			entry.add("dc", "endorfina");
			service.getAdminSession().add(entry);
		}

		final Dn binddn = new Dn(new Rdn("cn=authority"), dn);
		if (!service.getAdminSession().exists(binddn))
		{
			final Entry entry = service.newEntry(binddn);
			entry.add("objectClass", "person", "organizationalPerson", "inetOrgPerson", "top");
			entry.add("cn", "authority");
			entry.add("sn", "endorfina");
			entry.add("uid", "authority");
			entry.add("userPassword", "secret");
			service.getAdminSession().add(entry);
		}

		final Dn authdn = new Dn("o=Authority,dc=endorfina,dc=com");
		if (!service.getAdminSession().exists(authdn))
		{
			final Entry entry = service.newEntry(authdn);
			entry.add("objectClass", "organization", "top");
			entry.add("o", "Authority");
			service.getAdminSession().add(entry);
		}

		final Dn creddn = new Dn("ou=Credentials,o=Authority,dc=endorfina,dc=com");
		if (!service.getAdminSession().exists(creddn))
		{
			final Entry entry = service.newEntry(creddn);
			entry.add("objectClass", "organizationalUnit", "top");
			entry.add("ou", "Credentials");
			service.getAdminSession().add(entry);
		}

		service.setAllowAnonymousAccess(false);
		service.setAccessControlEnabled(true);
	}

}
