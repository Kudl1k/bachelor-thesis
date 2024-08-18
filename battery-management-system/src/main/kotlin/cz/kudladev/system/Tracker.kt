package cz.kudladev.system

import kotlinx.coroutines.Job


var isRunning = false
var job: Job? = null

suspend fun run() {
    while (isRunning) {
        println("Hello, World!")
        Thread.sleep(1000)
    }
}