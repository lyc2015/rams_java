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
