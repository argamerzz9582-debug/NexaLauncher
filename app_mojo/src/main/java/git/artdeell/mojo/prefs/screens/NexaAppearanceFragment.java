package git.artdeell.mojo.prefs.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import git.artdeell.mojo.R;
import git.artdeell.mojo.theme.NexaTheme;

/** Appearance options scoped to the launcher, independent of game rendering. */
public class NexaAppearanceFragment extends LauncherPreferenceFragment {
    private final ActivityResultLauncher<String[]> wallpaperPicker = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null || !isAdded()) return;
                try {
                    requireContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
                    if (prefs == null) return;
                    String old = prefs.getString(NexaTheme.WALLPAPER, "");
                    prefs.edit().putString(NexaTheme.WALLPAPER, uri.toString()).apply();
                    if (old != null && !old.isEmpty() && !old.equals(uri.toString())) release(Uri.parse(old));
                    Toast.makeText(requireContext(), R.string.nexa_wallpaper_saved, Toast.LENGTH_SHORT).show();
                } catch (SecurityException e) {
                    Toast.makeText(requireContext(), R.string.nexa_wallpaper_error, Toast.LENGTH_LONG).show();
                }
            });

    @Override public void onCreatePreferences(Bundle state, String rootKey) {
        addPreferencesFromResource(R.xml.pref_nexa_appearance);
        requirePreference("nexa_select_wallpaper").setOnPreferenceClickListener(p -> {
            wallpaperPicker.launch(new String[]{"image/*"});
            return true;
        });
        requirePreference("nexa_reset_wallpaper").setOnPreferenceClickListener(p -> {
            SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
            if (prefs != null) {
                String old = prefs.getString(NexaTheme.WALLPAPER, "");
                prefs.edit().remove(NexaTheme.WALLPAPER).apply();
                if (old != null && !old.isEmpty()) release(Uri.parse(old));
            }
            return true;
        });
    }

    private void release(Uri uri) {
        try {
            requireContext().getContentResolver().releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException e) {
            android.util.Log.w("NexaTheme", "Could not release old wallpaper permission", e);
        }
    }
}
