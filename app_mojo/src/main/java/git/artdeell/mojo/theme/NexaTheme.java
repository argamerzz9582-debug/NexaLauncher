package git.artdeell.mojo.theme;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.preference.PreferenceManager;

import java.io.InputStream;

import git.artdeell.mojo.MojoApplication;
import git.artdeell.mojo.R;
import git.artdeell.mojo.Tools;

/** Launcher-only appearance preferences; never changes the game rendering surface. */
public final class NexaTheme {
    public static final String PRESET = "nexa_theme_preset";
    public static final String WALLPAPER = "nexa_wallpaper_uri";
    private static final String TAG = "NexaTheme";
    private NexaTheme() {}

    public static void apply(Context context, View root) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String preset = prefs.getString(PRESET, "midnight");
        int background;
        switch (preset) {
            case "arctic": background = R.drawable.nexa_background_arctic; break;
            case "cyber": background = R.drawable.nexa_background_cyber; break;
            case "space": background = R.drawable.nexa_background_space; break;
            case "silver": background = R.drawable.nexa_background_silver; break;
            default: background = R.drawable.nexa_background;
        }
        root.setBackgroundResource(background);
        ImageView image = root.findViewById(R.id.nexa_wallpaper);
        View dim = root.findViewById(R.id.nexa_wallpaper_dim);
        image.setImageDrawable(null);
        dim.setVisibility(View.GONE);
        String value = prefs.getString(WALLPAPER, "");
        if (value == null || value.isEmpty()) return;
        Context app = context.getApplicationContext();
        Uri uri = Uri.parse(value);
        MojoApplication.sExecutorService.execute(() -> {
            Bitmap bitmap = null;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                try (InputStream in = app.getContentResolver().openInputStream(uri)) {
                    if (in == null) throw new java.io.IOException("Wallpaper unavailable");
                    BitmapFactory.decodeStream(in, null, options);
                }
                if (options.outWidth <= 0 || options.outHeight <= 0) throw new java.io.IOException("Unsupported wallpaper image");
                ActivityManager manager = (ActivityManager) app.getSystemService(Context.ACTIVITY_SERVICE);
                int limit = manager != null && manager.isLowRamDevice() ? 512 : 1024;
                options.inSampleSize = 1;
                while (options.outWidth / options.inSampleSize > limit || options.outHeight / options.inSampleSize > limit) {
                    options.inSampleSize *= 2;
                }
                options.inJustDecodeBounds = false;
                try (InputStream in = app.getContentResolver().openInputStream(uri)) {
                    if (in == null) throw new java.io.IOException("Wallpaper unavailable");
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                }
            } catch (Exception | OutOfMemoryError e) {
                Log.w(TAG, "Could not load wallpaper; using preset background", e);
            }
            final Bitmap result = bitmap;
            Tools.runOnUiThread(() -> {
                // Discard stale loads after a new selection or view destruction.
                if (!root.isAttachedToWindow() || !value.equals(prefs.getString(WALLPAPER, ""))) return;
                if (result != null) {
                    image.setImageBitmap(result);
                    dim.setVisibility(View.VISIBLE);
                }
            });
        });
    }
}
