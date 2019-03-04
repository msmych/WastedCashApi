package wasted

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WastedCashApiApplication

fun main(args: Array<String>) {
    runApplication<WastedCashApiApplication>(*args)
}
