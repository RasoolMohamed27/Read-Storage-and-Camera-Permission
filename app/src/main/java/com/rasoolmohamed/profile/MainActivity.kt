package com.rasoolmohamed.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rasoolmohamed.profile.ui.main.ProfileFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ProfileFragment.newInstance())
                .commitNow()
        }

        setStatusBarColor()
    }

    private fun setStatusBarColor() {
        val window = this.window
        window.statusBarColor = this.resources.getColor(R.color.statusBarColor, null)
    }
}