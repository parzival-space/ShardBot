package space.parzival.shardbot.commands.music;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import space.parzival.shardbot.exceptions.CommandExecutionException;
import space.parzival.shardbot.modules.music.AudioControl;
import space.parzival.shardbot.modules.music.TrackScheduler;
import space.parzival.shardbot.types.Command;
import space.parzival.shardbot.types.RichEmbedBuilder;

@Component
public class Skip extends Command {

    @Autowired
    private AudioControl audioController;
    
    public Skip() {
        super();
        super.name = "skip";
        super.description = "Skips the current playback.";

        super.options.add(new OptionData(OptionType.INTEGER, "amount", "The number of songs you want to skip."));

        // required for every command => override execute function
        super.executingInstance = this;
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

        // make sure the user does not input some trash
        int amount = 1;
        OptionMapping amountOption = event.getOption("amount");
        if (amountOption != null) amount = amountOption.getAsInt();

        if (amount < 1) {
            hook.sendMessageEmbeds(
                RichEmbedBuilder.simple("Values less than 1 are not valid!").build()
            ).queue();
            return;
        }

        // get media player and check if a playback is actually running
        TrackScheduler scheduler = this.audioController.getSchedulerForGuild(guild);
        AudioPlayer player = this.audioController.getPlayerForGuild(guild);

        if (scheduler.getNextTrack() == null && !scheduler.isPlaying()) {
            hook.sendMessageEmbeds(
                RichEmbedBuilder.simple("There is nothing that can be stopped or the current playback is already stopped.").build()
            ).queue();
            return;
        }

        // prevent index out of bounds
        var queue = scheduler.getQueue();
        if (amount > queue.size()) amount = scheduler.getQueue().size();

        // skips songs
        for (int i = 1; i < amount; i++) {
            queue.remove(0); //NOSONAR - intended
        }

        player.stopTrack();
        hook.sendMessageEmbeds(
            RichEmbedBuilder.simple("Okay, I skipped " + amount + " tracks.").build()
        ).queue();
    }
}
