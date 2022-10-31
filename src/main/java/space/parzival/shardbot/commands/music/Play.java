package space.parzival.shardbot.commands.music;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import space.parzival.shardbot.exceptions.CommandExecutionException;
import space.parzival.shardbot.music.AudioControl;
import space.parzival.shardbot.music.TrackScheduler;
import space.parzival.shardbot.types.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
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

        // read the options
        OptionMapping url = event.getOption("url");

        // fetch event data
        Member member = event.getMember();
        Guild guild = event.getGuild();

        // make sure the option is specified
        if (url == null) return;

        // is this actually run in a guild?
        if (member == null || guild == null) {
            hook.sendMessage(
                "Sorry, this command only works in a server."
            ).queue();
            return;
        }

        // make sure the option data contains a valid url (regex magic)
        if (!url.getAsString().matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
            hook.sendMessage(
                "The URL you entered seems to be invalid."
            ).queue();
            return;
        }
        
        // make sure command issuer is in a channel
        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState == null || !voiceState.inAudioChannel()) {
            hook.sendMessage(
                "It looks like you are not in a Voice Channel.\n" +
                "You cant use this command if you are not in a Voice Channel."
            ).queue();
            return;
        }

        // get audio channel
        AudioChannel audioChannel = voiceState.getChannel();
        if (audioChannel == null) {
            hook.sendMessage(
                "I can not access your Audio Channel.\n" +
                "This is an internal error! Please contact the developer."
            ).queue();
            return;
        }
        
        // connect to voice
        VoiceChannel channel = guild.getVoiceChannelById(audioChannel.getIdLong());
        AudioManager audioManager = guild.getAudioManager();
        audioManager.openAudioConnection(channel);

        

        // resolve song
        AudioPlayerManager manager = this.audioController.getPlayerManager();
        manager.loadItem(url.getAsString(), new AudioLoadResultHandler() {

            @Override
            public void loadFailed(FriendlyException err) { 
                // failed to load track
                event.getHook().sendMessage(
                    "Could not load your Track:\n" + err.getMessage()
                ).queue();
            }

            @Override
            public void noMatches() { 
                // no valid track found
                event.getHook().sendMessage(
                    "Could not find Track"
                ).queue();
             }

            @Override
            public void playlistLoaded(AudioPlaylist tracks) {
                tracks.getTracks().forEach(this::loadTrack);
                
                // added track
                event.getHook().sendMessage(
                    "Added Playlist to the queue."
                ).queue();
            }

            @Override
            public void trackLoaded(AudioTrack track) {
                this.loadTrack(track);
                
                // added track
                event.getHook().sendMessage(
                    "Added Track to the queue."
                ).queue();
            }

            private void loadTrack(AudioTrack track) {
                
                // set handler
                AudioManager audioManager = guild.getAudioManager();
                audioManager.setSendingHandler(audioController.getSendHandlerForGuild(event.getGuild()));

                // add to track scheduler
                TrackScheduler scheduler = audioController.getSchedulerForGuild(event.getGuild());
                AudioPlayer player = audioController.getPlayerForGuild(event.getGuild());
                scheduler.setNotifyChannel(event.getChannel());
                scheduler.queueTrack(track);

                if (!scheduler.isPlaying() && !player.isPaused()) player.playTrack(scheduler.getNextTrack());
            }
            
        });


    }
}
