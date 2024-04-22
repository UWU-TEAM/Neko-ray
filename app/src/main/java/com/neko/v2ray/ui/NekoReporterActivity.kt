package com.neko.v2ray.ui

import android.os.Bundle
import com.neko.reportissue.IssueReporterActivity
import com.neko.reportissue.model.github.GithubTarget

class NekoReporterActivity : IssueReporterActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMinimumDescriptionLength(10)
        setGuestEmailRequired(true)
    }

    override fun getGuestToken(): String {
        return "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"
    }

    override fun getTarget(): GithubTarget {
        return GithubTarget("Blawuken", "Neko_v2rayNG")
    }
}
