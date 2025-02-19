package com.github.vaatech.testcontainers.mysql;

import com.github.vaatech.testcontainers.DependsOnBeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

import static com.github.vaatech.testcontainers.mysql.MySQLProperties.BEAN_NAME_CONTAINER_MYSQL;

@AutoConfiguration(afterName = "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration")
@ConditionalOnClass(DataSource.class)
@ConditionalOnMySQLContainerEnabled
public class MySQLContainerDependenciesAutoConfiguration {

    @Bean
    public static BeanFactoryPostProcessor datasourceMySqlDependencyPostProcessor() {
        return new DependsOnBeanFactoryPostProcessor(DataSource.class, BEAN_NAME_CONTAINER_MYSQL);
    }
}
