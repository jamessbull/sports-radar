import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import sportRadar.*
import sportRadar.ScoreBoard.Companion.emptyScoreBoard
import java.time.LocalDateTime

class ScoreBoardTest {

    private val england = FootballTeam("England")
    private val germany = FootballTeam("Germany")
    private val brazil = FootballTeam("Brazil")
    private val italy = FootballTeam("Italy")

    private val now = LocalDateTime.now()
    private val testTimes = listOf(
        now,
        now.plusHours(1),
        now.plusHours(2),
        now.plusHours(3),
        now.plusHours(4)
    )
    private val testClock = TestClock(testTimes)

    @Test
    fun `can start a new game on an empty scoreboard`() {
        val scoreBoard = emptyScoreBoard(testClock)
            .startGame(Home(england), Away(germany))
        val expectedGame = Game(testTimes[0], Home(england), Away(germany), Score(0, 0))
        assertThat(scoreBoard.getGameFor(england), equalTo(expectedGame))
    }

    @Test
    fun `a team can not play itself`() {
        val result = runCatching {
            emptyScoreBoard(testClock)
                .startGame(Home(england), Away(england))
        }
        assertFailure(result, "${england.name} can not play themselves")
    }

    @Test
    fun `can not start two identical games`() {
        val result = runCatching {
            emptyScoreBoard(testClock)
                .startGame(Home(england), Away(germany))
                .startGame(Home(england), Away(germany))
        }
        assertFailure(result, "Can not start two games at once for team ${Home(england).team.name}")
    }

    @Test
    fun `can not start a game where the home team is already playing away`() {
        val brazil = FootballTeam("Brazil")
        val result = runCatching {
            emptyScoreBoard(testClock)
                .startGame(Home(brazil), Away(england))
                .startGame(Home(england), Away(germany))
        }

        assertFailure(result, "Can not start two games at once for team ${england.name}")

    }

    @Test
    fun `can not start a game where the home team is already playing at home`() {
        val brazil = FootballTeam("Brazil")
        val result = runCatching {
            emptyScoreBoard(testClock)
                .startGame(Home(brazil), Away(england))
                .startGame(Home(brazil), Away(germany))
        }

        assertFailure(result, "Can not start two games at once for team ${brazil.name}")

    }

    @Test
    fun `can not start a game where the away team is already playing at home`() {
        val brazil = FootballTeam("Brazil")
        val result = runCatching {
            emptyScoreBoard(testClock)
                .startGame(Home(germany), Away(england))
                .startGame(Home(brazil), Away(germany))
        }

        assertFailure(result, "Can not start two games at once for team ${germany.name}")

    }

    @Test
    fun `can not start a game where the away team is already playing away`() {
        val result = runCatching {
            emptyScoreBoard(testClock)
                .startGame(Home(germany), Away(england))
                .startGame(Home(brazil), Away(england))
        }

        assertFailure(result, "Can not start two games at once for team ${england.name}")

    }

    @Test
    fun `can start multiple games when all teams are different`() {
        val scoreBoard = emptyScoreBoard(testClock)
            .startGame(Home(germany), Away(england))
            .startGame(Home(brazil), Away(italy))

        val actualBrazilGame = scoreBoard.getGameFor(brazil)
        val expectedBrazilGame = Game(testTimes[1], Home(brazil), Away(italy), Score(0, 0))
        assertThat(actualBrazilGame, equalTo(expectedBrazilGame))

        val actualGermanyGame = scoreBoard.getGameFor(germany)
        val expectedGermanyGame = Game(testTimes[0], Home(germany), Away(england), Score(0, 0))
        assertThat(actualGermanyGame, equalTo(expectedGermanyGame))
    }

    @Test
    fun `can update scores`() {
        val scoreBoard = emptyScoreBoard(testClock)
            .startGame(Home(germany), Away(england))
            .startGame(Home(brazil), Away(italy))
            .updateScore(brazil, Score(2,1))
            .updateScore(england, Score(0,10))
            .updateScore(england, Score(0,11))
            .updateScore(england, Score(0,12))
            .updateScore(england, Score(0,13))

        assertThat(scoreBoard.getGameFor(brazil), equalTo(Game(testTimes[1], Home(brazil), Away(italy), Score(2, 1))))
        assertThat(scoreBoard.getGameFor(england), equalTo(Game(testTimes[0], Home(germany), Away(england), Score(0, 13))))
    }

    @Test
    fun `can not update score for a game that has not started`() {
        val result = runCatching {
            emptyScoreBoard(testClock)
                .startGame(Home(germany), Away(england))
                .updateScore(brazil, Score(2,1))
        }

        assertFailure(result, "Can not update score for ${brazil.name} game. Game has not started.")

    }

    @Test
    fun `can finish all games that have been started and nobody has scored by either team`() {
        val scoreBoard = emptyScoreBoard(testClock)
            .startGame(Home(germany), Away(england))
            .finishGame(england)
            .startGame(Home(brazil), Away(italy))
            .finishGame(brazil)

        assertThat(scoreBoard, equalTo(emptyScoreBoard(testClock)))
    }

    @Test
    fun `can finish a single game that has been started and some teams have scored`() {
        val scoreBoard = emptyScoreBoard(testClock)
            .startGame(Home(germany), Away(england))
            .updateScore(england, Score(1, 2))
            .startGame(Home(brazil), Away(italy))
            .updateScore(england, Score(1,3))
            .finishGame(england)
            .updateScore(brazil, Score(2,1))

        assertThat(scoreBoard, equalTo(ScoreBoard(testClock,
            listOf(
                Game(testTimes[1], Home(brazil), Away(italy), Score(2, 1))
            ),
        )))
    }

    @Test
    fun `can not finish a game that is not on the scoreboard`() {
        val result = runCatching {
            emptyScoreBoard(TestClock(listOf(LocalDateTime.now())))
                .startGame(Home(germany), Away(england))
                .finishGame(italy)
        }

        assertFailure(result, "Can not finish ${italy.name} game. Game has not started.")
    }

    @Test
    fun `when all score totals are different then summary is ordered by total score descending`() {
        val mexico = FootballTeam("Mexico")
        val canada = FootballTeam("Canada")

        val scoreBoard = emptyScoreBoard(testClock)
            .startGame(Home(germany), Away(england))
            .startGame(Home(brazil), Away(italy))
            .startGame(Home(mexico), Away(canada))
            .updateScore(germany, Score(1, 2))
            .updateScore(brazil, Score(2, 2))
            .updateScore(mexico, Score(3, 2))

        assertThat(scoreBoard.summary(), equalTo(listOf(
            Game(testTimes[2], Home(mexico), Away(canada), Score(3, 2)),
            Game(testTimes[1], Home(brazil), Away(italy), Score(2, 2)),
            Game(testTimes[0], Home(germany), Away(england), Score(1, 2)),
        )))
    }

    @Test
    fun `when all scores are the same then the games are ordered by date time`() {
        val scoreBoard = emptyScoreBoard(testClock)
            .startGame(Home(germany), Away(england))
            .startGame(Home(brazil), Away(italy))

        assertThat(scoreBoard.summary(), equalTo(listOf(
            Game(testTimes[1], Home(brazil), Away(italy), Score(0, 0)),
            Game(testTimes[0], Home(germany), Away(england), Score(0, 0))
        )))
    }

    @Test
    fun `when some games have equal score then those games are ordered by match start time otherwise in total score order`() {

        val argentina = FootballTeam("Argentina")
        val australia = FootballTeam("Australia")
        val mexico = FootballTeam("Mexico")
        val canada = FootballTeam("Canada")
        val spain = FootballTeam("Spain")
        val france = FootballTeam("France")
        val uruguay = FootballTeam("Uruguay")

        val scoreBoard = emptyScoreBoard(testClock)
            .startGame(Home(mexico), Away(canada))
            .startGame(Home(spain), Away(brazil))
            .startGame(Home(germany), Away(france))
            .startGame(Home(uruguay), Away(italy))
            .startGame(Home(argentina), Away(australia))
            .updateScore(mexico, Score(0, 5))
            .updateScore(spain, Score(10, 2))
            .updateScore(germany, Score(2, 2))
            .updateScore(uruguay, Score(6, 6))
            .updateScore(argentina, Score(3, 1))

        assertThat(scoreBoard.summary(), equalTo(listOf(
            Game(testTimes[3], Home(uruguay), Away(italy), Score(6, 6)),
            Game(testTimes[1], Home(spain), Away(brazil), Score(10, 2)),
            Game(testTimes[0], Home(mexico), Away(canada), Score(0, 5)),
            Game(testTimes[4], Home(argentina), Away(australia), Score(3, 1)),
            Game(testTimes[2], Home(germany), Away(france), Score(2, 2))
        )))
    }

    private fun assertFailure(result: Result<ScoreBoard>, message: String) {
        result
            .onSuccess { fail("Error message should be '$message' but request succeeded.") }
            .onFailure {
                assertThat(it.message, equalTo(message))
            }
    }

}