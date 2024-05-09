package sportsRadar

import sportsRadar.Game.Companion.newGame

class ScoreBoard(private val games: List<Game>) {
    fun startGame(home: Home, away: Away) : ScoreBoard {
        if(home.team == away.team) throw RuntimeException("${home.team.name} can not play themselves")
        throwIfAlreadyPlaying(home.team)
        throwIfAlreadyPlaying(away.team)
        return ScoreBoard(games + newGame(home, away))
    }

    private fun throwIfAlreadyPlaying(team: FootballTeam) {
        if(games.any { game -> game.features(team) })
            throw RuntimeException("Can not start two games at once for team ${team.name}")
    }

    fun getGameFor(homeTeam: Home) = games.find { it.home == homeTeam } ?:throw RuntimeException("no game for $homeTeam")

    companion object {
        fun emptyScoreBoard() = ScoreBoard(emptyList())
    }
}