package com.neko.aboutlibraries.util

import com.neko.aboutlibraries.entity.Library
import com.neko.aboutlibraries.entity.License

internal val Library.license: License?
    get() = licenses.firstOrNull()

val Library.author: String
    get() = developers.takeIf { it.isNotEmpty() }?.map { it.name }?.joinToString(", ") ?: organization?.name ?: ""

internal val License.htmlReadyLicenseContent: String?
    get() = licenseContent?.replace("\n", "<br />")