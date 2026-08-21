package com.musicgb.player.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class VisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var fftData = ByteArray(0)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1DB954")
        style = Paint.Style.FILL
    }
    private val barCount = 32
    private val rect = RectF()

    fun updateFft(data: ByteArray) {
        if (data.size < barCount) return
        fftData = data.copyOf(barCount)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (fftData.isEmpty()) return

        val width = width.toFloat()
        val height = height.toFloat()
        val barWidth = width / barCount
        val gap = 4f

        for (i in 0 until barCount) {
            val raw = fftData[i].toInt()
            val magnitude = kotlin.math.abs(raw)
            val barHeight = (magnitude / 128f) * height
            val left = i * barWidth + gap / 2
            val top = height - barHeight
            val right = left + barWidth - gap
            val bottom = height

            rect.set(left, top.coerceIn(0f, height), right, bottom)
            canvas.drawRoundRect(rect, 6f, 6f, paint)
        }
    }
}
