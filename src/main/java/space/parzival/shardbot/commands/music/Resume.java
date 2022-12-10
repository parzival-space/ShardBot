package space.parzival.shardbot.commands.music;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import space.parzival.discord.shared.base.exceptions.CommandExecutionException;
import space.parzival.shardbot.modules.music.AudioControl;
import space.parzival.shardbot.modules.music.TrackScheduler;
import space.parzival.discord.shared.base.types.Command;
import space.parzival.discord.shared.base.types.RichEmbedBuilder;

@Component
public class Resume extends Command {

    @Autowired
    private AudioControl audioController;
    
    public Resume() {
        super("resume", "Resumes the current playback.");
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

        // get media player and check if a playback is actually running
        TrackScheduler scheduler = this.audioController.getSchedulerForGuild(guild);
        AudioPlayer player = this.audioController.getPlayerForGuild(guild);

        if (scheduler.isPlaying() || !player.isPaused()) {
            hook.sendMessageEmbeds(
                RichEmbedBuilder.simple("There is nothing that can be resumed or the current playback is not paused.").build()
            ).queue();
            return;
        }

        player.setPaused(false);
        hook.sendMessageEmbeds(
            RichEmbedBuilder.simple("Okay, I resume the current playback.").build()
        ).queue();
    }
}
