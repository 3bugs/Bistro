package th.ac.dusit.dbizcom.bistro.etc;

import android.content.Context;
import android.content.SharedPreferences;

import th.ac.dusit.dbizcom.bistro.model.User;

public class MyPrefs {

    private static final int INVALID_USER_ID = -1;

    private static final String KEY_PREF_FILE = "pref_file";
    private static final String KEY_USER_ID = "user_id_pref";
    private static final String KEY_USER_NAME = "user_name_pref";
    private static final String KEY_USER_EMAIL = "user_email_pref";
    private static final String KEY_USER_CREATED_AT = "user_created_at_pref";

    private static SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences( //todo: บางครั้ง context is null ?!?
                KEY_PREF_FILE, Context.MODE_PRIVATE
        );
    }

    public static void setUserPref(Context context, User user) {
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putInt(KEY_USER_ID, user == null ? INVALID_USER_ID : user.id);
        editor.putString(KEY_USER_NAME, user == null ? "" : user.name);
        editor.putString(KEY_USER_EMAIL, user == null ? "" : user.email);
        editor.putString(KEY_USER_CREATED_AT, user == null ? "" : user.createdAt);
        editor.apply();
    }

    public static User getUserPref(Context context) {
        int userId = getSharedPref(context).getInt(KEY_USER_ID, INVALID_USER_ID);
        if (userId == INVALID_USER_ID) {
            return null;
        } else {
            String name = getSharedPref(context).getString(KEY_USER_NAME, "");
            String email = getSharedPref(context).getString(KEY_USER_EMAIL, "");
            String createdAt = getSharedPref(context).getString(KEY_USER_CREATED_AT, "");
            return new User(userId, name, email, createdAt);
        }
    }
}