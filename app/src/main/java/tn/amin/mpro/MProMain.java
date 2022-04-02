package tn.amin.mpro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.takusemba.spotlight.OnSpotlightListener;
import com.takusemba.spotlight.OnTargetListener;
import com.takusemba.spotlight.Spotlight;
import com.takusemba.spotlight.Target;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import kotlin.NotImplementedError;
import tn.amin.mpro.builders.MediaResourceBuilder;
import tn.amin.mpro.constants.Constants;
import tn.amin.mpro.constants.ReflectedClasses;
import tn.amin.mpro.utils.file.FileHelper;

/**
 * This is the most important class in this module
 * It statically maps different high level functions to interface with Messenger
 */
public class MProMain {
    static MainHook mainHook;

    public static boolean formatNextMessage = true;
    private static final ReflectedClasses mReflectedClasses = new ReflectedClasses();

    public static void init(MainHook mainHook) {
        MProMain.mainHook = mainHook;
        mReflectedClasses.init();
    }

    public static void showCommandsAutoComplete(List<Object> commandsList) {
        if (commandsList == null || commandsList.size() == 0) {
            XposedHelpers.callMethod(
                    XposedHelpers.getObjectField(mainHook.getActiveCommandsParser(), "A0G"),
                    "A04");
            XposedHelpers.callStaticMethod(mainHook.getActiveCommandsParser().getClass(), "A04",
                    mainHook.getActiveCommandsParser(), null);
        }
        else {
            XposedHelpers.setObjectField(
                    mainHook.getActiveCommandsParser(), "A0I", null);
            XposedHelpers.callStaticMethod(mainHook.getActiveCommandsParser().getClass(), "A04",
                mainHook.getActiveCommandsParser(), commandsList);
            XposedHelpers.setBooleanField(mainHook.getActiveCommandsParser(), "A0P", true);
            XposedHelpers.callStaticMethod(mainHook.getActiveCommandsParser().getClass(), "A02",
                    mainHook.getActiveCommandsParser());
        }
    }

    public static void sendMessage(String message) {
        sendMessage(message, false);
    }

    public static void sendMessage(String message, boolean clearEditText) {
        XposedBridge.log("Sending message: " + message);

        EditText messageEdit = mainHook.getActiveMessageEdit();
        View sendButton = mainHook.getActiveSendButton();

        String oldMessage = "";
        if (!clearEditText) {
            // Backup old message
            oldMessage = messageEdit.getText().toString();
        }
        // Send message
        messageEdit.setText(message);
        formatNextMessage = false; // We don't want our message to be formatted
        sendButton.performClick();
        if (!clearEditText) {
            // Restore old message
            messageEdit.setText(oldMessage);
        }
    }

    public static void putAttachment(Object mediaResource) {
        mainHook.setPendingAttachment(mediaResource);
    }

    public static void sendAttachment(Object mediaResource) {
        putAttachment(mediaResource);
        sendMessage("attachment");
    }

    public static void clearMessageInput() {
        mainHook.getActiveMessageEdit().setText("");
    }

    public static void startFileChooser() {
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(
                Intent.createChooser(intent, "Select a file"),
                Constants.MPRO_ATTACHFILE_REQUEST_CODE
        );
    }

    public static void showTutorial(View targetView) {
        FrameLayout tutorialRoot = new FrameLayout(MProMain.getContext());
        int[] anchor = {0, 0};
        targetView.getLocationOnScreen(anchor);

        final Target target = new Target.Builder()
                .setAnchor(anchor[0], anchor[1])
                .setOverlay(tutorialRoot)
                .setOnTargetListener(new OnTargetListener() {
                    @Override
                    public void onStarted() {
                        Toast.makeText(
                                getActivity(),
                                "first target is started",
                                Toast.LENGTH_SHORT
                        ).show() ;
                    }
                    @Override
                    public void onEnded() {
                        Toast.makeText(
                                getActivity(),
                                "first target is ended",
                                Toast.LENGTH_SHORT
                        ).show() ;
                    }
                })
                .build();

        Spotlight spotlight = new Spotlight.Builder(getActivity())
                .setTargets(new ArrayList<>(Collections.singletonList(target)))
                .setBackgroundColor(Color.parseColor("#BF000000"))
                .setDuration(1000L)
                .setContainer(getContentView())
                .setAnimation(new DecelerateInterpolator(2f))
                .setOnSpotlightListener(new OnSpotlightListener() {
                    @Override
                    public void onStarted() {
                        Toast.makeText(getActivity(), "spotlight is started", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onEnded() {
                        Toast.makeText(getActivity(), "spotlight is ended", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        spotlight.start();
    }

    public static Activity getActivity() {
        return mainHook.getActivity();
    }
    public static Context getContext() {
        return mainHook.getContext();
    }
    public static ViewGroup getContentView() { return mainHook.O_contentView.get(); }
    public static ReflectedClasses getReflectedClasses() { return mReflectedClasses; }
    public static Resources getMProResources() { return mainHook.mResources; }
    public static PrefReader getPrefReader() { return mainHook.getPrefReader(); }
    public static ConversationMapper getConversationMapper() { return mainHook.getConversationMapper(); }

    public static boolean isDarkMode() {
        return (getContext().getResources().getConfiguration().uiMode & 48) == 32;
    }

    public static boolean isDarkMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & 48) == 32;
    }
}
