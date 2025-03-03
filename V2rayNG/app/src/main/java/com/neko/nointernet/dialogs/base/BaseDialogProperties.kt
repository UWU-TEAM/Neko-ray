package com.neko.nointernet.dialogs.base

import com.neko.nointernet.callbacks.ConnectionCallback

abstract class BaseDialogProperties(
    var cancelable: Boolean = false,
    var connectionCallback: ConnectionCallback? = null
)