package tn.amin.mpro.constants;

import tn.amin.mpro.utils.XposedHilfer;

public class ReflectedClasses {
    // Android and Kotlin classes
    public Class<?> X_ListenerInfo;
    public Class<?> X_ResourcesImpl;
    public Class<?> X_RegularImmutableList;
    // Messenger classes
    public Class<?> X_ComponentHost;
    public Class<?> X_ContainerView;
    public Class<?> X_OneLineComposerView;
    public Class<?> X_MediaResource;
    public Class<?> X_ComposeFragment;
    public Class<?> X_CommandInterface;
    public Class<?> X_Message;
    public Class<?> X_FbDraweeView;
    // Obfuscated Messenger classes (I Guessed their names)
    public Class<?> X_FancyDialogBuilderHelper;
    public Class<?> X_FancyDialogBuilder;
    public Class<?> X_FancyDialogColorApplier;
    public Class<?> X_LoadingDialog;
    public Class<?> X_MUtilities;
    public Class<?> X_ButtonClicked;
    public Class<?> X_MentionsSearchAdapter;
    public Class<?> X_MediaResourceHelper;
    public Class<?> X_MediaResourceInitilizer;
    public Class<Enum> X_MediaResourceType;
    public Class<?> X_MoreDrawerGenericGridItemData;
    public Class<?> X_MoreDrawerGenericGridItemDataStore;
    public Class<?> X_MoreDrawerGenericGridItemDataStoreInit;
    public Class<Enum> X_IconType;
    public Class<Enum> X_ColorType;
    
    public void init() {
        // View.ListenerInfo is private
        X_ListenerInfo = XposedHilfer.findClass("android.view.View$ListenerInfo");
        X_ContainerView = XposedHilfer.findClass("com.facebook.messaging.composer.ComposerBarEditorActionBarContainerView");
        X_ComponentHost = XposedHilfer.findClass("com.facebook.litho.ComponentHost");
        X_OneLineComposerView = XposedHilfer.findClass("com.facebook.messaging.composer.OneLineComposerView");
        X_MediaResource = XposedHilfer.findClass("com.facebook.ui.media.attachments.model.MediaResource");
        X_FancyDialogBuilderHelper = XposedHilfer.findClass("X.G4p");
        X_FancyDialogBuilder = XposedHilfer.findClass("X.GPo");
        X_FancyDialogColorApplier = XposedHilfer.findClass("X.2tN");
        X_LoadingDialog = XposedHilfer.findClass("X.AGw");
        X_MUtilities = XposedHilfer.findClass("X.0rQ");
        X_ButtonClicked = XposedHilfer.findClass("X.1pP");
        X_ResourcesImpl = XposedHilfer.findClass("android.content.res.ResourcesImpl");
        X_ComposeFragment = XposedHilfer.findClass("com.facebook.messaging.composer.ComposeFragment");
        X_CommandInterface = XposedHilfer.findClass("X.5WC");
        X_FbDraweeView = XposedHilfer.findClass("com.facebook.drawee.fbpipeline.FbDraweeView");
        X_Message = XposedHilfer.findClass("com.facebook.messaging.model.messages.Message");
        X_MediaResourceHelper = XposedHilfer.findClass("X.2Pd");
        X_MediaResourceInitilizer = XposedHilfer.findClass("X.48l");
        X_MediaResourceType = (Class<Enum>) XposedHilfer.findClass("X.2Qk");
        X_RegularImmutableList = XposedHilfer.findClass("com.google.common.collect.RegularImmutableList");
        X_MentionsSearchAdapter = XposedHilfer.findClass("X.4iE");
        X_MoreDrawerGenericGridItemData = XposedHilfer.findClass("X.ARa");
        X_MoreDrawerGenericGridItemDataStore = XposedHilfer.findClass("X.2xK");
        X_MoreDrawerGenericGridItemDataStoreInit = XposedHilfer.findClass("X.2xJ");
        X_IconType = (Class<Enum>) XposedHilfer.findClass("X.1Rz");
        X_ColorType = (Class<Enum>) XposedHilfer.findClass("X.1kI");
    }
}
