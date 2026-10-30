package com.pakistan.rickshawcrush.ui

import android.graphics.*
import com.pakistan.rickshawcrush.model.SpecialType
import com.pakistan.rickshawcrush.model.Tile
import com.pakistan.rickshawcrush.model.VehicleType
import kotlin.math.cos
import kotlin.math.sin

/**
 * Native Android Canvas Renderer for Pakistani Vehicles and Game Elements
 */
class RickshawRenderer {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    fun drawTile(canvas: Canvas, tile: Tile, tileSize: Float) {
        val cx = tile.x + tileSize / 2f
        val cy = tile.y + tileSize / 2f
        val half = (tileSize * 0.82f) / 2f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(tile.scale, tile.scale)

        if (tile.special == SpecialType.SIGNAL_BOMB) {
            drawTrafficSignal(canvas, half)
        } else {
            when (tile.type) {
                VehicleType.RICKSHAW -> drawRickshaw(canvas, half)
                VehicleType.TRUCK_BUS -> drawTruckBus(canvas, half)
                VehicleType.MEHRAN -> drawMehran(canvas, half)
                VehicleType.MOTORBIKE -> drawBike(canvas, half)
                VehicleType.QINGQI -> drawQingqi(canvas, half)
                VehicleType.YELLOW_TAXI -> drawTaxi(canvas, half)
            }
            drawSpecialOverlays(canvas, tile.special, half)
        }

        canvas.restore()
    }

    private fun drawRickshaw(canvas: Canvas, half: Float) {
        // Green cabin
        paint.color = Color.parseColor("#27AE60")
        canvas.drawRoundRect(RectF(-half * 0.7f, -half * 0.5f, half * 0.7f, half * 0.8f), 8f, 8f, paint)

        // Yellow canopy roof
        paint.color = Color.parseColor("#F1C40F")
        canvas.drawRoundRect(RectF(-half * 0.8f, -half * 0.85f, half * 0.8f, -half * 0.3f), 6f, 6f, paint)

        // Windshield
        paint.color = Color.parseColor("#34495E")
        canvas.drawRect(-half * 0.5f, -half * 0.25f, half * 0.5f, half * 0.15f, paint)

        // Headlight
        paint.color = Color.parseColor("#FFF799")
        canvas.drawCircle(0f, half * 0.55f, half * 0.2f, paint)

        // Tires
        paint.color = Color.parseColor("#111111")
        canvas.drawRect(-half * 0.9f, -half * 0.1f, -half * 0.7f, half * 0.5f, paint)
        canvas.drawRect(half * 0.7f, -half * 0.1f, half * 0.9f, half * 0.5f, paint)
    }

    private fun drawTruckBus(canvas: Canvas, half: Float) {
        // Colorful Truck Art Bedford Bus
        paint.color = Color.parseColor("#E74C3C")
        canvas.drawRoundRect(RectF(-half * 0.85f, -half * 0.75f, half * 0.85f, half * 0.75f), 8f, 8f, paint)

        // Truck art floral crown
        paint.color = Color.parseColor("#F39C12")
        canvas.drawRect(-half * 0.75f, -half * 0.85f, half * 0.75f, -half * 0.65f, paint)

        // Windshields
        paint.color = Color.parseColor("#81ECEC")
        canvas.drawRect(-half * 0.65f, -half * 0.45f, -half * 0.05f, 0f, paint)
        canvas.drawRect(half * 0.05f, -half * 0.45f, half * 0.65f, 0f, paint)

        // Desi yellow bumper
        paint.color = Color.parseColor("#FFD700")
        canvas.drawRect(-half * 0.75f, half * 0.4f, half * 0.75f, half * 0.65f, paint)

        // Dual headlights
        paint.color = Color.WHITE
        canvas.drawCircle(-half * 0.45f, half * 0.5f, 4f, paint)
        canvas.drawCircle(half * 0.45f, half * 0.5f, 4f, paint)
    }

    private fun drawMehran(canvas: Canvas, half: Float) {
        // White Mehran Boss Car
        paint.color = Color.parseColor("#ECF0F1")
        canvas.drawRoundRect(RectF(-half * 0.75f, -half * 0.65f, half * 0.75f, half * 0.7f), 6f, 6f, paint)

        // Windshield
        paint.color = Color.parseColor("#2C3E50")
        canvas.drawRect(-half * 0.55f, -half * 0.35f, half * 0.55f, 0.05f, paint)

        // Front Grille
        paint.color = Color.parseColor("#222222")
        canvas.drawRect(-half * 0.5f, half * 0.35f, half * 0.5f, half * 0.55f, paint)

        // Square Headlights
        paint.color = Color.parseColor("#F39C12")
        canvas.drawRect(-half * 0.7f, half * 0.35f, -half * 0.5f, half * 0.55f, paint)
        canvas.drawRect(half * 0.5f, half * 0.35f, half * 0.7f, half * 0.55f, paint)
    }

    private fun drawBike(canvas: Canvas, half: Float) {
        // Red CD-70 Motorbike
        paint.color = Color.parseColor("#D35400")
        canvas.drawOval(RectF(-half * 0.35f, -half * 0.45f, half * 0.35f, half * 0.45f), paint)

        // Black Seat
        paint.color = Color.parseColor("#222222")
        canvas.drawRoundRect(RectF(-half * 0.25f, -half * 0.75f, half * 0.25f, -half * 0.3f), 4f, 4f, paint)

        // Handlebars
        strokePaint.color = Color.parseColor("#BDC3C7")
        strokePaint.strokeWidth = 4f
        canvas.drawLine(-half * 0.65f, half * 0.35f, half * 0.65f, half * 0.35f, strokePaint)

        // Front Round Headlight
        paint.color = Color.parseColor("#FFF799")
        canvas.drawCircle(0f, half * 0.55f, 6f, paint)
    }

    private fun drawQingqi(canvas: Canvas, half: Float) {
        // Blue Chingchi Rickshaw
        paint.color = Color.parseColor("#2980B9")
        canvas.drawRoundRect(RectF(-half * 0.8f, -half * 0.7f, half * 0.8f, half * 0.3f), 6f, 6f, paint)

        // Front Bike Driver Seat
        paint.color = Color.parseColor("#E74C3C")
        canvas.drawRect(-half * 0.25f, half * 0.3f, half * 0.25f, half * 0.7f, paint)

        // Canopy frame
        strokePaint.color = Color.parseColor("#00CEC9")
        strokePaint.strokeWidth = 3f
        canvas.drawRect(-half * 0.75f, -half * 0.65f, half * 0.75f, half * 0.25f, strokePaint)
    }

    private fun drawTaxi(canvas: Canvas, half: Float) {
        // Yellow Taxi
        paint.color = Color.parseColor("#F1C40F")
        canvas.drawRoundRect(RectF(-half * 0.75f, -half * 0.65f, half * 0.75f, half * 0.7f), 6f, 6f, paint)

        // Black checkered band
        paint.color = Color.parseColor("#222222")
        canvas.drawRect(-half * 0.75f, 0f, half * 0.75f, half * 0.15f, paint)

        // Windshield
        paint.color = Color.parseColor("#2C3E50")
        canvas.drawRect(-half * 0.55f, -half * 0.4f, half * 0.55f, -0.05f, paint)

        // TAXI roof sign
        paint.color = Color.parseColor("#E74C3C")
        canvas.drawRect(-half * 0.35f, -half * 0.8f, half * 0.35f, -half * 0.55f, paint)
    }

    private fun drawTrafficSignal(canvas: Canvas, half: Float) {
        // Traffic Signal (Color Bomb)
        val r = half * 0.75f
        paint.color = Color.parseColor("#2C3E50")
        canvas.drawCircle(0f, 0f, r, paint)

        val colors = intArrayOf(
            Color.parseColor("#2ECC71"),
            Color.parseColor("#E74C3C"),
            Color.parseColor("#F1C40F"),
            Color.parseColor("#3498DB")
        )

        val time = System.currentTimeMillis() * 0.004
        for (i in colors.indices) {
            val angle = time + (i * Math.PI * 2 / colors.size)
            val dotX = (cos(angle) * (r * 0.55f)).toFloat()
            val dotY = (sin(angle) * (r * 0.55f)).toFloat()
            paint.color = colors[i]
            canvas.drawCircle(dotX, dotY, 6f, paint)
        }
    }

    private fun drawSpecialOverlays(canvas: Canvas, special: SpecialType, half: Float) {
        when (special) {
            SpecialType.STRIPED_HORIZONTAL -> {
                strokePaint.color = Color.WHITE
                strokePaint.strokeWidth = 5f
                canvas.drawLine(-half + 4f, 0f, half - 4f, 0f, strokePaint)
                paint.color = Color.parseColor("#FF0055")
                canvas.drawCircle(0f, -half * 0.6f, 6f, paint)
            }
            SpecialType.STRIPED_VERTICAL -> {
                strokePaint.color = Color.WHITE
                strokePaint.strokeWidth = 5f
                canvas.drawLine(0f, -half + 4f, 0f, half - 4f, strokePaint)
                paint.color = Color.parseColor("#00E1FF")
                canvas.drawCircle(0f, -half * 0.6f, 6f, paint)
            }
            SpecialType.WRAPPED -> {
                strokePaint.color = Color.parseColor("#FFD700")
                strokePaint.strokeWidth = 4f
                strokePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
                canvas.drawRect(-half + 4f, -half + 4f, half - 4f, half - 4f, strokePaint)
                strokePaint.pathEffect = null
            }
            else -> {}
        }
    }
}
