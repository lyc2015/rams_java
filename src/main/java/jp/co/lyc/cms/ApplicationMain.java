package jp.co.lyc.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableScheduling
public class ApplicationMain {
	public static void main(String[] args) {
		// 强制使用Logback，排除其他SLF4J绑定实现
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "off");
		System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
		// 设置Logback为默认日志系统
		System.setProperty("logging.config", "classpath:logback-spring.xml");
		
		SpringApplication.run(ApplicationMain.class, args);
		System.setProperty("mail.mime.splitlongparameters", "false");
	}

	@Bean
	public FilterRegistrationBean<Myfilter> registFilter() {
		FilterRegistrationBean<Myfilter> bean = new FilterRegistrationBean<Myfilter>();
		// 定义filter的过滤路径规则。
		bean.addUrlPatterns("/*");
		bean.setFilter(new Myfilter());
		return bean;
	}
}
