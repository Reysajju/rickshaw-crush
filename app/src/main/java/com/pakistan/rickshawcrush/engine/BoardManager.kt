package com.pakistan.rickshawcrush.engine

import com.pakistan.rickshawcrush.model.*
import kotlin.math.abs

/**
 * Core Match-3 Game Logic Engine for Rickshaw Crush.
 * Handles board state, swaps, matching algorithms, cascades, and gravity.
 */
class BoardManager(val rows: Int = 8, val cols: Int = 8) {

    val grid: Array<Array<Tile?>> = Array(rows) { arrayOfNulls(cols) }

    init {
        generateInitialBoard()
    }

    /**
     * Initializes the grid with random vehicles, ensuring no matches exist initially.
     */
    fun generateInitialBoard() {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                var type: VehicleType
                do {
                    type = VehicleType.random()
                } while (
                    (r >= 2 && grid[r - 1][c]?.type == type && grid[r - 2][c]?.type == type) ||
                    (c >= 2 && grid[r][c - 1]?.type == type && grid[r][c - 2]?.type == type)
                )

                grid[r][c] = Tile(
                    type = type,
                    special = SpecialType.NONE,
                    row = r,
                    col = c
                )
            }
        }
    }

    /**
     * Finds all horizontal and vertical match groups of 3 or more.
     */
    fun findMatches(): List<MatchGroup> {
        val groups = mutableListOf<MatchGroup>()

        // Horizontal matches
        for (r in 0 until rows) {
            var count = 1
            for (c in 0 until cols) {
                val current = grid[r][c]
                val next = if (c < cols - 1) grid[r][c + 1] else null

                if (next != null && current != null && current.matchesWith(next)) {
                    count++
                } else {
                    if (count >= 3) {
                        val tiles = mutableListOf<Tile>()
                        for (k in (c - count + 1)..c) {
                            grid[r][k]?.let { tiles.add(it) }
                        }
                        groups.add(MatchGroup(tiles, isHorizontal = true, count = count))
                    }
                    count = 1
                }
            }
        }

        // Vertical matches
        for (c in 0 until cols) {
            var count = 1
            for (r in 0 until rows) {
                val current = grid[r][c]
                val next = if (r < rows - 1) grid[r + 1][c] else null

                if (next != null && current != null && current.matchesWith(next)) {
                    count++
                } else {
                    if (count >= 3) {
                        val tiles = mutableListOf<Tile>()
                        for (k in (r - count + 1)..r) {
                            grid[k][c]?.let { tiles.add(it) }
                        }
                        groups.add(MatchGroup(tiles, isHorizontal = false, count = count))
                    }
                    count = 1
                }
            }
        }

        return groups
    }

    /**
     * Checks if a swap between (r1, c1) and (r2, c2) is valid.
     */
    fun canSwap(r1: Int, c1: Int, r2: Int, c2: Int): Boolean {
        if (abs(r1 - r2) + abs(c1 - c2) != 1) return false
        val t1 = grid[r1][c1] ?: return false
        val t2 = grid[r2][c2] ?: return false

        // Special bomb combos are always valid
        if (t1.special == SpecialType.SIGNAL_BOMB || t2.special == SpecialType.SIGNAL_BOMB) return true
        if (t1.isSpecial() && t2.isSpecial()) return true

        // Tentatively swap
        grid[r1][c1] = t2
        grid[r2][c2] = t1

        val hasMatch = findMatches().isNotEmpty()

        // Revert swap
        grid[r1][c1] = t1
        grid[r2][c2] = t2

        return hasMatch
    }

    /**
     * Executes special combo interactions (e.g. Signal + Signal, Signal + Vehicle, Striped + Wrapped)
     */
    fun processSpecialCombo(t1: Tile, t2: Tile): CascadeStep? {
        // 1. Two Traffic Signals swapped = Entire board wipe!
        if (t1.special == SpecialType.SIGNAL_BOMB && t2.special == SpecialType.SIGNAL_BOMB) {
            val allTiles = mutableSetOf<Tile>()
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    grid[r][c]?.let { allTiles.add(it) }
                }
            }
            return CascadeStep(
                removedTiles = allTiles,
                createdSpecials = emptyList(),
                pointsEarned = 5000,
                comboMultiplier = 1,
                urduCheer = "میلہ لوٹ لیا! شاہراہ کلیئر!"
            )
        }

        // 2. Traffic Signal + Any Vehicle = Clears all vehicles of that type
        if (t1.special == SpecialType.SIGNAL_BOMB || t2.special == SpecialType.SIGNAL_BOMB) {
            val targetColor = if (t1.special == SpecialType.SIGNAL_BOMB) t2.type else t1.type
            val targetTiles = mutableSetOf<Tile>()
            targetTiles.add(t1)
            targetTiles.add(t2)

            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val tile = grid[r][c]
                    if (tile != null && tile.type == targetColor) {
                        targetTiles.add(tile)
                    }
                }
            }
            return CascadeStep(
                removedTiles = targetTiles,
                createdSpecials = emptyList(),
                pointsEarned = targetTiles.size * 120,
                comboMultiplier = 1,
                urduCheer = "ٹریفک وارڈن الرٹ!"
            )
        }

        // 3. Striped + Wrapped = Giant 3x3 mega cross beam
        val isStripedAndWrapped = (t1.special == SpecialType.STRIPED_HORIZONTAL || t1.special == SpecialType.STRIPED_VERTICAL) &&
                t2.special == SpecialType.WRAPPED ||
                (t2.special == SpecialType.STRIPED_HORIZONTAL || t2.special == SpecialType.STRIPED_VERTICAL) &&
                t1.special == SpecialType.WRAPPED

        if (isStripedAndWrapped) {
            val centerR = t2.row
            val centerC = t2.col
            val clearedTiles = mutableSetOf<Tile>()

            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if (abs(r - centerR) <= 1 || abs(c - centerC) <= 1) {
                        grid[r][c]?.let { clearedTiles.add(it) }
                    }
                }
            }
            return CascadeStep(
                removedTiles = clearedTiles,
                createdSpecials = emptyList(),
                pointsEarned = clearedTiles.size * 100,
                comboMultiplier = 1,
                urduCheer = "سپر ایمبولینس پروٹوکول!"
            )
        }

        return null
    }

    /**
     * Resolves match groups, triggers specials, calculates points and Urdu cheers.
     */
    fun resolveMatchStep(groups: List<MatchGroup>, comboChain: Int, focalTile: Tile? = null): CascadeStep {
        val removed = mutableSetOf<Tile>()
        val createdSpecials = mutableListOf<Tile>()

        // Check for special item creations
        for (g in groups) {
            val count = g.count
            val targetTile = focalTile ?: g.tiles[g.tiles.size / 2]

            if (count >= 5) {
                // 5 in a straight line -> Traffic Signal Color Bomb!
                val bomb = Tile(
                    type = VehicleType.RICKSHAW, // placeholder
                    special = SpecialType.SIGNAL_BOMB,
                    row = targetTile.row,
                    col = targetTile.col
                )
                createdSpecials.add(bomb)
            } else if (count == 4) {
                // 4 in a line -> VIP Siren Striped
                val striped = Tile(
                    type = targetTile.type,
                    special = if (g.isHorizontal) SpecialType.STRIPED_VERTICAL else SpecialType.STRIPED_HORIZONTAL,
                    row = targetTile.row,
                    col = targetTile.col
                )
                createdSpecials.add(striped)
            }
            removed.addAll(g.tiles)
        }

        // Expand removals based on triggered specials
        val toExpand = removed.toList()
        for (t in toExpand) {
            when (t.special) {
                SpecialType.STRIPED_HORIZONTAL -> {
                    for (c in 0 until cols) grid[t.row][c]?.let { removed.add(it) }
                }
                SpecialType.STRIPED_VERTICAL -> {
                    for (r in 0 until rows) grid[r][t.col]?.let { removed.add(it) }
                }
                SpecialType.WRAPPED -> {
                    for (dr in -1..1) {
                        for (dc in -1..1) {
                            val nr = t.row + dr
                            val nc = t.col + dc
                            if (nr in 0 until rows && nc in 0 until cols) {
                                grid[nr][nc]?.let { removed.add(it) }
                            }
                        }
                    }
                }
                else -> {}
            }
        }

        // Clear removed tiles from grid
        for (t in removed) {
            grid[t.row][t.col] = null
        }

        // Add newly formed specials
        for (s in createdSpecials) {
            grid[s.row][s.col] = s
        }

        // Urdu cheer text based on combo depth
        val cheer = when (comboChain) {
            1 -> if (groups.any { it.count >= 4 }) "واہ بھائی واہ!" else null
            2 -> "کمال کر دیا!"
            3 -> "شاباش اوئے!"
            else -> "زبردست جانی! مہران اوورٹیک!"
        }

        val points = removed.size * 50 * comboChain

        return CascadeStep(
            removedTiles = removed,
            createdSpecials = createdSpecials,
            pointsEarned = points,
            comboMultiplier = comboChain,
            urduCheer = cheer
        )
    }

    /**
     * Applies gravity: drops tiles down and generates new random vehicles at top.
     */
    fun applyGravity(): Boolean {
        var changed = false

        for (c in 0 until cols) {
            var emptyRow = rows - 1
            for (r in rows - 1 downTo 0) {
                val current = grid[r][c]
                if (current != null) {
                    if (emptyRow != r) {
                        grid[emptyRow][c] = current
                        grid[r][c] = null
                        current.row = emptyRow
                        changed = true
                    }
                    emptyRow--
                }
            }

            // Spawn new vehicles at top
            for (r in emptyRow downTo 0) {
                val newTile = Tile(
                    type = VehicleType.random(),
                    special = SpecialType.NONE,
                    row = r,
                    col = c
                )
                grid[r][c] = newTile
                changed = true
            }
        }

        return changed
    }
}
