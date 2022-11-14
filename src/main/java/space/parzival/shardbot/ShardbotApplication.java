package space.parzival.shardbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import space.parzival.shardbot.properties.ClientProperties;

@SpringBootApplication
@EnableConfigurationProperties
@Import({ClientProperties.class})
public class ShardbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShardbotApplication.class, args);
	}

}
