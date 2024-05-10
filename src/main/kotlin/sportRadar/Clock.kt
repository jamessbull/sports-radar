package sportRadar

import java.time.LocalDateTime

interface Clock {
    fun now(): LocalDateTime
}