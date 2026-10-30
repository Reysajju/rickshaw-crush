package com.pakistan.rickshawcrush.model

import android.graphics.Color

/**
 * Pakistani vehicles replacing Candy Crush candies
 */
enum class VehicleType(
    val id: Int,
    val urduName: String,
    val primaryColor: Int,
    val accentColor: Int
) {
    RICKSHAW(0, "رکشہ", Color.parseColor("#27AE60"), Color.parseColor("#F1C40F")),
    TRUCK_BUS(1, "سجی ہوئی بس", Color.parseColor("#E74C3C"), Color.parseColor("#F39C12")),
    MEHRAN(2, "مہران باس", Color.parseColor("#ECF0F1"), Color.parseColor("#7F8C8D")),
    MOTORBIKE(3, "موٹر سائیکل 70", Color.parseColor("#D35400"), Color.parseColor("#E67E22")),
    QINGQI(4, "چنگ چی", Color.parseColor("#2980B9"), Color.parseColor("#00CEC9")),
    YELLOW_TAXI(5, "پیلے رنگ کی ٹیکسی", Color.parseColor("#F1C40F"), Color.parseColor("#2C3E50"));

    companion object {
        fun fromId(id: Int): VehicleType? = values().firstOrNull { it.id == id }
        fun random(): VehicleType = values().random()
    }
}
