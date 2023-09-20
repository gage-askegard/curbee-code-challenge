package com.askegard.curbee.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DiffToolApplication

fun main(args: Array<String>) {
    runApplication<DiffToolApplication>(*args)
}