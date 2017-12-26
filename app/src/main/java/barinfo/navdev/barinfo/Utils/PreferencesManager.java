package barinfo.navdev.barinfo.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static PreferencesManager sInstance;
    private final SharedPreferences mPref;

    private PreferencesManager(Context context) {
        mPref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesManager(context);
        }
    }

    public static synchronized PreferencesManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(PreferencesManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    public void setValue(String key, long value) {
        mPref.edit()
                .putLong(key, value)
                .apply();
    }

    public long getValue(String key) {
        return mPref.getLong(key, 0);
    }

    public void remove(String key) {
        mPref.edit()
                .remove(key)
                .apply();
    }

    public boolean clear() {
        return mPref.edit()
                .clear()
                .commit();
    }
}
