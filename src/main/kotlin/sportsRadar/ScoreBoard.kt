package sportsRadar

import sportsRadar.Game.Companion.newGame

class ScoreBoard(private val games: List<Game>) {
    fun startGame(home: Home, away: Away) : ScoreBoard {
        return ScoreBoard(listOf(newGame(home, away)))
    }

    fun getGameFor(homeTeam: Home) = games.find { it.home == homeTeam } ?:throw RuntimeException("no game for $homeTeam")

    companion object {
        fun emptyScoreBoard() = ScoreBoard(emptyList())
    }
}