package sportsRadar

import sportsRadar.Score.Companion.initialScore

data class Game(val home: Home, val away: Away, val score: Score) {

    fun features(team: FootballTeam) = home.team == team || away.team == team

    companion object {
        fun newGame(home: Home, away: Away) = Game(home, away, initialScore())
    }
}