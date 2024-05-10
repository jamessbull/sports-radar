package sportRadar

import sportRadar.Game.Companion.newGame

data class ScoreBoard(private val games: List<Game>) {
    fun startGame(home: Home, away: Away): ScoreBoard {
        throwIfAlreadyPlaying(listOf(home.team, away.team))
        return ScoreBoard(games + newGame(home, away))
    }

    fun getGameFor(team: FootballTeam) = games.single { it.features(team) }

    fun updateScore(team: FootballTeam, score: Score): ScoreBoard {
        val existingGame = runCatching { getGameFor(team) }
            .onFailure { throw RuntimeException("Can not update score for ${team.name} game. Game has not started.") }
            .getOrThrow()
        return ScoreBoard(otherGames(team) + existingGame.copy(score = score))
    }

    fun finishGame(team: FootballTeam): ScoreBoard {
        runCatching { getGameFor(team) }.onFailure {
            throw RuntimeException("Can not finish ${team.name} game. Game has not started.")
        }
        return ScoreBoard(otherGames(team))
    }

    private fun throwIfAlreadyPlaying(teams: List<FootballTeam>) {
        teams.forEach { team ->
            if (games.any { game -> game.features(team) })
                throw RuntimeException("Can not start two games at once for team ${team.name}")
        }
    }
    private fun otherGames(team: FootballTeam) = games.filter { !it.features(team) }
    fun summary(): List<Game> {
        return games.sortedWith(Game.summaryOrder())
    }



    companion object {
        fun emptyScoreBoard() = ScoreBoard(emptyList())
    }
}