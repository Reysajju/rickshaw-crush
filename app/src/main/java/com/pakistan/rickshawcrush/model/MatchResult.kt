package com.pakistan.rickshawcrush.model

/**
 * Encapsulates match detection results and groups
 */
data class MatchGroup(
    val tiles: List<Tile>,
    val isHorizontal: Boolean,
    val count: Int
)

data class CascadeStep(
    val removedTiles: Set<Tile>,
    val createdSpecials: List<Tile>,
    val pointsEarned: Int,
    val comboMultiplier: Int,
    val urduCheer: String? = null
)
