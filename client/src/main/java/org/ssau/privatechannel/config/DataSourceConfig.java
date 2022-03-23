package org.ssau.privatechannel.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.ssau.privatechannel.constants.SystemProperties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "java")
@EnableTransactionManagement
public class DataSourceConfig {

    private static abstract class HibernateProps {
        public static final String DIALECT = "hibernate.dialect";
        public static final String SHOW_SQL = "hibernate.show_sql";
        public static final String AUTO_DDL = "hibernate.hbm2ddl.auto";
        public static final String SQL_FORMAT = "hibernate.format_sql";
    }

    private static final String POSTGRES_DIALECT = "org.hibernate.dialect.PostgreSQL94Dialect";

    @Bean
    public DataSource getDataSource() {

        String url = System.getProperty(SystemProperties.DB_URL);
        String username = System.getProperty(SystemProperties.DB_USER);
        String password = System.getProperty(SystemProperties.DB_PASSWORD);

        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(url);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }

    @Bean
    public Properties hibernateProperties() {
        Properties hibernateProp = new Properties();
        hibernateProp.put(HibernateProps.DIALECT, POSTGRES_DIALECT);
        hibernateProp.put(HibernateProps.SHOW_SQL, Boolean.TRUE.toString());
        hibernateProp.put(HibernateProps.AUTO_DDL, "update");
        hibernateProp.put(HibernateProps.SQL_FORMAT, Boolean.TRUE.toString());
        return hibernateProp;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory());
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan("org/ssau/privatechannel/model");
        factoryBean.setDataSource(getDataSource());
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factoryBean.setJpaProperties(hibernateProperties());
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        factoryBean.afterPropertiesSet();
        return factoryBean.getNativeEntityManagerFactory();
    }
}