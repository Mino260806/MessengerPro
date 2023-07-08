package tn.amin.mpro2.features;

import tn.amin.mpro2.features.action.AttachFileFeature;
import tn.amin.mpro2.features.action.ConversationLockFeature;
import tn.amin.mpro2.features.action.CopyThreadKeyFeature;
import tn.amin.mpro2.features.action.SettingsFeature;
import tn.amin.mpro2.features.internal.ThreadKeyDetectorFeature;
import tn.amin.mpro2.features.state.CommandsFeature;
import tn.amin.mpro2.features.state.DefaultCameraFeature;
import tn.amin.mpro2.features.state.FormattingFeature;
import tn.amin.mpro2.features.state.UnseenFeature;
import tn.amin.mpro2.features.state.UntypingFeature;
import tn.amin.mpro2.features.tasker.TaskerEventMessageFeature;
import tn.amin.mpro2.features.tasker.TaskerEventTypingIndicatorFeature;
import tn.amin.mpro2.hook.HookManager;
import tn.amin.mpro2.orca.OrcaGateway;

public class MProFeatureManager extends FeatureManager {
    public MProFeatureManager(HookManager hookManager) {
        super(hookManager);
    }

    public void initFeatures(OrcaGateway gateway) {
        // Internal features
        addFeature(new ThreadKeyDetectorFeature(gateway));

        // Action features
        addFeature(new AttachFileFeature(gateway));
        addFeature(new CopyThreadKeyFeature(gateway));
        addFeature(new ConversationLockFeature(gateway));
        addFeature(new SettingsFeature(gateway));

        // Normal features
        addFeature(new CommandsFeature(gateway));
        addFeature(new FormattingFeature(gateway));
        addFeature(new UnseenFeature(gateway));
        addFeature(new UntypingFeature(gateway));
        addFeature(new DefaultCameraFeature(gateway));

        // Tasker features
        addFeature(new TaskerEventMessageFeature(gateway));
        addFeature(new TaskerEventTypingIndicatorFeature(gateway));
    }
}
