package ru.fedrbodr.exchangearbitr.config.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		entityManagerFactoryRef = "longTimeEntityManagerFactory", transactionManagerRef = "longTimeTransactionManager",
		basePackages = {"ru.fedrbodr.exchangearbitr.dao.longtime.repo"}
)
public class LongTimeDbConfig {

	@Bean(name = "longTimeDataSource")
	@ConfigurationProperties(prefix = "longtime.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "longTimeEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean longTimeEntityManagerFactory(
			EntityManagerFactoryBuilder builder, @Qualifier("longTimeDataSource") DataSource dataSource) {
		return builder.dataSource(dataSource).packages("ru.fedrbodr.exchangearbitr.dao.longtime.domain").persistenceUnit("longtime")
				.build();
	}

	@Bean(name = "longTimeTransactionManager")
	public PlatformTransactionManager barTransactionManager(
			@Qualifier("longTimeEntityManagerFactory") EntityManagerFactory longTimeEntityManagerFactory) {
		return new JpaTransactionManager(longTimeEntityManagerFactory);
	}

}
