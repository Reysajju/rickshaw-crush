package com.pakistan.rickshawcrush.engine

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * High-performance Native Android Sound Synthesizer.
 * Synthesizes authentic Pakistani vehicular horns and game sounds directly
 * via AudioTrack without requiring heavy external asset files.
 */
class SoundFXEngine {

    var isEnabled: Boolean = true
    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Rickshaw / Bike Peep Horn (580Hz sawtooth-like pulse)
     */
    fun playRickshawHorn() {
        if (!isEnabled) return
        scope.launch {
            playTone(580, 110, 0.4f)
        }
    }

    /**
     * Bus Pressure Horn (Chords: 440Hz, 554Hz, 659Hz)
     */
    fun playPressureHorn() {
        if (!isEnabled) return
        scope.launch {
            playTone(440, 280, 0.3f)
            playTone(554, 280, 0.3f)
            playTone(659, 280, 0.35f)
        }
    }

    /**
     * Match Chime with pitch rising per combo
     */
    fun playMatchSound(combo: Int) {
        if (!isEnabled) return
        scope.launch {
            val freq = (340 + (combo * 70)).coerceAtMost(900)
            playTone(freq, 140, 0.35f)
        }
    }

    /**
     * Explosion / Cylinder Blast (Low bass drop)
     */
    fun playExplosionSound() {
        if (!isEnabled) return
        scope.launch {
            playTone(120, 320, 0.5f)
        }
    }

    /**
     * Laser / Striped Vehicle Sirens
     */
    fun playLaserSound() {
        if (!isEnabled) return
        scope.launch {
            playTone(850, 180, 0.35f)
        }
    }

    /**
     * Swap Whoosh Sound
     */
    fun playSwapSound() {
        if (!isEnabled) return
        scope.launch {
            playTone(320, 80, 0.2f)
        }
    }

    private fun playTone(freqHz: Int, durationMs: Int, volume: Float) {
        try {
            val sampleRate = 44100
            val numSamples = (durationMs * sampleRate) / 1000
            val generatedSnd = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                val angle = 2.0 * Math.PI * freqHz * time
                // Envelope decay to prevent clicks
                val envelope = 1.0 - (i.toDouble() / numSamples)
                val sample = (sin(angle) * envelope * Short.MAX_VALUE * volume).toInt()
                generatedSnd[i] = sample.toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(numSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(generatedSnd, 0, numSamples)
            audioTrack.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
