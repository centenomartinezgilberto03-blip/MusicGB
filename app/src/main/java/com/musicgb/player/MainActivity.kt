package com.musicgb.player

import android.os.Bundle
import android.app.Activity
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#121212"))
            setPadding(32, 64, 32, 32)
        }
        val titleText = TextView(this).apply {
            text = "MusicGB"
            textSize = 32f
            setTextColor(Color.parseColor("#1DB954"))
            gravity = Gravity.CENTER
        }
        rootLayout.addView(titleText)
        setContentView(rootLayout)
    }
}
