package com.subbhu.wallpapers

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private val bg = Color.rgb(12, 14, 25)
    private val accent = Color.rgb(65, 220, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
            isFillViewport = true
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 36, 22, 28)
        }

        root.addView(text(
            "SUBBHU",
            16f,
            accent,
            true
        ))

        root.addView(text(
            "WALLPAPERS & RINGTONES",
            26f,
            Color.WHITE,
            true
        ))

        root.addView(text(
            "Your personal customization hub",
            15f,
            Color.LTGRAY
        ))

        root.addView(space(24))

        root.addView(text(
            "Explore your style",
            21f,
            Color.WHITE,
            true
        ))

        val features = listOf(
            "Wallpapers" to "Browse and download high-resolution images",
            "Live Wallpapers" to "Animated backgrounds powered by Android",
            "Ringtones" to "Preview and download audio",
            "Themes" to "Explore matching customization collections",
            "Favorites" to "Your saved content"
        )

        features.forEach { (title, description) ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(18, 18, 18, 18)
                setBackgroundColor(Color.rgb(28, 32, 49))
            }

            card.addView(text(title, 19f, accent, true))
            card.addView(space(6))
            card.addView(text(description, 14f, Color.LTGRAY))

            val button = Button(this).apply {
                text = "Explore"
                setOnClickListener {
                    Toast.makeText(
                        this@MainActivity,
                        "$title module is planned for the next development phase.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            card.addView(button)

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }

            root.addView(card, params)
        }

        root.addView(space(12))
        root.addView(text(
            "Foundation build — online services and native customization features are not connected yet.",
            13f,
            Color.LTGRAY
        ))

        scroll.addView(root)
        setContentView(scroll)
    }

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        gravity = Gravity.START

        if (bold) {
            setTypeface(null, Typeface.BOLD)
        }
    }

    private fun space(height: Int): TextView =
        TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                1,
                height
            )
        }
}
