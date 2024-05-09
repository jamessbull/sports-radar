import ScoreBoardTest.ScoreBoard.Companion.emptyScoreBoard
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class ScoreBoardTest {

    val england = FootballTeam("England")
    val germany = FootballTeam("Germany")
    val homeTeam = Home(england)
    val awayTeam = Away(germany)

    @Test
    fun `can start a new game on an empty scoreboard`() {
        val scoreBoard = emptyScoreBoard()
            .startGame(homeTeam, awayTeam)
        val expectedGame = Game(homeTeam, awayTeam, Score(0, 0))
        assertThat(scoreBoard.getGameFor(homeTeam), equalTo(expectedGame))
    }

    data class FootballTeam(val name: String)
    data class Home(val team: FootballTeam)
    data class Away(val team: FootballTeam)
    data class Game(val home: Home, val away: Away, val score: Score)
    data class Score(val homeGoals: Int, val awayGoals: Int)

    class ScoreBoard(private val games: List<Game>) {
        fun startGame(home: Home, away: Away) : ScoreBoard{
            return ScoreBoard(listOf(Game(home, away, Score(0,0))))
        }

        fun getGameFor(homeTeam: Home): Game = games.find { it.home == homeTeam } ?:throw RuntimeException("no game for $homeTeam")

        companion object {
            fun emptyScoreBoard() = ScoreBoard(emptyList())
        }
    }
}