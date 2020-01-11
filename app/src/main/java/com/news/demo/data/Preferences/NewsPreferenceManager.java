package com.news.demo.data.Preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.news.demo.NewsApplication;

public class NewsPreferenceManager {
    private final static String PREF_FILE = "NewsPreference";

    private SharedPreferences mSharedPreferences;
    private static volatile NewsPreferenceManager sPreferencesManagerInstance;

    private NewsPreferenceManager() {
        mSharedPreferences = NewsApplication.getContext().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        if (sPreferencesManagerInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static synchronized NewsPreferenceManager getInstance() {
        if (sPreferencesManagerInstance == null) { //if there is no instance available... create new one
            synchronized (NewsPreferenceManager.class) {
                if (sPreferencesManagerInstance == null) sPreferencesManagerInstance = new NewsPreferenceManager();
            }
        }
        return sPreferencesManagerInstance;
    }
    //Make singleton from serialize and deserialize operation.
    protected NewsPreferenceManager readResolve() {
        return getInstance();
    }

    /**
     * Set a string shared preference
     *
     * @param key   - Key to set shared preference
     * @param value - Value for the key
     */
    public void setString(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * Get a string shared preference
     *
     * @param key      - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference
     *                 isn't found.
     * @return value - String containing value of the shared preference if
     * found.
     */
    public String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public long getLong(String key) {
        return getLong(key, -1);
    }

    public int getInt(String key) {
        return getInt(key, -1);
    }
}
