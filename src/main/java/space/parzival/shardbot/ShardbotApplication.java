package space.parzival.shardbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import space.parzival.shardbot.properties.ClientProperties;
import space.parzival.shardbot.properties.HttpProperties;

@SpringBootApplication
@EnableConfigurationProperties
@Import({
	ClientProperties.class,
	HttpProperties.class
})
public class ShardbotApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(ShardbotApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		
		application.run(args);
	}

}
