import sportsRadar.ScoreBoard.Companion.emptyScoreBoard
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import sportsRadar.*

class ScoreBoardTest {

    private val england = FootballTeam("England")
    private val germany = FootballTeam("Germany")
    private val home = Home(england)
    private val away = Away(germany)

    @Test
    fun `can start a new game on an empty scoreboard`() {
        val scoreBoard = emptyScoreBoard()
            .startGame(home, away)
        val expectedGame = Game(home, away, Score(0, 0))
        assertThat(scoreBoard.getGameFor(home), equalTo(expectedGame))
    }

    @Test
    fun `can not start two games for the same home team`() {
        val result = runCatching {
            emptyScoreBoard()
                .startGame(home, away)
                .startGame(home, away)
        }

        result
            .onSuccess { fail("Should not be able to start two games for the same home team") }
            .onFailure {
                assertThat(it.message, equalTo("Can not start two games at once for home team ${home.team.name}"))
            }
    }

}