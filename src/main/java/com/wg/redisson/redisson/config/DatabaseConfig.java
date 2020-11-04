package com.wg.redisson.redisson.config;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    @Value("${postgres.host}")
    private String postgresHost;

    @Value("${postgres.database}")
    private String databaseName;

    @Bean
    @Qualifier("pgConnectionFactory")
    public ConnectionFactory pgConnectionFactory(R2dbcProperties properties) {
        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host(postgresHost)
                        .database(databaseName)
                        .username(properties.getUsername())
                        .password(properties.getPassword())
                        //.codecRegistrar(EnumCodec.builder().withEnum("post_status", Post.Status.class).build())
                        .build()
        );
    }

/*    @Bean
    public ConnectionFactoryInitializer initializer(@Qualifier("pgConnectionFactory") ConnectionFactory connectionFactory) {

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("data.sql")));
        initializer.setDatabasePopulator(populator);

        return initializer;
    }*/

}
