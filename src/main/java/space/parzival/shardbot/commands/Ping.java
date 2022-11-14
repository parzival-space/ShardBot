package space.parzival.shardbot.commands;

import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import space.parzival.shardbot.exceptions.CommandExecutionException;
import space.parzival.shardbot.types.Command;

@Component
public class Ping extends Command {
    
    public Ping() {
        super();
        super.name = "ping";
        super.description = "An example Command";

        // required for every command => override execute function
        super.executingInstance = this;
    }


    @Override
    public void execute(JDA client, SlashCommandInteractionEvent event, InteractionHook hook) throws CommandExecutionException {

        long gatewayPing = client.getGatewayPing();
        long restPing = client.getRestPing().complete();

        // average ping
        long average = (gatewayPing + restPing) / 2;

        hook.sendMessage("Pong (" + average + "ms)").queue();
    }
}
