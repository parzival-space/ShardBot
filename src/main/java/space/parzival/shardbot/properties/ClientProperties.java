package space.parzival.shardbot.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@ConfigurationProperties(prefix = "shard.client")
public class ClientProperties {
 
    /**
     * The Discord Client Token
     */
    private String token;
}
