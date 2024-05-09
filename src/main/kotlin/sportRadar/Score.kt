package sportRadar

data class Score(val homeGoals: Int, val awayGoals: Int) {
    companion object {
        fun initialScore() = Score(0,0)
    }
}