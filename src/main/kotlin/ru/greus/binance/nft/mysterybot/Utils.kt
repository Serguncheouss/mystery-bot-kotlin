package ru.greus.binance.nft.mysterybot

import org.json.JSONObject
import org.openqa.selenium.TimeoutException
import java.time.Duration
import java.util.*
import kotlin.jvm.Throws
import kotlin.reflect.KProperty0

object Utils {
    private val formatTime: (Long) -> String = {
        with(Duration.ofMillis(it)) {
            String.format("%02d:%02d:%02d", toHours(), toMinutesPart(), toSecondsPart())
        }
    }
    var timeDelta = 0L // Server time synchronization
        set(value) {
            field = value
            if (value != 0L) println("Time configured successfully.")
        }

    @Throws(TimeoutException::class)
    fun waitUntilNull(property: KProperty0<Any?>, timeout: Long = 10) {
        val start = Date().time

        while (Date().time - start < timeout * 1000) {
            if (property.get() == null) Thread.sleep(1000 / 5) else return
        }

        throw TimeoutException("Timed out after ${timeout * 1000}s waiting for object.")
    }

    fun waitFor(timeToWait: Long, interruptAhead: Long = 0, logSingleLine: Boolean = true) {
        val timeToWaitWithDelta = timeToWait + timeDelta
        var lastDelta = timeToWaitWithDelta - Date().time
        while (true) {
            val currentTime = Date().time
            val delta = timeToWaitWithDelta - currentTime
            // Logging
            if (lastDelta - delta > 1000) {
                val message = "Time left: ${formatTime.invoke(delta)}"
                if (logSingleLine) print(message + '\r') else println(message)
                lastDelta = delta
            }

            if (delta < interruptAhead * 1000) break
        }
    }

    fun mapToJSON(map: Map<String, Any?>): String {
        return JSONObject(map).toString()
    }
}