package space.parzival.shardbot.commands.music;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import space.parzival.shardbot.exceptions.CommandExecutionException;
import space.parzival.shardbot.music.AudioControl;
import space.parzival.shardbot.music.TrackScheduler;
import space.parzival.shardbot.types.Command;

@Service
public class Stop extends Command {

    @Autowired
    private AudioControl audioController;
    
    public Stop() {
        super();
        super.name = "stop";
        super.description = "Stops the current playback and clears the playlist.";

        // required for every command => override execute function
        super.executingInstance = this;
    }


    @Override
    public void execute(JDA client, SlashCommandInteractionEvent event, InteractionHook hook) throws CommandExecutionException {

        Guild guild = event.getGuild();
        Member member = event.getMember();
        
        // is this actually run in a guild?
        if (member == null || guild == null) {
            hook.sendMessage(
                "Sorry, this command only works in a server."
            ).queue();
            return;
        }

        // get media player and check if a playback is actually running
        TrackScheduler scheduler = this.audioController.getSchedulerForGuild(guild);
        AudioPlayer player = this.audioController.getPlayerForGuild(guild);

        if (scheduler.getNextTrack() == null && !scheduler.isPlaying()) {
            hook.sendMessage(
                "There is nothing that can be stopped or the current playback is already stopped."
            ).queue();
            return;
        }

        scheduler.clearQueue();
        player.stopTrack();
        hook.sendMessage(
            "Okay, I stopped the current playback."
        ).queue();
    }
}
