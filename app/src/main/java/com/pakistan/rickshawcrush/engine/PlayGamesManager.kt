package com.pakistan.rickshawcrush.engine

import android.app.Activity
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk

/**
 * Handles automatic Google Play Games Services V2 sign-in.
 * Players are automatically recognized by their Google Play Gamer ID
 * with ZERO registration, ZERO username/password, and ZERO signup forms!
 */
object PlayGamesManager {

    var isAuthenticated = false
        private set
    var playerName = ""
        private set

    fun initialize(activity: Activity) {
        try {
            PlayGamesSdk.initialize(activity)
            val gamesSignInClient = PlayGames.getGamesSignInClient(activity)

            gamesSignInClient.isAuthenticated.addOnCompleteListener { isAuthenticatedTask ->
                val authenticated = isAuthenticatedTask.isSuccessful && isAuthenticatedTask.result.isAuthenticated
                if (authenticated) {
                    isAuthenticated = true
                    PlayGames.getPlayersClient(activity).currentPlayer.addOnCompleteListener { playerTask ->
                        if (playerTask.isSuccessful) {
                            playerName = playerTask.result.displayName
                        }
                    }
                } else {
                    // Attempt automatic silent sign-in
                    gamesSignInClient.signIn()
                }
            }
        } catch (e: Exception) {
            // Standalone offline fallback; game continues smoothly even without Play Services
            e.printStackTrace()
        }
    }
}
