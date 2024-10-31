package org.waterfogsw.httpinterface

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@EnableRetry
@SpringBootApplication
class HttpInterfaceApplication

fun main(args: Array<String>) {
    runApplication<HttpInterfaceApplication>(*args)
}
