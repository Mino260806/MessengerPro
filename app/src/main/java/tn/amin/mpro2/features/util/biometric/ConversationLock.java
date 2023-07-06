package tn.amin.mpro2.features.util.biometric;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.CancellationSignal;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class ConversationLock {
    private long lastAuthTime = 0;

    public ConversationLock() {

    }

    public boolean promptAuthentication(Context context, Runnable onSuccess, Runnable onFail, boolean alwaysRun) {
        long time = System.currentTimeMillis();
        if (time - lastAuthTime < 1000) {
            lastAuthTime = time;
            if (alwaysRun) {
                onSuccess.run();
            }
            return true;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            Executor executor = ContextCompat.getMainExecutor(context);
            BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(context)
                    .setTitle("Biometric Authentication")
                    .setSubtitle("Authenticate using your biometric credential")
                    .setDescription("Place your finger on the fingerprint scanner")
                    .setNegativeButton("Cancel", context.getMainExecutor(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(context, "Authentication cancelled", Toast.LENGTH_SHORT).show();

                            onFail.run();
                        }
                    })
                    .build();

            CancellationSignal cancellationSignal = new CancellationSignal();

            biometricPrompt.authenticate(cancellationSignal, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(context,
                            "Authentication error: " + errString, Toast.LENGTH_SHORT)
                            .show();

                    onFail.run();
                }

                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(context,
                            "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                    lastAuthTime = System.currentTimeMillis();

                    onSuccess.run();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(context, "Authentication failed",
                            Toast.LENGTH_SHORT)
                            .show();

                    onFail.run();
                }
            });
            return false;
        }
        return true;
    }
}
