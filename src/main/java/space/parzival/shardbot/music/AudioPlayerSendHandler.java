package space.parzival.shardbot.music;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;
  
    /**
     * This class acts as a Man-In-The-Middle converter for LavaPlayer
     * @param audioPlayer
     */
    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
      this.audioPlayer = audioPlayer;
    }
  
    @Override
    public boolean canProvide() {
      lastFrame = audioPlayer.provide();
      return lastFrame != null;
    }
  
    @Override
    public ByteBuffer provide20MsAudio() {
      return ByteBuffer.wrap(lastFrame.getData());
    }
  
    @Override
    public boolean isOpus() {
      return true;
    }
  }