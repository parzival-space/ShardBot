package space.parzival.shardbot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import space.parzival.shardbot.exceptions.CommandExecutionException;
import space.parzival.shardbot.handlers.AudioControl;
import space.parzival.shardbot.handlers.TrackScheduler;
import space.parzival.shardbot.types.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

@Service
public class Play extends Command {

    @Autowired
    private AudioControl audioController;
    
    public Play() {
        super();
        super.name = "play";
        super.description = "Plays a YouTube Video or a Song";

        super.options.add(
            new OptionData(OptionType.STRING, "url", "The remote resource to play.", true)
        );

        // required for every command => override execute function
        super.executingInstance = this;
    }


    @Override
    public void execute(JDA client, SlashCommandInteractionEvent event, InteractionHook hook) throws CommandExecutionException {

        String url = event.getOption("url").getAsString();
        Member member = event.getMember();
        Guild guild = event.getGuild();

        // make sure the option data contains a valid url (regex magic)
        if (!url.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
            hook.sendMessage(
                "The URL you entered seems to be invalid."
            ).queue();
            return;
        }

        // make sure command issuer is in a channel
        if (member.getVoiceState() == null || !member.getVoiceState().inAudioChannel()) {
            hook.sendMessage(
                "It looks like you are not in a Voice Channel.\n" +
                "You cant use this command if you are not in a Voice Channel."
            ).queue();
            return;
        }
        
        // connect to voice
        VoiceChannel channel = guild.getVoiceChannelById(member.getVoiceState().getChannel().getIdLong());
        AudioManager audioManager = guild.getAudioManager();
        audioManager.openAudioConnection(channel);

        

        // resolve song
        AudioPlayerManager manager = this.audioController.getPlayerManager();
        manager.loadItem(url, new AudioLoadResultHandler() {

            @Override
            public void loadFailed(FriendlyException arg0) { sendLoadFailed(event, arg0); }

            @Override
            public void noMatches() { sendNoMatches(event); }

            @Override
            public void playlistLoaded(AudioPlaylist arg0) {
                arg0.getTracks().forEach(t -> doTrackLoad(event, t));
            }

            @Override
            public void trackLoaded(AudioTrack arg0) { doTrackLoad(event, arg0); }
            
        });


    }


    private void sendLoadFailed(SlashCommandInteractionEvent event, FriendlyException err) {
        event.getHook().sendMessage(
            "Could not load your Track:\n" + err.getMessage()
        ).queue();
    }
    private void sendNoMatches(SlashCommandInteractionEvent event) {
        event.getHook().sendMessage(
            "Could not find Track"
        ).queue();
    }

    private void doTrackLoad(SlashCommandInteractionEvent event, AudioTrack track) {
        // set handler
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.setSendingHandler(audioController.getSendHandlerForGuild(event.getGuild()));

        // add to track scheduler
        TrackScheduler scheduler = audioController.getSchedulerForGuild(event.getGuild());
        scheduler.setNotifyChannel(event.getChannel());
        scheduler.queueTrack(track);

        if (!scheduler.isPlaying()) audioController.getPlayerForGuild(event.getGuild()).playTrack(scheduler.getNextTrack());
    }
}
