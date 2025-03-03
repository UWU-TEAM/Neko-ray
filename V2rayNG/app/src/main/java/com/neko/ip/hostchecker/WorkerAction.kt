package com.neko.ip.hostchecker

interface WorkerAction {
    fun runFirst()

    fun runLast()
}
