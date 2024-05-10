package sportRadar

data class Score(val homeGoals: Int, val awayGoals: Int) {
    fun total() = homeGoals + awayGoals

    companion object {
        fun initialScore() = Score(0,0)
    }
}