package ru.lab.hunter.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DbConfig {
    @Primary
    @Bean("masterConfig")
    @ConfigurationProperties("spring.datasource")
    public HikariConfig getMasterConfig() {
        HikariConfig hikariConfig = getNewConfig();
        hikariConfig.setPoolName("master-pool");
        hikariConfig.setInitializationFailTimeout(-1);
        hikariConfig.setConnectionTimeout(0);
        return hikariConfig;
    }

    @Primary
    @Bean("master")
    public DataSource getMasterDataSource() throws SQLException {
        DataSource dataSource = new HikariDataSource(getMasterConfig());
        dataSource.getConnection();
        return dataSource;
    }

    private HikariConfig getNewConfig() {
        return new HikariConfig();
    }
}
