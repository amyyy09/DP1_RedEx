package pe.edu.pucp.packetsoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableCaching
public class PacketsoftApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacketsoftApplication.class, args);
	}

	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST","PUT", "DELETE");
                //registry.addMapping("/api/**").allowedOrigins("http://localhost:8080").allowedMethods("GET", "POST","PUT", "DELETE");
                // Configura aquí el patrón de URL de tu API addMapping("/api/**")
                // Permite solicitudes desde el dominio del frontend allowedOrigins("http://localhost:3000"
                // Especifica los métodos HTTP permitidos allowedMethods("G
            }
        };
    }

}