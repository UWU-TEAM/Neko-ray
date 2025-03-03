package com.neko.ip.hostchecker

import android.os.AsyncTask

class Worker(private val workerAction: WorkerAction) : AsyncTask<Void, Int, String>() {

    override fun doInBackground(vararg voids: Void?): String? {
        workerAction.runFirst()
        return null
    }

    override fun onPostExecute(result: String?) {
        workerAction.runLast()
    }
}
