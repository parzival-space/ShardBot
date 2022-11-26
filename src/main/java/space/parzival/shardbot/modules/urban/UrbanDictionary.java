package space.parzival.shardbot.modules.urban;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import space.parzival.shardbot.modules.urban.model.BaseResponse;
import space.parzival.shardbot.modules.urban.model.Definition;
import space.parzival.shardbot.properties.HttpProperties;

@Service
public class UrbanDictionary {

    private RestTemplate restTemplate;
    private final String apiBase = "https://api.urbandictionary.com/v0";

    private UrbanDictionary(HttpProperties httpProperties) {
        // prepare template
        this.restTemplate = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(httpProperties.getRequestTimeout()))
            .setReadTimeout(Duration.ofSeconds(httpProperties.getRequestTimeout()))
            .defaultHeader("User-Agent", httpProperties.getAgent())
            .defaultHeader("Accept", "application/json")
            .build();
    }


    public List<Definition> getDefinitions(String word) {
        ResponseEntity<BaseResponse> response = this.restTemplate.getForEntity(this.apiBase + "/define?term=" + word, BaseResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody().getList();
        }
        
        return new ArrayList<Definition>();
    }
    
}
