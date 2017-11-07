/**
 * ODM Data Analysis - a tool for the automatic validation, monitoring and
 * generation of generic descriptive statistics of clinical data.
 * 
 * Copyright (c) 2017 Institut für Medizinische Informatik, Münster
 *
 * ODM Data Analysis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, version 3.
 *
 * ODM Data Analysis is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 * more details.
 *
 * You should have received a copy of the GNU General Public License in the file
 * COPYING along with ODM Data Analysis. If not, see <http://www.gnu.org/licenses/>.
 */
package de.unimuenster.imi.odmda.config.databases;

import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Hibernate Configuration
 * 
 * @author Tobias Brix
 */
@Configuration
//enable scheduling annotations (used for cron-job)
@EnableScheduling //poolsize of 1 by default
@EnableAsync
@ComponentScan("de.unimuenster.imi.odmda.cron")
@EnableTransactionManagement
public class HibernateConfig {

	@Autowired
	private CustomRoutingDataSource s;

	@Bean
	public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		localSessionFactoryBean.setHibernateProperties(hibernateProperties());
		localSessionFactoryBean.setPackagesToScan("de.unimuenster.imi.odmda.model.metadata",
												  "de.unimuenster.imi.odmda.model.clinicaldata");
		localSessionFactoryBean.setDataSource(dataSource);
		return localSessionFactoryBean;
	}

	@Bean(name = "dataSource")
	public DataSource dataSource() {
		return s;
	}

	Properties hibernateProperties() {  
		return new Properties() {  
			{
				//configure H2
				setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
				//Disable the second-level cache
				setProperty("hibernate.cache.provider_class", "org.hibernate.cache.internal.NoCacheProvider");
				//Echo all executed SQL to stdout
				setProperty("hibernate.show_sql", "false");
				//Drop and re-create the database schema on startup
				//setProperty("hibernate.hbm2ddl.auto", "create");
				//Shortcut for lazy collections
				setProperty("hibernate.enable_lazy_load_no_trans", "true");
				setProperty("hibernate.connection.pool_size", "50");
				setProperty("hibernate.connection.autocommit","false");
				//batching
				setProperty("hibernate.jdbc.batch_size", "50");
				setProperty("hibernate.order_inserts", "true");
				setProperty("hibernate.order_updates", "true");
				setProperty("hibernate.jdbc.batch_versioned_data", "true");
				
				//setProperty("hibernate.show_sql", "true");
			}
		};
	}

	@Bean 
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {  
		HibernateTransactionManager txManager = new HibernateTransactionManager();  
		txManager.setSessionFactory(sessionFactory);  
		return txManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
}
