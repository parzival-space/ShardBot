package space.parzival.shardbot.events;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.ReadyEvent;
import space.parzival.shardbot.types.EventListener;

@Slf4j
@Service
public class BotReady extends EventListener {
    
    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        log.info("The Application is now read to operate.");
    }
}
