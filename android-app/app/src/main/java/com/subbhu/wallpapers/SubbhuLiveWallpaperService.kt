package com.subbhu.wallpapers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import kotlin.concurrent.thread

class SubbhuLiveWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = AnimatedEngine()

    private inner class AnimatedEngine : Engine() {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        @Volatile private var visible = false
        @Volatile private var running = false
        private var phase = 0f

        override fun onVisibilityChanged(isVisible: Boolean) {
            visible = isVisible
            if (isVisible) drawFrames()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            if (visible) drawFrames()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            visible = false
            super.onSurfaceDestroyed(holder)
        }

        override fun onDestroy() {
            visible = false
            super.onDestroy()
        }

        private fun drawFrames() {
            if (running) return
            running = true
            thread {
                while (visible) {
                    var canvas: Canvas? = null
                    try {
                        canvas = surfaceHolder.lockCanvas()
                        if (canvas != null) {
                            val w = canvas.width.toFloat()
                            val h = canvas.height.toFloat()
                            canvas.drawColor(Color.rgb(8, 10, 23))
                            val x = w * (0.35f + 0.18f * kotlin.math.sin(phase.toDouble()).toFloat())
                            val y = h * (0.36f + 0.12f * kotlin.math.cos(phase.toDouble()).toFloat())
                            paint.shader = RadialGradient(
                                x, y, w * 0.75f,
                                intArrayOf(Color.rgb(42, 205, 255), Color.rgb(105, 55, 190), Color.TRANSPARENT),
                                floatArrayOf(0f, 0.48f, 1f), Shader.TileMode.CLAMP
                            )
                            canvas.drawRect(0f, 0f, w, h, paint)
                            paint.shader = null
                            phase += 0.035f
                        }
                    } catch (_: Exception) {
                    } finally {
                        if (canvas != null) try { surfaceHolder.unlockCanvasAndPost(canvas) } catch (_: Exception) {}
                    }
                    try { Thread.sleep(33) } catch (_: InterruptedException) {}
                }
                running = false
            }
        }
    }
}
