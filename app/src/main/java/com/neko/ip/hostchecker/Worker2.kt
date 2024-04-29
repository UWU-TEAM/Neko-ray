package com.neko.ip.hostchecker

import android.content.Context
import android.os.AsyncTask

class Worker2(private val workerAction: WorkerAction, context: Context) : AsyncTask<Void, Int, String>() {

    override fun doInBackground(vararg voids: Void?): String? {
        workerAction.runFirst()
        return null
    }

    override fun onPreExecute() {
    }

    override fun onProgressUpdate(vararg values: Int?) {
    }

    override fun onPostExecute(result: String?) {
        workerAction.runLast()
    }
}
