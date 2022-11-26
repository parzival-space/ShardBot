package space.parzival.shardbot.modules.music;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

@Service
public class AudioControl {
    private Map<Guild, AudioPlayer> playerStore = new HashMap<>();
    private Map<Guild, TrackScheduler> schedulerStore = new HashMap<>();
    private Map<Guild, AudioPlayerSendHandler> sendHandlerStore = new HashMap<>();
    private Map<Guild, EqualizerFactory> equalizerStore = new HashMap<>();

    @Getter
    private AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    /**
     * Baiscally a in-memory Database that manages {@see AudioPlayer}s, {@see TrackScheduler}s 
     * and {@see AudioPlayerSendHandler}s for multiple {@see Guild}s.
     * <p>
     * Multi-Guild-Bot lets gooo!!
     */
    public AudioControl() {
        AudioSourceManagers.registerRemoteSources(playerManager);

        this.playerManager.getConfiguration().setFilterHotSwapEnabled(true);
        this.playerManager.getConfiguration().setOpusEncodingQuality(10); // set to max

        // TODO: implement YoutubeAudioSourceManager
    }

    /**
     * Loads or registers a audio player for the specific guild.
     * @param guild
     * @return
     */
    public AudioPlayer getPlayerForGuild(Guild guild) {
        if (!this.playerStore.containsKey(guild)) {
            AudioPlayer player = this.playerManager.createPlayer();

            // default volume
            player.setVolume(8);

            player.addListener(this.getSchedulerForGuild(guild));
            this.playerStore.put(guild, player);
        }

        return this.playerStore.get(guild);
    }

    /**
     * Loads or registers a track scheduler for the specific guild.
     * @param guild
     * @param notifyChannel
     * @return
     */
    public TrackScheduler getSchedulerForGuild(Guild guild) {
        return this.schedulerStore.computeIfAbsent(guild, v -> new TrackScheduler());
    }

    /**
     * Loads or registers a sound handler for the specific guild.
     * @param guild
     * @return
     */
    public AudioPlayerSendHandler getSendHandlerForGuild(Guild guild) {
        if (!this.sendHandlerStore.containsKey(guild)) {
            AudioPlayerSendHandler handler = new AudioPlayerSendHandler(this.getPlayerForGuild(guild));
            this.sendHandlerStore.put(guild, handler);
        }

        return this.sendHandlerStore.get(guild);
    }

    /**
     * Loads or regusters the equalizer settings of a guild.
     * @param guild
     * @return
     */
    public EqualizerFactory getEqualizerForGuild(Guild guild) {
        if (!this.equalizerStore.containsKey(guild)) {
            EqualizerFactory equalizer = new EqualizerFactory();

            AudioPlayer player = this.getPlayerForGuild(guild);
            player.setFilterFactory(equalizer);
            player.setFrameBufferDuration(500); // prevent equalizer taking time to take effect

            // TODO: load last set filter from database or seomthing like that

            this.equalizerStore.put(guild, equalizer);
        }

        return this.equalizerStore.get(guild);
    }
}
