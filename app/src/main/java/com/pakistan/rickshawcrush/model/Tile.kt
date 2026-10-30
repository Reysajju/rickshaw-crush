package com.pakistan.rickshawcrush.model

/**
 * Represents a single vehicle tile on the game grid
 */
data class Tile(
    var type: VehicleType,
    var special: SpecialType = SpecialType.NONE,
    var row: Int,
    var col: Int,
    var x: Float = 0f,
    var y: Float = 0f,
    var targetX: Float = 0f,
    var targetY: Float = 0f,
    var scale: Float = 1.0f,
    var alpha: Float = 1.0f,
    var isRemoving: Boolean = false
) {
    fun isSpecial(): Boolean = special != SpecialType.NONE

    fun matchesWith(other: Tile?): Boolean {
        if (other == null) return false
        // Traffic signal bombs don't match normally; they require direct swap
        if (this.special == SpecialType.SIGNAL_BOMB || other.special == SpecialType.SIGNAL_BOMB) {
            return false
        }
        return this.type == other.type
    }
}
