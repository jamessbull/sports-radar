import sportRadar.Clock
import java.time.LocalDateTime

class TestClock(val testTimes: List<LocalDateTime>) : Clock {

    private var index = 0
    override fun now(): LocalDateTime {
        index  +=1
        return testTimes[index -1]
    }
}