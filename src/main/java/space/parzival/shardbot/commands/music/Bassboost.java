package space.parzival.shardbot.commands.music;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import space.parzival.discord.shared.base.exceptions.CommandExecutionException;
import space.parzival.shardbot.modules.music.AudioControl;
import space.parzival.discord.shared.base.types.Command;
import space.parzival.discord.shared.base.types.RichEmbedBuilder;

@Component
public class Bassboost extends Command {

    @Autowired
    private AudioControl audioController;
    
    public Bassboost() {
        super("bassboost", "Enables/Disables the Bass-Boost.");

        super.options.add(new OptionData(OptionType.INTEGER, "percent", "The Bass-Boost amount in percent.", true));
    }


    @Override
    public void execute(JDA client, SlashCommandInteractionEvent event, InteractionHook hook) throws CommandExecutionException {

        Guild guild = event.getGuild();
        Member member = event.getMember();
        
        // is this actually run in a guild?
        if (member == null || guild == null) {
            hook.sendMessageEmbeds(
                RichEmbedBuilder.simple("Sorry, this command only works in a server.").build()
            ).queue();
            return;
        }

        // get equalizer
        EqualizerFactory equalizer = this.audioController.getEqualizerForGuild(guild);
        
        // load option
        int amount = 0;
        OptionMapping amountOption = event.getOption("percent");
        if (amountOption != null && amountOption.getAsInt() > 100) {
            amount = 100;
        } else if (amountOption != null && amountOption.getAsInt() > 0) {
            amount = amountOption.getAsInt();
        }

        // calculate multiplier
        float multiplier = amount / 100.00f;

        // apply bass boost
        for (int i = 0; i < BASS_BOOST.length; i++) {
            equalizer.setGain(i, BASS_BOOST[i] * multiplier);
        }

        MessageEmbed message = new RichEmbedBuilder()
            .setTitle("Bass-Boost set to " + amount + "%.")
            .setDescription("It may take a while to apply this.")
            .build();

        hook.sendMessageEmbeds(message).queue();
    }

    // bass boost pattern
    private static final float[] BASS_BOOST = {
        0.2f, 0.15f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, 
        -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f
    };
}
