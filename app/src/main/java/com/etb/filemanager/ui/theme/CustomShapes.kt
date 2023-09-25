/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - CustomShapes.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import com.etb.filemanager.files.extensions.getShapeFromPreferences


@Immutable
class CustomShapes(
    val cutLeftMedium: CornerBasedShape = CustomShapesDefault.CutLeftMedium,
    val cutRightMedium: CornerBasedShape = CustomShapesDefault.CutRightMedium,
    val shapeFromPreferences: CornerBasedShape = CustomShapesDefault.ShapeFromPreferences,
    val rounded: CustomShapesDefault.Rounded = CustomShapesDefault.Rounded,
) {
    fun copy(
        cutLeftMedium: CornerBasedShape = this.cutLeftMedium,
        cutRightMedium: CornerBasedShape = this.cutRightMedium,
        shapeFromPreferences: CornerBasedShape = this.shapeFromPreferences,
        rounded: CustomShapesDefault.Rounded = this.rounded
    ): CustomShapes = CustomShapes(
        cutLeftMedium = cutLeftMedium,
        cutRightMedium = cutRightMedium,
        shapeFromPreferences = shapeFromPreferences,
        rounded = rounded
    )

}

object CustomShapesDefault {

    val CutRightMedium = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
    val CutLeftMedium = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
    val ShapeFromPreferences = getShapeFromPreferences()

    object Rounded {
        val cornerSize = 20.dp
        val Medium =  RoundedCornerShape(cornerSize)
    }
}