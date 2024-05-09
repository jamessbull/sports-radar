import sportsRadar.ScoreBoard.Companion.emptyScoreBoard
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import sportsRadar.*

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

}