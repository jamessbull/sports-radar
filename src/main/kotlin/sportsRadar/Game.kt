package sportsRadar

import sportsRadar.Score.Companion.initialScore

data class Game(val home: Home, val away: Away, val score: Score) {
    companion object {
        fun newGame(home: Home, away: Away) = Game(home, away, initialScore())
    }
}