package tn.amin.mpro2.features.state;

import androidx.annotation.Nullable;

import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.MediaTranscoderHook;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.orca.OrcaGateway;

public class MediaTranscodeFeature extends Feature implements MediaTranscoderHook.MediaTranscodeHookListener {
    public MediaTranscodeFeature(OrcaGateway gateway) {
        super(gateway);
    }
    @Nullable
    @Override
    public String getPreferenceKey() {
        return "mpro_disable_media_transcoder";
    }
    @Override
    public FeatureId getId() { return FeatureId.MEDIA_TRANSCODER; }

    @Override
    public FeatureType getType() {
        return FeatureType.CHECKABLE_STATE;
    }

    @Override
    public HookId[] getHookIds() {
        return new HookId[] { HookId.MEDIA_TRANSCODER};
    }
    @Override
    public boolean isEnabledByDefault() {
        return false;
    }
    @Override
    public HookListenerResult<Boolean> onMediaTranscode() {
        Logger.verbose("isEnabled: " + isEnabled());
        return HookListenerResult.consume(true);
    }

}
