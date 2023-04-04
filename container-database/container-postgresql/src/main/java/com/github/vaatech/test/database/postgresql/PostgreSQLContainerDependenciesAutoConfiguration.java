package com.github.vaatech.test.database.postgresql;

import com.github.vaatech.test.common.spring.DependsOnPostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

import static com.github.vaatech.test.database.postgresql.PostgreSQLProperties.BEAN_NAME_CONTAINER_POSTGRESQL;
import static org.springframework.boot.autoconfigure.AutoConfigureOrder.DEFAULT_ORDER;

@AutoConfiguration(after = {DataSourceAutoConfiguration.class})
@AutoConfigureOrder(DEFAULT_ORDER)
@ConditionalOnClass(DataSource.class)
@ConditionalOnExpression("${containers.enabled:true}")
@ConditionalOnProperty(name = "container.postgresql.enabled", matchIfMissing = true)
public class PostgreSQLContainerDependenciesAutoConfiguration {

  @Bean
  public static BeanFactoryPostProcessor datasourcePostgreSqlDependencyPostProcessor() {
    return new DependsOnPostProcessor(
        DataSource.class, BEAN_NAME_CONTAINER_POSTGRESQL);
  }
}
