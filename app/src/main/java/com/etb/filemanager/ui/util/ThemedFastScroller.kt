
/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - ThemedFastScroller.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.ui.util

import android.view.ViewGroup
import me.zhanghai.android.fastscroll.FastScroller
import me.zhanghai.android.fastscroll.FastScrollerBuilder

object ThemedFastScroller {
    fun create(view: ViewGroup): FastScroller = FastScrollerBuilder(view).useMd2Style().build()
}