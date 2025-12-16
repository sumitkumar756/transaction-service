package com.transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction.service.TransactionService;
import com.zaxxer.hikari.HikariConfig;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
   public static final  Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws JsonProcessingException {

        String env = System.getenv("ENV");
        Config applicationConf = Config.builder()
            .sources(ConfigSources.classpath("application.conf"))
            .build();
        Config envConfig = getEnvironmentConfig(applicationConf, env);
        logger.info("Transaction Service is starting...");
        DSLContext dslContext = getDSLContext(envConfig); // Assume this method initializes and returns a DSLContext
        TransactionService transactionService = new TransactionService(dslContext);
        RestApiRouter mainApiRouter = new RestApiRouter(transactionService);
        mainApiRouter.startServer(applicationConf);
        logger.info("Transaction Service started successfully.");
    }


    private static Config getEnvironmentConfig(Config applicationConf, String env) {
        if(env == null){
            logger.error("ENV variable is not set. Changing to service startup by default to 'dev'");
            env = "dev";
        }
        if(applicationConf.get(env).exists()){
            logger.info("Loading configuration for environment: {}", env);
            return applicationConf.get(env);
        } else {
            logger.warn("Configuration for environment: {} not found. Falling back to 'dev' configuration",env);
            return applicationConf.get("dev");
        }
    }

    private static DSLContext getDSLContext(Config envConfig) throws JsonProcessingException {
        DataSource dataSource = getDataSource(envConfig);
        SQLDialect sqlDialect = SQLDialect.valueOf(envConfig.get("database.dialect").asString().get());
        DSLContext dslContext = DSL.using(dataSource, sqlDialect, new Settings().withRenderNameCase(RenderNameCase.LOWER)
                .withExecuteWithOptimisticLockingExcludeUnversioned(true).withUpdateRecordVersion(true));
        try (Connection connection = dataSource.getConnection()) {
            logger.info("Successfully connected to the database: {}", connection.getMetaData().getURL());
            return dslContext;
        }catch (SQLException exception){
            logger.error("Error initializing DSLContext", exception);
            throw new RuntimeException(exception);
        }
    }

    private static DataSource getDataSource(Config envConfig) throws JsonProcessingException {
        HikariConfig poolConfig = new HikariConfig();
        logger.info("print database conf:"+ new ObjectMapper().writeValueAsString(envConfig));
        poolConfig.setJdbcUrl(envConfig.get("database.url").asString().get());
        poolConfig.setUsername(envConfig.get("database.user").asString().get());
        poolConfig.setPassword(envConfig.get("database.password").asString().get());
        poolConfig.setMinimumIdle(envConfig.get("database.minIdleConnections").asInt().orElse(5));
        poolConfig.setMaximumPoolSize(envConfig.get("database.maxPoolSize").asInt().orElse(10));
        return new com.zaxxer.hikari.HikariDataSource(poolConfig);
    }


}
