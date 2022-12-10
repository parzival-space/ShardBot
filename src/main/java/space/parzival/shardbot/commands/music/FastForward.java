package space.parzival.shardbot.commands.music;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import space.parzival.discord.shared.base.exceptions.CommandExecutionException;
import space.parzival.shardbot.modules.music.AudioControl;
import space.parzival.shardbot.modules.music.TrackScheduler;
import space.parzival.discord.shared.base.types.Command;
import space.parzival.discord.shared.base.types.RichEmbedBuilder;

@Component
public class FastForward extends Command {

    @Autowired
    private AudioControl audioController;
    
    public FastForward() {
        super("ff", "Fast forward the current playback.");

        super.options.add(new OptionData(OptionType.INTEGER, "seconds", "The amount of seconds to fast forward.", true));
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

        if (!scheduler.isPlaying() || player.isPaused()) {
            hook.sendMessageEmbeds(
                RichEmbedBuilder.simple("There is nothing that can be fast forwarded or the current playback is already paused.").build()
            ).queue();
            return;
        }

        long timing = 0;
        AudioTrack currentTrack = player.getPlayingTrack();
        OptionMapping secondsMapping = event.getOption("seconds");

        if (secondsMapping != null) timing = secondsMapping.getAsInt() * 1000L;
        
        // check timings
        if (timing > currentTrack.getDuration()) {
            player.stopTrack();
        }
        else if (currentTrack.getPosition() - timing < 0 && timing < 0) {
            currentTrack.setPosition(0);
        } else {
            currentTrack.setPosition(currentTrack.getPosition() + timing);
        }

        hook.sendMessageEmbeds(
            RichEmbedBuilder.simple("Playback has been fast-forwarded by " + timing / 1000 + " seconds.").build()
        ).queue();
    }
}
