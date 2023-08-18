
/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.etb.filemanager.ui.util

import android.view.ViewGroup
import me.zhanghai.android.fastscroll.FastScroller
import me.zhanghai.android.fastscroll.FastScrollerBuilder

object ThemedFastScroller {
    fun create(view: ViewGroup): FastScroller = FastScrollerBuilder(view).useMd2Style().build()
}