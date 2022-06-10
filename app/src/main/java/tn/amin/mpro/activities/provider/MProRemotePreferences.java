package tn.amin.mpro.activities.provider;

import com.crossbowffs.remotepreferences.RemotePreferenceFile;
import com.crossbowffs.remotepreferences.RemotePreferenceProvider;

public class MProRemotePreferences extends RemotePreferenceProvider {
    public MProRemotePreferences() {
        super("tn.amin.mpro.preferences", new String[] { "pref_main" });
    }

    public MProRemotePreferences(String authority, String[] prefFileNames) {
        super(authority, prefFileNames);
    }

    public MProRemotePreferences(String authority, RemotePreferenceFile[] prefFiles) {
        super(authority, prefFiles);
    }
}
