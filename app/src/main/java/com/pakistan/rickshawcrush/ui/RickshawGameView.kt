package com.pakistan.rickshawcrush.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.pakistan.rickshawcrush.engine.BoardManager
import com.pakistan.rickshawcrush.engine.ParticleEngine
import com.pakistan.rickshawcrush.engine.SoundFXEngine
import com.pakistan.rickshawcrush.model.Tile
import kotlinx.coroutines.*
import kotlin.math.abs
import kotlin.math.hypot

/**
 * Native Android Game SurfaceView running at 60 FPS
 */
class RickshawGameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback, Runnable {

    interface GameListener {
        fun onScoreChanged(newScore: Int)
        fun onMovesChanged(remainingMoves: Int)
        fun onUrduCheer(message: String)
        fun onLevelWon(finalScore: Int)
        fun onGameOver()
    }

    var listener: GameListener? = null
    val boardManager = BoardManager()
    val soundFX = SoundFXEngine()
    private val renderer = RickshawRenderer()
    private val particles = ParticleEngine()

    private var gameThread: Thread? = null
    @Volatile private var isRunning = false

    private var tileSize: Float = 0f
    private var isBusy = false
    private var selectedTile: Tile? = null
    private var touchDownX = 0f
    private var touchDownY = 0f

    var score = 0
        private set
    var movesLeft = 25
        private set
    var targetScore = 3000
        private set
    var level = 1
        private set

    private val bgPaint = Paint().apply { color = Color.parseColor("#0C2017") }
    private val asphaltPaint1 = Paint().apply { color = Color.parseColor("#163625") }
    private val asphaltPaint2 = Paint().apply { color = Color.parseColor("#10251B") }
    private val selectStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.parseColor("#FFD700")
    }

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val size = width.coerceAtMost(height)
        tileSize = size / boardManager.cols.toFloat()
        syncTilePositions(immediate = true)

        isRunning = true
        gameThread = Thread(this)
        gameThread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        val size = w.coerceAtMost(h)
        tileSize = size / boardManager.cols.toFloat()
        syncTilePositions(immediate = true)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isRunning = false
        try {
            gameThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        var lastTime = System.nanoTime()
        while (isRunning) {
            val now = System.nanoTime()
            val dt = ((now - lastTime) / 1_000_000_000.0f).coerceAtMost(0.1f)
            lastTime = now

            updatePhysics(dt)
            drawFrame()

            try {
                Thread.sleep(16) // ~60 FPS
            } catch (e: InterruptedException) {
                break
            }
        }
    }

    private fun updatePhysics(dt: Float) {
        val speed = 20f
        for (r in 0 until boardManager.rows) {
            for (c in 0 until boardManager.cols) {
                val t = boardManager.grid[r][c] ?: continue
                t.x += (t.targetX - t.x) * (speed * dt).coerceAtMost(1f)
                t.y += (t.targetY - t.y) * (speed * dt).coerceAtMost(1f)

                if (t.scale > 1.0f) {
                    t.scale = (t.scale - dt * 2f).coerceAtLeast(1.0f)
                }
                if (t.isRemoving) {
                    t.scale = (t.scale - dt * 6f).coerceAtLeast(0f)
                    t.alpha = (t.alpha - dt * 6f).coerceAtLeast(0f)
                }
            }
        }
        particles.update(dt)
    }

    private fun drawFrame() {
        if (!holder.surface.isValid) return
        val canvas: Canvas = holder.lockCanvas() ?: return

        try {
            // 1. Draw Road Asphalt Grid
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

            for (r in 0 until boardManager.rows) {
                for (c in 0 until boardManager.cols) {
                    val px = c * tileSize
                    val py = r * tileSize
                    val paint = if ((r + c) % 2 == 0) asphaltPaint1 else asphaltPaint2
                    canvas.drawRect(px + 3f, py + 3f, px + tileSize - 3f, py + tileSize - 3f, paint)
                }
            }

            // 2. Highlight selected tile
            selectedTile?.let {
                val sx = it.col * tileSize
                val sy = it.row * tileSize
                canvas.drawRect(sx + 4f, sy + 4f, sx + tileSize - 4f, sy + tileSize - 4f, selectStrokePaint)
            }

            // 3. Draw Tiles
            for (r in 0 until boardManager.rows) {
                for (c in 0 until boardManager.cols) {
                    val tile = boardManager.grid[r][c]
                    if (tile != null && tile.alpha > 0f) {
                        renderer.drawTile(canvas, tile, tileSize)
                    }
                }
            }

            // 4. Draw Particles and FX
            particles.draw(canvas)
        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isBusy || movesLeft <= 0) return true

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDownX = event.x
                touchDownY = event.y
                val col = (touchDownX / tileSize).toInt()
                val row = (touchDownY / tileSize).toInt()
                if (row in 0 until boardManager.rows && col in 0 until boardManager.cols) {
                    selectedTile = boardManager.grid[row][col]
                    soundFX.playRickshawHorn()
                }
            }
            MotionEvent.ACTION_UP -> {
                val dx = event.x - touchDownX
                val dy = event.y - touchDownY
                val dist = hypot(dx.toDouble(), dy.toDouble()).toFloat()

                selectedTile?.let { startTile ->
                    if (dist > 30f) {
                        val targetRow = startTile.row + if (abs(dx) > abs(dy)) 0 else (if (dy > 0) 1 else -1)
                        val targetCol = startTile.col + if (abs(dx) > abs(dy)) (if (dx > 0) 1 else -1) else 0

                        if (targetRow in 0 until boardManager.rows && targetCol in 0 until boardManager.cols) {
                            executeSwap(startTile.row, startTile.col, targetRow, targetCol)
                        }
                    }
                }
                selectedTile = null
            }
        }
        return true
    }

    private fun executeSwap(r1: Int, c1: Int, r2: Int, c2: Int) {
        scope.launch {
            isBusy = true
            soundFX.playSwapSound()

            val t1 = boardManager.grid[r1][c1] ?: return@launch
            val t2 = boardManager.grid[r2][c2] ?: return@launch

            // Animate swap move
            t1.targetX = c2 * tileSize
            t1.targetY = r2 * tileSize
            t2.targetX = c1 * tileSize
            t2.targetY = r1 * tileSize

            delay(200)

            boardManager.grid[r1][c1] = t2
            boardManager.grid[r2][c2] = t1
            t1.row = r2; t1.col = c2
            t2.row = r1; t2.col = c1

            // Check special combo interaction
            val specialStep = boardManager.processSpecialCombo(t1, t2)
            if (specialStep != null) {
                movesLeft--
                score += specialStep.pointsEarned
                listener?.onMovesChanged(movesLeft)
                listener?.onScoreChanged(score)
                specialStep.urduCheer?.let { listener?.onUrduCheer(it) }

                for (t in specialStep.removedTiles) {
                    t.isRemoving = true
                    particles.addExplosion(t.x + tileSize / 2f, t.y + tileSize / 2f, Color.parseColor("#FFD700"))
                }
                delay(250)

                boardManager.applyGravity()
                syncTilePositions(immediate = false)
                delay(300)

                resolveCascades()
                checkGameState()
                isBusy = false
                return@launch
            }

            // Normal match check
            val matches = boardManager.findMatches()
            if (matches.isNotEmpty()) {
                movesLeft--
                listener?.onMovesChanged(movesLeft)
                resolveMatchAndCascades(matches, focalTile = t1, combo = 1)
                checkGameState()
            } else {
                // Revert swap
                t1.targetX = c1 * tileSize
                t1.targetY = r1 * tileSize
                t2.targetX = c2 * tileSize
                t2.targetY = r2 * tileSize
                delay(200)

                boardManager.grid[r1][c1] = t1
                boardManager.grid[r2][c2] = t2
                t1.row = r1; t1.col = c1
                t2.row = r2; t2.col = c2
            }

            isBusy = false
        }
    }

    private suspend fun resolveMatchAndCascades(matches: List<com.pakistan.rickshawcrush.model.MatchGroup>, focalTile: Tile?, combo: Int) {
        soundFX.playMatchSound(combo)

        val step = boardManager.resolveMatchStep(matches, comboChain = combo, focalTile = focalTile)
        score += step.pointsEarned
        listener?.onScoreChanged(score)
        step.urduCheer?.let { listener?.onUrduCheer(it) }

        for (t in step.removedTiles) {
            t.isRemoving = true
            particles.addExplosion(t.x + tileSize / 2f, t.y + tileSize / 2f, t.type.primaryColor)
        }

        delay(220)

        // Drop down & spawn new
        boardManager.applyGravity()
        syncTilePositions(immediate = false)
        delay(250)

        // Cascade chain check
        val nextMatches = boardManager.findMatches()
        if (nextMatches.isNotEmpty()) {
            delay(150)
            resolveMatchAndCascades(nextMatches, null, combo + 1)
        }
    }

    private suspend fun resolveCascades() {
        var matches = boardManager.findMatches()
        var chain = 1
        while (matches.isNotEmpty()) {
            resolveMatchAndCascades(matches, null, chain)
            chain++
            matches = boardManager.findMatches()
        }
    }

    private fun checkGameState() {
        if (score >= targetScore) {
            soundFX.playPressureHorn()
            listener?.onLevelWon(score)
        } else if (movesLeft <= 0) {
            soundFX.playExplosionSound()
            listener?.onGameOver()
        }
    }

    fun restartGame() {
        score = 0
        movesLeft = 25
        boardManager.generateInitialBoard()
        syncTilePositions(immediate = true)
        listener?.onScoreChanged(score)
        listener?.onMovesChanged(movesLeft)
    }

    fun advanceNextLevel() {
        level++
        targetScore += 2500
        movesLeft = 25
        boardManager.generateInitialBoard()
        syncTilePositions(immediate = true)
        listener?.onScoreChanged(score)
        listener?.onMovesChanged(movesLeft)
    }

    private fun syncTilePositions(immediate: Boolean) {
        for (r in 0 until boardManager.rows) {
            for (c in 0 until boardManager.cols) {
                val t = boardManager.grid[r][c] ?: continue
                t.targetX = c * tileSize
                t.targetY = r * tileSize
                if (immediate) {
                    t.x = t.targetX
                    t.y = t.targetY
                }
            }
        }
    }
}
