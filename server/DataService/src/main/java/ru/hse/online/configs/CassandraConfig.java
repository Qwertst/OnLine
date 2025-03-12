package ru.hse.online.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.DataCenterReplication;
import org.springframework.data.cassandra.core.cql.session.init.KeyspacePopulator;
import org.springframework.data.cassandra.core.cql.session.init.ResourceKeyspacePopulator;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;

@Configuration
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Value("${spring.cassandra.contact-points}")
    private String contactPoints;

    @Value("${spring.cassandra.port}")
    private int port;

    @Value("${spring.cassandra.keyspace-name}")
    private String keyspaceName;

    @Value("${spring.cassandra.replication-factor}")
    private int replicationFactor;

    @Value("${spring.cassandra.network-strategy}")
    private String networkStrategy;

    @Value("${spring.cassandra.schema-action}")
    private String schemaAction;

    @Value("${spring.cassandra.cql-script-path}")
    private String cqlScriptPath;

    @Override
    protected @NonNull String getKeyspaceName() {
        return keyspaceName;
    }

    @Override
    protected @NonNull String getContactPoints() {
        return contactPoints;
    }

    @Override
    protected int getPort() {
        return port;
    }

    @Override
    public @NonNull SchemaAction getSchemaAction() {
        return SchemaAction.valueOf(schemaAction.toUpperCase());
    }

    @Override
    protected @NonNull List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        CreateKeyspaceSpecification specification;
        if ("SimpleStrategy".equals(networkStrategy)) {
            specification = CreateKeyspaceSpecification
                    .createKeyspace(keyspaceName)
                    .ifNotExists()
                    .withSimpleReplication(replicationFactor);
        } else {
            specification = CreateKeyspaceSpecification
                    .createKeyspace(keyspaceName)
                    .ifNotExists()
                    .withNetworkReplication(DataCenterReplication.of("dc1", replicationFactor));
        }

        return Collections.singletonList(specification);
    }

    @Bean
    public KeyspacePopulator keyspacePopulator() {
        if (cqlScriptPath != null && !cqlScriptPath.isEmpty()) {
            Resource cqlResource = new ClassPathResource(cqlScriptPath);
            return new ResourceKeyspacePopulator(cqlResource);
        } else {
            return null;
        }
    }
}
