package sportsRadar

import sportsRadar.Game.Companion.newGame

class ScoreBoard(private val games: List<Game>) {
    fun startGame(home: Home, away: Away) : ScoreBoard {
        if(gameInProgress(home)) throw RuntimeException("Can not start two games at once for home team ${home.team.name}")
        return ScoreBoard(listOf(newGame(home, away)))
    }
    fun gameInProgress(home: Home) = games.any { it.home == home }
    fun getGameFor(homeTeam: Home) = games.find { it.home == homeTeam } ?:throw RuntimeException("no game for $homeTeam")

    companion object {
        fun emptyScoreBoard() = ScoreBoard(emptyList())
    }
}