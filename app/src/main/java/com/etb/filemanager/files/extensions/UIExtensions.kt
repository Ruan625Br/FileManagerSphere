package com.etb.filemanager.files.extensions

import android.content.res.Resources
import android.graphics.Color
import android.view.View
import com.etb.filemanager.files.util.getColorByAttr
import com.etb.filemanager.settings.preference.Preferences
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

fun Int.toPixelFromDP(): Int {
    val density = Resources.getSystem().displayMetrics.density
    val pixelValue = (this * density).toInt()
    return pixelValue
}

fun View.applyBackgroundFromPreferences(
    cornerSize: Float? = null, cornerFamily: Int = Preferences.Interface.cornerFamily
) {
    val mCornerSize = cornerSize ?: if (Preferences.Interface.isEnabledRoundedCorners) 30f else 0f
    val context = this.context
    val colorPrimary = context.getColorByAttr(com.google.android.material.R.attr.colorPrimary)
    val showBackground = true

    val shapeAppearanceModel = ShapeAppearanceModel.builder()
   shapeAppearanceModel.applyCornerFamilyFromPreferences()
    val materialShapeDrawable = MaterialShapeDrawable(shapeAppearanceModel.build())

    if (showBackground) materialShapeDrawable.setTint(colorPrimary) else materialShapeDrawable.setTint(
        Color.TRANSPARENT
    )

    this.background = materialShapeDrawable
}


fun ShapeAppearanceModel.Builder.applyCornerFamilyFromPreferences(
    cornerFamily: Int = Preferences.Interface.cornerFamily, cornerSize: Float = 30f
) {
    val shapeModel = this
    val rounded = CornerFamily.ROUNDED
    val cut = CornerFamily.CUT

    when (cornerFamily) {
        CornerStyle.ROUNDED.cornerFamily -> {
            shapeModel.setAllCorners(rounded, cornerSize)
        }

        CornerStyle.CUT.cornerFamily -> {
            shapeModel.setAllCorners(cut, cornerSize)
        }

        CornerStyle.ROUNDED_TOP_RIGHT.cornerFamily -> {
            shapeModel.setTopRightCorner(rounded, cornerSize)
        }

        CornerStyle.ROUNDED_TOP_RIGHT_BOTTOM_LEFT.cornerFamily -> {
            shapeModel.setTopRightCorner(rounded, cornerSize)
            shapeModel.setBottomLeftCorner(rounded, cornerSize)
        }

        CornerStyle.CUT_TOP_RIGHT_BOTTOM_LEFT.cornerFamily -> {
            shapeModel.setTopRightCorner(cut, cornerSize)
            shapeModel.setBottomLeftCorner(cut, cornerSize)
        }

        CornerStyle.CUT_TOP_RIGHT.cornerFamily -> {
            shapeModel.setTopRightCorner(cut, cornerSize)
        }

        CornerStyle.CUT_TOP_LEFT.cornerFamily -> {
            shapeModel.setTopLeftCorner(cut, cornerSize)
        }

        CornerStyle.ROUNDED_TOP_LEFT.cornerFamily -> {
            shapeModel.setTopLeftCorner(rounded, cornerSize)
        }

        CornerStyle.CUT_TOP_RIGHT_BOTTOM.cornerFamily -> {
            shapeModel.setTopRightCorner(rounded, cornerSize)
            shapeModel.setBottomRightCorner(cut, cornerSize)
        }
    }
}
fun createShapeModelBasedOnCornerFamilyPreference(
    cornerFamily: Int = Preferences.Interface.cornerFamily, cornerSize: Float = 30f
): ShapeAppearanceModel {
    val shapeModel = ShapeAppearanceModel.builder()
    val rounded = CornerFamily.ROUNDED
    val cut = CornerFamily.CUT

    when (cornerFamily) {
        CornerStyle.ROUNDED.cornerFamily -> {
            shapeModel.setAllCorners(rounded, cornerSize)
        }

        CornerStyle.CUT.cornerFamily -> {
            shapeModel.setAllCorners(cut, cornerSize)
        }

        CornerStyle.ROUNDED_TOP_RIGHT.cornerFamily -> {
            shapeModel.setTopRightCorner(rounded, cornerSize)
        }

        CornerStyle.ROUNDED_TOP_RIGHT_BOTTOM_LEFT.cornerFamily -> {
            shapeModel.setTopRightCorner(rounded, cornerSize)
            shapeModel.setBottomLeftCorner(rounded, cornerSize)
        }

        CornerStyle.CUT_TOP_RIGHT_BOTTOM_LEFT.cornerFamily -> {
            shapeModel.setTopRightCorner(cut, cornerSize)
            shapeModel.setBottomLeftCorner(cut, cornerSize)
        }

        CornerStyle.CUT_TOP_RIGHT.cornerFamily -> {
            shapeModel.setTopRightCorner(cut, cornerSize)
        }

        CornerStyle.CUT_TOP_LEFT.cornerFamily -> {
            shapeModel.setTopLeftCorner(cut, cornerSize)
        }

        CornerStyle.ROUNDED_TOP_LEFT.cornerFamily -> {
            shapeModel.setTopLeftCorner(rounded, cornerSize)
        }

        CornerStyle.CUT_TOP_RIGHT_BOTTOM.cornerFamily -> {
            shapeModel.setTopRightCorner(rounded, cornerSize)
            shapeModel.setBottomRightCorner(cut, cornerSize)
        }
    }
    return shapeModel.build()
}

enum class CornerStyle(val cornerFamily: Int) {
    ROUNDED(0), CUT(1), ROUNDED_TOP_RIGHT(2), ROUNDED_TOP_RIGHT_BOTTOM_LEFT(3), CUT_TOP_RIGHT_BOTTOM_LEFT(
        4
    ),
    CUT_TOP_RIGHT(5), CUT_TOP_LEFT(6), ROUNDED_TOP_LEFT(7), CUT_TOP_RIGHT_BOTTOM(8)
}
