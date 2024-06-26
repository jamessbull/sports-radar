package sportRadar

import sportRadar.Game.Companion.newGame

data class ScoreBoard(private val clock: Clock, val games: List<Game>) {
    fun startGame(home: Home, away: Away): ScoreBoard {
        throwIfAlreadyPlaying(listOf(home.team, away.team))
        return ScoreBoard(clock, games + newGame(clock, home, away))
    }

    fun getGameFor(team: FootballTeam) = games.single { it.features(team) }

    fun updateScore(team: FootballTeam, score: Score): ScoreBoard {
        val existingGame = runCatching { getGameFor(team) }
            .onFailure { throw RuntimeException("Can not update score for ${team.name} game. Game has not started.") }
            .getOrThrow()
        return ScoreBoard(clock,otherGames(team) + existingGame.copy(score = score))
    }

    fun finishGame(team: FootballTeam): ScoreBoard {
        runCatching { getGameFor(team) }.onFailure {
            throw RuntimeException("Can not finish ${team.name} game. Game has not started.")
        }
        return ScoreBoard(clock, otherGames(team))
    }

    fun summary() = games.sortedWith(Game.summaryOrder())

    private fun throwIfAlreadyPlaying(teams: List<FootballTeam>) {
        teams.forEach { team ->
            if (games.any { game -> game.features(team) })
                throw RuntimeException("Can not start two games at once for team ${team.name}")
        }
    }

    private fun otherGames(team: FootballTeam) = games.filter { !it.features(team) }

    companion object {
        fun emptyScoreBoard(clock: Clock) = ScoreBoard(clock, emptyList())
    }
}