package com.pakistan.rickshawcrush.engine

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * High-performance 2D Particle Engine for Android Canvas
 */
class ParticleEngine {

    private val particles = mutableListOf<Particle>()
    private val floatingTexts = mutableListOf<FloatingText>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    data class Particle(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var color: Int,
        var radius: Float,
        var alpha: Float = 1.0f,
        var life: Float = 0.6f
    )

    data class FloatingText(
        val text: String,
        var x: Float,
        var y: Float,
        val color: Int,
        var alpha: Float = 1.0f,
        var scale: Float = 1.2f
    )

    fun addExplosion(x: Float, y: Float, color: Int, count: Int = 16) {
        for (i in 0 until count) {
            val angle = Random.nextFloat() * Math.PI.toFloat() * 2f
            val speed = 3f + Random.nextFloat() * 6f
            particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = color,
                    radius = 4f + Random.nextFloat() * 6f
                )
            )
        }
    }

    fun addFloatingText(text: String, x: Float, y: Float, color: Int = Color.parseColor("#FFD700")) {
        floatingTexts.add(FloatingText(text, x, y, color))
    }

    fun update(dt: Float) {
        val pIter = particles.iterator()
        while (pIter.hasNext()) {
            val p = pIter.next()
            p.x += p.vx
            p.y += p.vy
            p.alpha -= dt / p.life
            if (p.alpha <= 0) pIter.remove()
        }

        val tIter = floatingTexts.iterator()
        while (tIter.hasNext()) {
            val ft = tIter.next()
            ft.y -= 45f * dt
            ft.alpha -= dt * 1.5f
            if (ft.alpha <= 0) tIter.remove()
        }
    }

    fun draw(canvas: Canvas) {
        for (p in particles) {
            paint.color = p.color
            paint.alpha = (p.alpha.coerceIn(0f, 1f) * 255).toInt()
            canvas.drawCircle(p.x, p.y, p.radius, paint)
        }

        for (ft in floatingTexts) {
            paint.color = ft.color
            paint.alpha = (ft.alpha.coerceIn(0f, 1f) * 255).toInt()
            paint.textSize = 34f * ft.scale
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(ft.text, ft.x, ft.y, paint)
        }
    }
}
