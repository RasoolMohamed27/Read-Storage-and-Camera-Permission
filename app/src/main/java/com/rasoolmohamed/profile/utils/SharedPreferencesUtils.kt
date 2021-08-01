package com.rasoolmohamed.profile.utils


import android.content.Context
import android.content.SharedPreferences
import com.rasoolmohamed.profile.R

/**
 * Created by rasoolmohamed on 8/3/18.
 */
object SharedPreferencesUtils {
    private val TAG = SharedPreferencesUtils::class.java.canonicalName
    private val mSharedPreferences: SharedPreferences? = null
    private val mEditor: SharedPreferences.Editor? = null
    const val KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates"
    fun setProfilePath(context: Context, path: String?) {
        val settings = context
            .getSharedPreferences(
                context.getString(R.string.APP_DATA),
                Context.MODE_PRIVATE
            )
        val e = settings.edit()
        e.putString(context.getString(R.string.profile_path), path)
        e.apply()
    }

    fun getProfilePath(context: Context): String? {
        val settings = context
            .getSharedPreferences(
                context.getString(R.string.APP_DATA),
                Context.MODE_PRIVATE
            )
        return settings.getString(context.getString(R.string.profile_path), null)
    }
}