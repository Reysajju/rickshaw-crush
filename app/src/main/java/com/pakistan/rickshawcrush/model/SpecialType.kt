package com.pakistan.rickshawcrush.model

/**
 * Special vehicle power-ups replacing Candy Crush special candies:
 * - STRIPED_HORIZONTAL / VERTICAL: VIP Siren / Ambulance clear (Clears full row/column)
 * - WRAPPED: Pressure Horn / Gas Cylinder Blast (Explodes 3x3 surrounding tiles)
 * - SIGNAL_BOMB: Traffic Signal / Traffic Warden (Destroys all selected vehicles on board)
 */
enum class SpecialType {
    NONE,
    STRIPED_HORIZONTAL,
    STRIPED_VERTICAL,
    WRAPPED,
    SIGNAL_BOMB
}
