package sportsRadar

class ScoreBoard(private val games: List<Game>) {
    fun startGame(home: Home, away: Away) : ScoreBoard {
        return ScoreBoard(listOf(Game(home, away, Score(0, 0))))
    }

    fun getGameFor(homeTeam: Home): Game = games.find { it.home == homeTeam } ?:throw RuntimeException("no game for $homeTeam")

    companion object {
        fun emptyScoreBoard() = ScoreBoard(emptyList())
    }
}