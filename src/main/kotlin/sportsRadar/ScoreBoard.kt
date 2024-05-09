package sportsRadar

import sportsRadar.Game.Companion.newGame

class ScoreBoard(private val games: List<Game>) {
    fun startGame(home: Home, away: Away): ScoreBoard {
        throwIfAlreadyPlaying(listOf(home.team, away.team))
        return ScoreBoard(games + newGame(home, away))
    }

    private fun throwIfAlreadyPlaying(teams: List<FootballTeam>) {
        teams.forEach { team ->
            if (games.any { game -> game.features(team) })
                throw RuntimeException("Can not start two games at once for team ${team.name}")
        }
    }

    fun getGameFor(homeTeam: Home) =
        games.find { it.home == homeTeam } ?: throw RuntimeException("no game for $homeTeam")

    companion object {
        fun emptyScoreBoard() = ScoreBoard(emptyList())
    }
}