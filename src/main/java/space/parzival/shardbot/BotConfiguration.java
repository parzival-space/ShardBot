package space.parzival.shardbot;

import java.util.List;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import space.parzival.shardbot.properties.ClientProperties;
import space.parzival.shardbot.types.Command;
import space.parzival.shardbot.types.EventListener;
import space.parzival.shardbot.utils.Commands;

@Slf4j
@Configuration
public class BotConfiguration {
    
    @Autowired
    private ClientProperties clientProperties; //NOSONAR - required

    @Autowired
    private List<? extends Command> commands; //NOSONAR - required

    @Autowired
    private List<? extends EventListener> events; //NOSONAR - required

    /**
     * The Main-Function for the Client.
     * 
     * @return
     * @throws LoginException
     */
    @Bean(name = "Discord Service")
    public void handleDiscordService() throws LoginException {

        JDA client = JDABuilder.createDefault(clientProperties.getToken())
                .build();

        // remove previously registered commands
        Commands.registerCommands(commands, client);

        // register new commands & event handlers
        events.forEach(client::addEventListener);
        log.info("Registered {} events and {} commands.", events.size(), commands.size());

    }

}
