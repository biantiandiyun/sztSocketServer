package com.lakala;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.context.annotation.ImportResource;

@ImportResource(locations ={"classpath*:META-INF/config/*.xml"})
@SpringBootApplication
public class Boot {

	public static void main(String[] args) {
//		SpringApplication springApplication = new SpringApplication();
		SpringApplication.run(Boot.class, args);
	}

	public void customize(ConfigurableEmbeddedServletContainer arg0) {
	}

}
