package com.hackday.sns_timeline.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

@Configuration
public class ThymeleafViewResolverConfig {

	@Value("${spring.thymeleaf.cache}")
	private boolean isCache;

	// 템플릿 리졸버 빈 등록
	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setPrefix("classpath:templates/");
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("LEGACYHTML5");
		templateResolver.setCacheable(isCache);
		return templateResolver;
	}

	// 템플릿 엔진 빈 등록
	@Bean
	public SpringTemplateEngine templateEngine(MessageSource messageSource) {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		templateEngine.setTemplateEngineMessageSource(messageSource);

		templateEngine.addDialect(new SpringSecurityDialect());
		return templateEngine;
	}

	// 뷰 리졸버 빈 등록
	@Bean
	@Autowired
	public ViewResolver viewResolver(MessageSource messageSource) {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine(messageSource));
		viewResolver.setCharacterEncoding("UTF-8");
		viewResolver.setOrder(0);
		return viewResolver;
	}

	// 시큐리티 방언 사용을 위해서 빈 등록
	// 템플릿 엔진 빈 등록 시 사용됨
   @Bean
   public SpringSecurityDialect securityDialect() {
	   return new SpringSecurityDialect();
   }
}
