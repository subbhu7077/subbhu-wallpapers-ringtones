package com.subbhu.wallpapers

import android.app.Activity
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.*
import org.json.JSONArray
import java.net.URL
import kotlin.concurrent.thread

private data class Wall(val title: String, val category: String, val url: String)

class MainActivity : Activity() {
    private val bg = Color.rgb(10, 12, 24)
    private val panel = Color.rgb(27, 31, 49)
    private val cyan = Color.rgb(65, 220, 255)
    private val purple = Color.rgb(164, 105, 255)
    private val prefs by lazy { getSharedPreferences("subbhu_prefs", MODE_PRIVATE) }
    private lateinit var content: LinearLayout
    private var page = "Home"
    private var player: MediaPlayer? = null

    private val wallpapers = listOf(
        Wall("Ocean Dream", "Nature", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=900"),
        Wall("Mountain Glow", "Nature", "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=900"),
        Wall("Neon City", "Abstract", "https://images.unsplash.com/photo-1519608487953-e999c86e7455?w=900"),
        Wall("Pink Sky", "Aesthetic", "https://images.unsplash.com/photo-1490730141103-6cac27aaab94?w=900"),
        Wall("Forest Calm", "Nature", "https://images.unsplash.com/photo-1448375240586-882707db888b?w=900"),
        Wall("Purple Night", "Abstract", "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=900"),
        Wall("Minimal Sand", "Minimal", "https://images.unsplash.com/photo-1470252649378-9c29740c9fa8?w=900"),
        Wall("Blue Horizon", "Aesthetic", "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=900")
    )
    private val ringtones = listOf(
        "Sample Melody 1" to "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Sample Melody 2" to "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Sample Melody 3" to "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        showPage("Home")
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }

    private fun showPage(target: String) {
        page = target
        val outer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }
        val scroll = ScrollView(this).apply { isFillViewport = true }
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(20), dp(18), dp(24))
        }
        scroll.addView(content)
        outer.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        outer.addView(bottomNav())
        setContentView(outer)

        when (target) {
            "Home" -> home()
            "Wallpapers" -> gallery()
            "Live Wallpapers" -> liveWallpapers()
            "Ringtones" -> ringtonePage()
            "Themes" -> themes()
            "Favorites" -> favorites()
        }
        content.alpha = 0f
        content.translationY = dp(12).toFloat()
        content.animate().alpha(1f).translationY(0f).setDuration(350)
            .setInterpolator(DecelerateInterpolator()).start()
    }

    private fun home() {
        content.addView(label("SUBBHU ✦", 16f, cyan, true))
        content.addView(gap(8))
        content.addView(label("WALLPAPERS\\n& RINGTONES", 30f, Color.WHITE, true))
        content.addView(gap(6))
        content.addView(label("Your phone. Your vibe.", 15f, Color.LTGRAY))
        content.addView(gap(20))
        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(22), dp(20), dp(22))
            background = gradient(intArrayOf(Color.rgb(34, 71, 104), Color.rgb(74, 45, 120)), 24)
        }
        hero.addView(label("YOUR SCREEN, REIMAGINED", 12f, Color.WHITE, true))
        hero.addView(gap(10))
        hero.addView(label("Fresh looks,\\none tap away.", 27f, Color.WHITE, true))
        hero.addView(gap(14))
        hero.addView(button("Explore wallpapers  →") { showPage("Wallpapers") })
        content.addView(hero, fullWidth())
        content.addView(gap(20))
        content.addView(label("EXPLORE YOUR STYLE", 19f, Color.WHITE, true))
        listOf(
            "Wallpapers" to "High-resolution backgrounds",
            "Live Wallpapers" to "Animated Android background",
            "Ringtones" to "Preview and download audio",
            "Themes" to "Choose your colour mood",
            "Favorites" to "Your saved collection"
        ).forEach { (title, description) ->
            val card = card()
            card.addView(label(title, 19f, cyan, true))
            card.addView(gap(5))
            card.addView(label(description, 14f, Color.LTGRAY))
            card.addView(gap(10))
            card.addView(button("EXPLORE  →") { showPage(title) })
            content.addView(card, fullWidth())
        }
        content.addView(label("Made for your everyday vibe • SUBBHU", 12f, Color.GRAY))
    }

    private fun header(title: String, subtitle: String) {
        content.addView(label("SUBBHU ✦", 13f, cyan, true))
        content.addView(gap(8))
        content.addView(label(title, 27f, Color.WHITE, true))
        content.addView(gap(5))
        content.addView(label(subtitle, 14f, Color.LTGRAY))
        content.addView(gap(18))
    }

    private fun gallery() {
        header("Wallpaper Gallery", "Search, preview, save and download.")
        val search = EditText(this).apply {
            hint = "Search wallpapers…"
            setHintTextColor(Color.GRAY)
            setTextColor(Color.WHITE)
            textSize = 15f
            setSingleLine()
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = rounded(panel, 14)
        }
        content.addView(search, fullWidth())
        content.addView(gap(10))
        val categoryRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        content.addView(categoryRow)
        content.addView(gap(12))
        content.addView(list)

        var selectedCategory = "All"
        fun render(query: String) {
            list.removeAllViews()
            wallpapers.filter {
                (selectedCategory == "All" || it.category == selectedCategory) &&
                    (it.title.contains(query, true) || it.category.contains(query, true))
            }.forEach { wall ->
                val card = card()
                val image = ImageView(this).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    background = rounded(panel, 16)
                }
                card.addView(image, LinearLayout.LayoutParams(-1, dp(185)))
                loadImage(wall.url, image)
                card.addView(gap(8))
                card.addView(label(wall.title, 17f, Color.WHITE, true))
                card.addView(label(wall.category, 12f, cyan))
                card.addView(gap(8))
                val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
                actions.addView(button("♡ Save") {
                    toggleFavorite(wall)
                    Toast.makeText(this@MainActivity, "Favorites updated", Toast.LENGTH_SHORT).show()
                }, LinearLayout.LayoutParams(0, dp(44), 1f))
                actions.addView(View(this), LinearLayout.LayoutParams(dp(8), 1))
                actions.addView(button("↓ Download") {
                    download(wall.url, wall.title.replace(" ", "_") + ".jpg")
                }, LinearLayout.LayoutParams(0, dp(44), 1f))
                card.addView(actions)
                image.setOnClickListener { preview(wall) }
                card.setOnClickListener { preview(wall) }
                list.addView(card, fullWidth())
            }
            if (list.childCount == 0) list.addView(label("No wallpapers found.", 14f, Color.LTGRAY))
        }

        listOf("All", "Nature", "Abstract", "Aesthetic", "Minimal").forEach { category ->
            val chip = label(category, 12f, if (category == "All") bg else Color.WHITE, true).apply {
                setPadding(dp(9), dp(9), dp(9), dp(9))
                background = rounded(if (category == "All") cyan else panel, 22)
                setOnClickListener {
                    selectedCategory = category
                    render(search.text.toString())
                }
            }
            categoryRow.addView(chip, LinearLayout.LayoutParams(-2, -2).apply {
                marginEnd = dp(5)
            })
        }
        search.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                render(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        render("")
    }

    private fun preview(wall: Wall) {
        val dialog = android.app.Dialog(this)
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = rounded(panel, 22)
        }
        val image = ImageView(this).apply { scaleType = ImageView.ScaleType.CENTER_CROP }
        box.addView(image, LinearLayout.LayoutParams(-1, dp(320)))
        loadImage(wall.url, image)
        box.addView(gap(10))
        box.addView(label(wall.title, 20f, Color.WHITE, true))
        box.addView(gap(10))
        box.addView(button("Download wallpaper") {
            download(wall.url, wall.title.replace(" ", "_") + ".jpg")
            dialog.dismiss()
        })
        box.addView(gap(8))
        box.addView(button("Save to favorites") {
            toggleFavorite(wall)
            dialog.dismiss()
        })
        dialog.setContentView(box)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        dialog.window?.setLayout((resources.displayMetrics.widthPixels * 0.9).toInt(), -2)
    }

    private fun liveWallpapers() {
        header("Live Wallpapers", "Apply an animated gradient using Android's wallpaper preview.")
        val preview = View(this).apply {
            background = gradient(intArrayOf(cyan, purple, bg), 26)
        }
        content.addView(preview, LinearLayout.LayoutParams(-1, dp(280)))
        content.addView(gap(16))
        content.addView(button("Set live wallpaper  →") {
            try {
                val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                    putExtra(
                        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        ComponentName(this@MainActivity, SubbhuLiveWallpaperService::class.java)
                    )
                }
                startActivity(intent)
            } catch (_: Exception) {
                startActivity(Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER))
            }
        })
        content.addView(gap(10))
        content.addView(label("Android shows a system preview before applying.", 13f, Color.LTGRAY))
    }

    private fun ringtonePage() {
        header("Ringtones & Sounds", "Preview sample audio or download it.")
        ringtones.forEach { (name, url) ->
            val card = card()
            card.addView(label("♫  $name", 18f, Color.WHITE, true))
            card.addView(gap(10))
            card.addView(button("▶ Preview / Stop") { playAudio(url) })
            card.addView(gap(7))
            card.addView(button("↓ Download audio") {
                download(url, name.replace(" ", "_") + ".mp3")
            })
            content.addView(card, fullWidth())
        }
        content.addView(label("These are sample tracks for testing. Use audio you have rights to use.", 12f, Color.GRAY))
    }

    private fun themes() {
        header("Choose your theme", "Save a colour mood for this device.")
        val palettes = listOf(
            "Neon Ocean" to intArrayOf(cyan, Color.rgb(10, 20, 38)),
            "Purple Haze" to intArrayOf(purple, Color.rgb(25, 13, 38)),
            "Rose Glow" to intArrayOf(Color.rgb(255, 120, 180), Color.rgb(38, 14, 29)),
            "Golden Hour" to intArrayOf(Color.rgb(255, 199, 85), Color.rgb(34, 25, 14))
        )
        palettes.forEach { (name, colors) ->
            val card = card()
            card.addView(View(this).apply { background = gradient(colors, 16) },
                LinearLayout.LayoutParams(-1, dp(65)))
            card.addView(gap(8))
            card.addView(label(name, 18f, Color.WHITE, true))
            card.addView(gap(8))
            card.addView(button("Apply $name") {
                prefs.edit().putString("theme", name).apply()
                Toast.makeText(this, "$name saved. Reopen the app to refresh.", Toast.LENGTH_SHORT).show()
            })
            content.addView(card, fullWidth())
        }
    }

    private fun favorites() {
        header("Your Favorites", "Saved wallpapers are stored on this device.")
        val array = try { JSONArray(prefs.getString("favorites", "[]")) } catch (_: Exception) { JSONArray() }
        val saved = (0 until array.length()).map { array.optString(it) }.toSet()
        val items = wallpapers.filter { it.url in saved }
        if (items.isEmpty()) {
            content.addView(label("♡", 54f, cyan, true))
            content.addView(label("Nothing saved yet", 21f, Color.WHITE, true))
            content.addView(label("Open Wallpapers and tap Save to keep favourites here.", 14f, Color.LTGRAY))
            content.addView(gap(14))
            content.addView(button("Explore wallpapers") { showPage("Wallpapers") })
        } else {
            items.forEach { wall ->
                val card = card()
                val image = ImageView(this).apply { scaleType = ImageView.ScaleType.CENTER_CROP }
                card.addView(image, LinearLayout.LayoutParams(-1, dp(170)))
                loadImage(wall.url, image)
                card.addView(label(wall.title, 17f, Color.WHITE, true))
                card.addView(button("Remove from favorites") {
                    toggleFavorite(wall)
                    showPage("Favorites")
                })
                content.addView(card, fullWidth())
            }
        }
    }

    private fun toggleFavorite(wall: Wall) {
        val array = try { JSONArray(prefs.getString("favorites", "[]")) } catch (_: Exception) { JSONArray() }
        val values = (0 until array.length()).map { array.optString(it) }.toMutableSet()
        if (!values.add(wall.url)) values.remove(wall.url)
        prefs.edit().putString("favorites", JSONArray(values.toList()).toString()).apply()
    }

    private fun playAudio(url: String) {
        try {
            player?.release()
            player = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener {
                    it.start()
                    Toast.makeText(this@MainActivity, "Playing preview", Toast.LENGTH_SHORT).show()
                }
                setOnCompletionListener { it.release(); player = null }
                setOnErrorListener { mp, _, _ ->
                    mp.release()
                    player = null
                    Toast.makeText(this@MainActivity, "Audio unavailable. Check internet.", Toast.LENGTH_LONG).show()
                    true
                }
                prepareAsync()
            }
        } catch (_: Exception) {
            Toast.makeText(this, "Could not play audio. Check internet.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun download(url: String, filename: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(filename)
                .setDescription("Downloading from SUBBHU Wallpapers")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(false)
            (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
            Toast.makeText(this, "Download started — check notifications", Toast.LENGTH_LONG).show()
        } catch (_: Exception) {
            Toast.makeText(this, "Download failed. Check internet.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadImage(url: String, target: ImageView) {
        thread {
            try {
                val connection = URL(url).openConnection()
                connection.connectTimeout = 10000
                connection.readTimeout = 15000
                val bitmap = connection.getInputStream().use { BitmapFactory.decodeStream(it) }
                runOnUiThread {
                    if (!isFinishing && bitmap != null) target.setImageBitmap(bitmap)
                }
            } catch (_: Exception) {
                runOnUiThread {
                    target.background = gradient(intArrayOf(Color.rgb(35, 70, 100), Color.rgb(85, 40, 120)), 16)
                }
            }
        }
    }

    private fun bottomNav(): View {
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(4), dp(8), dp(4), dp(8))
            setBackgroundColor(Color.rgb(18, 21, 36))
        }
        listOf("Home" to "⌂", "Wallpapers" to "✦", "Ringtones" to "♫", "Favorites" to "♡").forEach { (target, icon) ->
            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setOnClickListener { showPage(target) }
            }
            item.addView(label(icon, 20f, if (page == target) cyan else Color.LTGRAY, true))
            item.addView(label(target, 10f, if (page == target) cyan else Color.LTGRAY))
            bar.addView(item, LinearLayout.LayoutParams(0, -2, 1f))
        }
        return bar
    }

    private fun card() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(14), dp(14), dp(14), dp(14))
        background = rounded(panel, 20)
        elevation = dp(2).toFloat()
    }

    private fun button(title: String, action: () -> Unit) = TextView(this).apply {
        text = title
        textSize = 14f
        typeface = Typeface.DEFAULT_BOLD
        gravity = Gravity.CENTER
        setTextColor(bg)
        setPadding(dp(12), dp(12), dp(12), dp(12))
        background = gradient(intArrayOf(cyan, purple), 13)
        isClickable = true
        isFocusable = true
        setOnClickListener {
            animate().scaleX(0.97f).scaleY(0.97f).setDuration(70).withEndAction {
                animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                action()
            }.start()
        }
    }

    private fun label(value: String, size: Float, color: Int, bold: Boolean = false) =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) typeface = Typeface.create("sans-serif", Typeface.BOLD)
        }

    private fun gap(height: Int) = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(1, dp(height))
    }

    private fun fullWidth() = LinearLayout.LayoutParams(-1, -2).apply {
        bottomMargin = dp(12)
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }

    private fun gradient(colors: IntArray, radius: Int) =
        GradientDrawable(GradientDrawable.Orientation.TL_BR, colors).apply {
            cornerRadius = dp(radius).toFloat()
        }
}
