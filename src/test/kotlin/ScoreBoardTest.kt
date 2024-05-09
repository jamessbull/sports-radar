import sportsRadar.ScoreBoard.Companion.emptyScoreBoard
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import sportsRadar.*

class ScoreBoardTest {

    private val england = FootballTeam("England")
    private val germany = FootballTeam("Germany")
    private val brazil = FootballTeam("Brazil")


    @Test
    fun `can start a new game on an empty scoreboard`() {
        val scoreBoard = emptyScoreBoard()
            .startGame(Home(england), Away(germany))
        val expectedGame = Game(Home(england), Away(germany), Score(0, 0))
        assertThat(scoreBoard.getGameFor(Home(england)), equalTo(expectedGame))
    }

    @Test
    fun `can not start two identical games`() {
        val result = runCatching {
            emptyScoreBoard()
                .startGame(Home(england), Away(germany))
                .startGame(Home(england), Away(germany))
        }
        assertFailure(result, "Can not start two games at once for team ${Home(england).team.name}")
    }

    @Test
    fun `can not start a game where the home team is already playing away`() {
        val brazil = FootballTeam("Brazil")
        val result = runCatching {
            emptyScoreBoard()
                .startGame(Home(brazil), Away(england))
                .startGame(Home(england), Away(germany))
        }

        assertFailure(result, "Can not start two games at once for team ${england.name}")

    }

    @Test
    fun `can not start a game where the home team is already playing at home`() {
        val brazil = FootballTeam("Brazil")
        val result = runCatching {
            emptyScoreBoard()
                .startGame(Home(brazil), Away(england))
                .startGame(Home(brazil), Away(germany))
        }

        assertFailure(result, "Can not start two games at once for team ${brazil.name}")

    }

    @Test
    fun `can not start a game where the away team is already playing at home`() {
        val brazil = FootballTeam("Brazil")
        val result = runCatching {
            emptyScoreBoard()
                .startGame(Home(germany), Away(england))
                .startGame(Home(brazil), Away(germany))
        }

        assertFailure(result, "Can not start two games at once for team ${germany.name}")

    }

    @Test
    fun `can not start a game where the away team is already playing away`() {
        val result = runCatching {
            emptyScoreBoard()
                .startGame(Home(germany), Away(england))
                .startGame(Home(brazil), Away(england))
        }

        assertFailure(result, "Can not start two games at once for team ${england.name}")

    }

    private fun assertFailure(result: Result<ScoreBoard>, message: String) {
        result
            .onSuccess { fail("Should not be able to start two games for the same team") }
            .onFailure {
                assertThat(it.message, equalTo(message))
            }
    }

}