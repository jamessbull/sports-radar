package sportRadar

import sportRadar.Score.Companion.initialScore
import java.time.LocalDateTime

data class Game(val matchStartTime: LocalDateTime, val home: Home, val away: Away, val score: Score) {
    init {
        if(home.team == away.team) throw RuntimeException("${home.team.name} can not play themselves")
    }

    fun features(team: FootballTeam) = home.team == team || away.team == team

    companion object {
        fun newGame(clock: Clock, home: Home, away: Away) = Game(clock.now(), home, away, initialScore())
        fun summaryOrder(): Comparator<Game> = compareBy<Game> { it.score.total() }.reversed()
    }
}