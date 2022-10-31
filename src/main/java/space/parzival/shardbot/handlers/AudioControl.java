package space.parzival.shardbot.handlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

@Service
public class AudioControl {
    private Map<Guild, AudioPlayer> playerStore;
    private Map<Guild, TrackScheduler> schedulerStore;
    private Map<Guild, AudioPlayerSendHandler> sendHandlerStore;

    @Getter
    private AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    public AudioControl() {
        this.playerStore = new HashMap<>();
        this.schedulerStore = new HashMap<>();
        this.sendHandlerStore = new HashMap<>();

        AudioSourceManagers.registerRemoteSources(playerManager);
        this.playerManager.getConfiguration().setFilterHotSwapEnabled(true);
        this.playerManager.getConfiguration().setOpusEncodingQuality(10); // set to max
    }

    /**
     * Loads or registers a audio player for the specific guild.
     * @param guild
     * @return
     */
    public AudioPlayer getPlayerForGuild(Guild guild) {
        if (!this.playerStore.containsKey(guild)) {
            AudioPlayer player = this.playerManager.createPlayer();

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
     * @param guiled
     * @return
     */
    public AudioPlayerSendHandler getSendHandlerForGuild(Guild guild) {
        if (!this.sendHandlerStore.containsKey(guild)) {
            AudioPlayerSendHandler handler = new AudioPlayerSendHandler(this.getPlayerForGuild(guild));
            this.sendHandlerStore.put(guild, handler);
        }

        return this.sendHandlerStore.get(guild);
    }
}
