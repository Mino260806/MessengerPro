package tn.amin.mpro.features.biometric;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.biometrics.BiometricPrompt.AuthenticationCallback;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import tn.amin.mpro.MProMain;
import tn.amin.mpro.utils.XposedHilfer;

public class BiometricConversationLock {
    public ArrayList<String> mLockedThreadKeys;

    public BiometricConversationLock(ArrayList<String> lockedThreadKeys) {
        if (lockedThreadKeys == null)
            lockedThreadKeys = new ArrayList();
        mLockedThreadKeys = lockedThreadKeys;
    }

    public BiometricConversationLock() { mLockedThreadKeys = new ArrayList(); }

    public boolean lockConversation(String threadKey) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            BiometricPrompt.Builder biometricPromptBuilder = new BiometricPrompt.Builder(MProMain.getContext())
                    .setTitle("Confirm identity")
                    .setDescription("Confirm that you are the owner of the device");
            setNegativeButtonOrCredentials(biometricPromptBuilder);
            authenticate(biometricPromptBuilder.build(), new ConversationLockCallback(threadKey));
            return true;
        }
        else {
            return false;
        }
    }

    public boolean unlockConversation(String threadKey) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            BiometricPrompt.Builder biometricPromptBuilder = new BiometricPrompt.Builder(MProMain.getContext())
                    .setTitle("Confirm identity")
                    .setDescription("Confirm that you are the owner of the device");
            setNegativeButtonOrCredentials(biometricPromptBuilder);
            authenticate(biometricPromptBuilder.build(), new ConversationUnlockCallback(threadKey));
            return true;
        }
        else {
            XposedBridge.log(new UnknownError());
            return false;
        }
    }

    public void accessConversation(XC_MethodHook.MethodHookParam param) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            BiometricPrompt.Builder biometricPromptBuilder = new BiometricPrompt.Builder(MProMain.getContext())
                    .setTitle("Requires authentication")
                    .setDescription("This conversation is locked");
            setNegativeButtonOrCredentials(biometricPromptBuilder);
            authenticate(biometricPromptBuilder.build(), new ConversationAccessCallback(param));
        }
    }

    public boolean isConversationLocked(String threadKey) {
        return mLockedThreadKeys.contains(threadKey);
    }

    public ArrayList<String> getLockedThreadKeys() { return mLockedThreadKeys; }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private static void setNegativeButtonOrCredentials(BiometricPrompt.Builder builder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            builder.setAllowedAuthenticators(BIOMETRIC_STRONG | BIOMETRIC_WEAK | DEVICE_CREDENTIAL);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setDeviceCredentialAllowed(true);
        }
        else {
            builder.setNegativeButton("Cancel",
                    ContextCompat.getMainExecutor(MProMain.getContext()),
                    (dialogInterface, i) -> {});
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private static void authenticate(BiometricPrompt biometricPrompt, AuthenticationCallback callback) {
        biometricPrompt.authenticate(new CancellationSignal(),
                ContextCompat.getMainExecutor(MProMain.getContext()),
                callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static class ConversationAccessCallback extends AuthenticationCallback {

        XC_MethodHook.MethodHookParam mParam;

        public ConversationAccessCallback(XC_MethodHook.MethodHookParam methodHookParam) {
            mParam = methodHookParam;
        }
        @Override
        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
            try {
                XposedHilfer.invokeOriginalMethod(mParam);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public class ConversationLockCallback extends AuthenticationCallback {

        private String mThreadKey;

        public ConversationLockCallback(String threadKey) {
            mThreadKey = threadKey;
        }

        @Override
        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
            try {
                mLockedThreadKeys.add(mThreadKey);
                Toast.makeText(MProMain.getContext(), "Conversation locked", Toast.LENGTH_SHORT).show();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public class ConversationUnlockCallback extends AuthenticationCallback {

        private String mThreadKey;

        public ConversationUnlockCallback(String threadKey) {
            mThreadKey = threadKey;
        }
        @Override
        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
            try {
                mLockedThreadKeys.remove(mThreadKey);
                Toast.makeText(MProMain.getContext(), "Conversation unlocked", Toast.LENGTH_SHORT).show();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

    }

    private static boolean isAuthenticationAvailable() {
        //        BiometricManager biometricManager = BiometricManager.from(MProMain.getContext());
        //        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
        //            case BiometricManager.BIOMETRIC_SUCCESS:
        //                return true;
        //            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
        //            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
        //                return false;
        //            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
        //                // Prompts the user to create credentials that your app accepts.
        //                final Intent enrollIntent;
        //                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        //                    enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
        //                    enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
        //                            BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
        //                    startActivityForResult(enrollIntent, REQUEST_CODE);
        //                }
        //                break;
        //        }
        return true;
    }
}
