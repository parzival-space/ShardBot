package space.parzival.shardbot.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@ConfigurationProperties(prefix = "shard.http")
public class HttpProperties {
 
    /**
     * The user agent to use for this application.
     */
    private String agent = "Shard Bot";

    /**
     * How long a server has time before the client drops the request in seconds.
     */
    private Integer requestTimeout = 60;
}
