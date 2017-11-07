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
package de.unimuenster.imi.odmda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import de.unimuenster.imi.odmda.model.FileValidator;
import de.unimuenster.imi.odmda.model.ODMValidator;

/**
 * Main file to configure the spring application.
 * 
 * @author Tobias Brix
 * @author Saad Sarfraz
 */
@EnableWebMvc
@Configuration
@ComponentScan({"de.unimuenster.imi.odmda.config",
				"de.unimuenster.imi.odmda.config.databases",
				"de.unimuenster.imi.odmda.controller",
				"de.unimuenster.imi.odmda.cron",
				"de.unimuenster.imi.odmda.dao",
				"de.unimuenster.imi.odmda.export",
				"de.unimuenster.imi.odmda.service",
				"de.unimuenster.imi.odmda.statistics",
				"de.unimuenster.imi.odmda.utils",
				"de.unimuenster.imi.odmda.utils.pdf"
})
public class AppConfig extends WebMvcConfigurerAdapter{

	@Bean
	public UrlBasedViewResolver viewResolver() {
		UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
		viewResolver.setExposeContextBeansAsAttributes(true);
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/jsp/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}

	@Bean
	public FileValidator fileValidator(){
		return new FileValidator();
	}

	@Bean
	public ODMValidator odmValidator(){
		return new ODMValidator();
	}

	@Bean
	public CommonsMultipartResolver multipartResolver(){
		return new CommonsMultipartResolver();
	}

	@Bean
	public ResourceBundleMessageSource messageSource(){
		ResourceBundleMessageSource  messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("validation");
		return messageSource;
	}

	//provides access to static resources and webjars
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**")
				.addResourceLocations("/WEB-INF/resources/");
		registry.addResourceHandler("/webjars/**")
				.addResourceLocations("/webjars/");
		registry.addResourceHandler("/PDFs/**")
				.addResourceLocations("/WEB-INF/PDFs/");
	}
}
