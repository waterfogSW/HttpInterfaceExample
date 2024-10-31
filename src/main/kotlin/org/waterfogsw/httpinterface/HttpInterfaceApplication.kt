package org.waterfogsw.httpinterface

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HttpInterfaceApplication

fun main(args: Array<String>) {
    runApplication<HttpInterfaceApplication>(*args)
}
