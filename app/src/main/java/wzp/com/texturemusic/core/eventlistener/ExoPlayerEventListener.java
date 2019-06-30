package wzp.com.texturemusic.core.eventlistener;


import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.BandwidthMeter;

/**
 * Created by Go_oG
 * Description:一个抽象类用于减少接口的方法
 * on 2018/2/3.
 * 实现了Exoplayer的部分监听器
 */

public abstract class ExoPlayerEventListener implements Player.EventListener, AudioListener,
        BandwidthMeter.EventListener{
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {


    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onSeekProcessed() {
    }

    @Override
    public void onAudioSessionId(int audioSessionId) {

    }

    @Override
    public void onBandwidthSample(int elapsedMs, long bytes, long bitrate) {

    }

    @Override
    public void onAudioAttributesChanged(AudioAttributes audioAttributes) {

    }

    @Override
    public void onVolumeChanged(float volume) {

    }
}
