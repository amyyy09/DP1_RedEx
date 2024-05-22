package src;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RedexApplication {
	public static void main(String[] args) {
		SpringApplication.run(RedexApplication.class, args);
		System.out.println("Hola Mundo");
	}

	@Bean
	public String anyBean() {
		return "Test";
	}
}
