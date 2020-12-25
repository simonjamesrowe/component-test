package com.simonjamesrowe.componenttest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ComponentTestApplication

fun main(args: Array<String>) {
	runApplication<ComponentTestApplication>(*args)
}
