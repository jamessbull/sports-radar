package sportRadar

import sportRadar.Score.Companion.initialScore

data class Game(val home: Home, val away: Away, val score: Score) {
    init {
        if(home.team == away.team) throw RuntimeException("${home.team.name} can not play themselves")
    }

    fun features(team: FootballTeam) = home.team == team || away.team == team

    companion object {
        fun newGame(home: Home, away: Away) = Game(home, away, initialScore())
    }
}