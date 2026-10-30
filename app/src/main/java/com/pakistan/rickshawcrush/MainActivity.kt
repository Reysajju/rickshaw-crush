package com.pakistan.rickshawcrush

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pakistan.rickshawcrush.ui.RickshawGameView

class MainActivity : AppCompatActivity(), RickshawGameView.GameListener {

    private lateinit var gameView: RickshawGameView
    private lateinit var tvScore: TextView
    private lateinit var tvTarget: TextView
    private lateinit var tvMoves: TextView
    private lateinit var tvCheerBanner: TextView
    private lateinit var btnSound: ImageButton
    private lateinit var btnRestart: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameView = findViewById(R.id.game_view)
        tvScore = findViewById(R.id.tv_score)
        tvTarget = findViewById(R.id.tv_target)
        tvMoves = findViewById(R.id.tv_moves)
        tvCheerBanner = findViewById(R.id.tv_cheer_banner)
        btnSound = findViewById(R.id.btn_sound)
        btnRestart = findViewById(R.id.btn_restart)

        gameView.listener = this

        tvTarget.text = gameView.targetScore.toString()
        tvMoves.text = gameView.movesLeft.toString()
        tvScore.text = gameView.score.toString()

        btnSound.setOnClickListener {
            val enabled = !gameView.soundFX.isEnabled
            gameView.soundFX.isEnabled = enabled
            btnSound.setImageResource(if (enabled) android.R.drawable.ic_lock_silent_mode_off else android.R.drawable.ic_lock_silent_mode)
        }

        btnRestart.setOnClickListener {
            gameView.restartGame()
        }
    }

    override fun onScoreChanged(newScore: Int) {
        runOnUiThread {
            tvScore.text = newScore.toString()
        }
    }

    override fun onMovesChanged(remainingMoves: Int) {
        runOnUiThread {
            tvMoves.text = remainingMoves.toString()
        }
    }

    override fun onUrduCheer(message: String) {
        runOnUiThread {
            tvCheerBanner.text = message
            tvCheerBanner.visibility = View.VISIBLE
            tvCheerBanner.alpha = 0f
            tvCheerBanner.scaleX = 0.5f
            tvCheerBanner.scaleY = 0.5f

            tvCheerBanner.animate()
                .alpha(1f)
                .scaleX(1.15f)
                .scaleY(1.15f)
                .setDuration(300)
                .setInterpolator(OvershootInterpolator())
                .withEndAction {
                    tvCheerBanner.animate()
                        .alpha(0f)
                        .scaleX(0.8f)
                        .scaleY(0.8f)
                        .setStartDelay(1000)
                        .setDuration(300)
                        .withEndAction {
                            tvCheerBanner.visibility = View.GONE
                        }
                        .start()
                }
                .start()
        }
    }

    override fun onLevelWon(finalScore: Int) {
        runOnUiThread {
            showEndDialog(
                title = getString(R.string.win_title),
                message = getString(R.string.win_message, finalScore),
                buttonText = getString(R.string.next_level)
            ) {
                gameView.advanceNextLevel()
                tvTarget.text = gameView.targetScore.toString()
            }
        }
    }

    override fun onGameOver() {
        runOnUiThread {
            showEndDialog(
                title = getString(R.string.lose_title),
                message = getString(R.string.lose_message),
                buttonText = getString(R.string.retry)
            ) {
                gameView.restartGame()
            }
        }
    }

    private fun showEndDialog(title: String, message: String, buttonText: String, onAction: () -> Unit) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_game_over)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvTitle = dialog.findViewById<TextView>(R.id.dialog_title)
        val tvMsg = dialog.findViewById<TextView>(R.id.dialog_message)
        val btnAction = dialog.findViewById<Button>(R.id.dialog_action_btn)

        tvTitle.text = title
        tvMsg.text = message
        btnAction.text = buttonText

        btnAction.setOnClickListener {
            dialog.dismiss()
            onAction()
        }

        dialog.show()
    }
}
