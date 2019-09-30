package nl.menninga.menno.cms;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

import nl.menninga.menno.cms.jdbc.datasource.RetryableDataSource;

@EnableJpaRepositories()
@EnableJpaAuditing()
@EnableRetry
@SpringBootApplication
public class CmsServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CmsServerApplication.class, args);
	}
    
    @Order(Ordered.HIGHEST_PRECEDENCE)
	private class RetryableDataSourceBeanPostProcessor implements BeanPostProcessor {
		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName)
				throws BeansException {
			if (bean instanceof DataSource) {
				bean = new RetryableDataSource((DataSource)bean);
			}
			return bean;
		}
		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName)
				throws BeansException {
			return bean;
		}
	}
	
	@Bean
	public BeanPostProcessor dataSouceWrapper() {
		return new RetryableDataSourceBeanPostProcessor();
	}

}
