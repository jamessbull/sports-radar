import sportRadar.ScoreBoard.Companion.emptyScoreBoard
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import sportRadar.*

class ScoreBoardTest {

    private val england = FootballTeam("England")
    private val germany = FootballTeam("Germany")
    private val brazil = FootballTeam("Brazil")
    private val italy = FootballTeam("Italy")


    @Test
    fun `can start a new game on an empty scoreboard`() {
        val scoreBoard = emptyScoreBoard()
            .startGame(Home(england), Away(germany))
        val expectedGame = Game(Home(england), Away(germany), Score(0, 0))
        assertThat(scoreBoard.getGameFor(england), equalTo(expectedGame))
    }

    @Test
    fun `a team can not play itself`() {
        val result = runCatching {
            emptyScoreBoard()
                .startGame(Home(england), Away(england))
        }
        assertFailure(result, "${england.name} can not play themselves")
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

    @Test
    fun `can start multiple games when all teams are different`() {
        val scoreBoard = emptyScoreBoard()
            .startGame(Home(germany), Away(england))
            .startGame(Home(brazil), Away(italy))

        val actualBrazilGame = scoreBoard.getGameFor(brazil)
        val expectedBrazilGame = Game(Home(brazil), Away(italy), Score(0, 0))
        assertThat(actualBrazilGame, equalTo(expectedBrazilGame))

        val actualGermanyGame = scoreBoard.getGameFor(germany)
        val expectedGermanyGame = Game(Home(germany), Away(england), Score(0, 0))
        assertThat(actualGermanyGame, equalTo(expectedGermanyGame))
    }

    @Test
    fun `can update scores`() {
        val scoreBoard = emptyScoreBoard()
            .startGame(Home(germany), Away(england))
            .startGame(Home(brazil), Away(italy))
            .updateScore(brazil, Score(2,1))
            .updateScore(england, Score(0,10))
            .updateScore(england, Score(0,11))
            .updateScore(england, Score(0,12))
            .updateScore(england, Score(0,13))

        assertThat(scoreBoard.getGameFor(brazil), equalTo(Game(Home(brazil), Away(italy), Score(2, 1))))
        assertThat(scoreBoard.getGameFor(england), equalTo(Game(Home(germany), Away(england), Score(0, 13))))
    }

    @Test
    fun `can not update score for a game that has not started`() {
        val result = runCatching {
            emptyScoreBoard()
                .startGame(Home(germany), Away(england))
                .updateScore(brazil, Score(2,1))
        }

        assertFailure(result, "Can not update score for ${brazil.name} game. Game has not started.")

    }

    @Test
    fun `can finish all games that have been started and nobody has scored by either team`() {
        val scoreBoard = emptyScoreBoard()
            .startGame(Home(germany), Away(england))
            .finishGame(england)
            .startGame(Home(brazil), Away(italy))
            .finishGame(brazil)

        assertThat(scoreBoard, equalTo(emptyScoreBoard()))
    }

    @Test
    fun `can finish a single game that has been started and some teams have scored`() {
        val scoreBoard = emptyScoreBoard()
            .startGame(Home(germany), Away(england))
            .updateScore(england, Score(1, 2))
            .startGame(Home(brazil), Away(italy))
            .updateScore(england, Score(1,3))
            .finishGame(england)
            .updateScore(brazil, Score(2,1))

        assertThat(scoreBoard, equalTo(ScoreBoard(
            listOf(
                Game(Home(brazil), Away(italy), Score(2, 1))
            )
        )))
    }

    @Test
    fun `can not finish a game that is not on the scoreboard`() {
        val result = runCatching {
            emptyScoreBoard()
                .startGame(Home(germany), Away(england))
                .finishGame(italy)
        }

        assertFailure(result, "Can not finish ${italy.name} game. Game has not started.")
    }

    private fun assertFailure(result: Result<ScoreBoard>, message: String) {
        result
            .onSuccess { fail("Error message should be '$message' but request succeeded.") }
            .onFailure {
                assertThat(it.message, equalTo(message))
            }
    }

}