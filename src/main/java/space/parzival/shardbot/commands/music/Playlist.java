package space.parzival.shardbot.commands.music;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

@Component
public class Playlist extends Command {

    @Autowired
    private AudioControl audioController;
    
    public Playlist() {
        super();
        super.name = "playlist";
        super.description = "Displays the current song queue.";

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

        if (scheduler.getQueue().isEmpty() && !scheduler.isPlaying()) {
            hook.sendMessage(
                "The current playlist is empty."
            ).queue();
            return;
        }

        StringBuilder playlist = new StringBuilder();

        playlist.append("Current Track: " + player.getPlayingTrack().getInfo().title + " by " + player.getPlayingTrack().getInfo().author + "\n");
        for (var track : scheduler.getQueue()) {
            String nextTrack = "- " + track.getInfo().title + " by " + track.getInfo().author + "\n";
            if (playlist.length() + nextTrack.length() < 1500) {
                playlist.append(nextTrack);
            }
        }

        playlist.append("There may be more songs wich cannot be displayed right now.");

        String message = playlist.toString();
        if (message == null) {
            return;
        }

        player.setPaused(false);
        hook.sendMessage(
            message
        ).queue();
    }
}
