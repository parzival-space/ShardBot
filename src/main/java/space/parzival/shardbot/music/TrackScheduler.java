package space.parzival.shardbot.music;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.ser.std.ToEmptyObjectSerializer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Slf4j
public class TrackScheduler extends AudioEventAdapter {

    private List<AudioTrack> queue = new ArrayList<>();

    @Getter
    private boolean playing = false; // TODO: implement pause and stuff

    @Setter
    /**
     * Specifies the channel in wich the "Now playing..." messages will be send.
     * If null then no messages will be send.
     */
    private @Nullable MessageChannelUnion notifyChannel = null;


    /**
     * Add a {@see Track} to the playlist.
     * @param track The Track you want to add.
     */
    public void queueTrack(AudioTrack track) {
        this.queue.add(track);
    }

    /**
     * Return the next Track that is going to be played.
     * @return null if nothing is in queue.
     */
    public @Nullable AudioTrack getNextTrack() {
        if (this.queue.isEmpty()) 
            return null;
        else
            return queue.get(0);
    }


    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        log.info("Now playing '{}'", track.getInfo().title);

        // remove current entry
        queue.remove(0);
        playing = true;

        MessageChannelUnion messageChannel = this.notifyChannel;
        if (messageChannel != null) messageChannel.sendMessage("Now playing: " + track.getInfo().title).queue();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext && !queue.isEmpty()) {
            player.playTrack(queue.get(0));
            return;
        }

        else if (endReason == AudioTrackEndReason.STOPPED) {
            this.queue.clear();
        }

        // all songs have finished
        playing = false;

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext
        // = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not
        // finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you
        // can put a
        // clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be
        // received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // stuck
    }
}