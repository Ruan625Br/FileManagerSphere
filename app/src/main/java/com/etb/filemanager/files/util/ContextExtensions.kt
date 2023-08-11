package com.etb.filemanager.files.util


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.FileProvider
import com.etb.filemanager.R
import com.etb.filemanager.files.provider.archive.common.mime.compat.obtainStyledAttributesCompat
import com.etb.filemanager.files.provider.archive.common.mime.compat.use
import com.etb.filemanager.manager.media.model.Media
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

val Context.activity: Activity?
    get() {
        var context = this
        while (true) {
            when (context) {
                is Activity -> return context
                is ContextWrapper -> context = context.baseContext
                else -> return null
            }
        }
    }

fun Context.getAnimation(@AnimRes id: Int): Animation = AnimationUtils.loadAnimation(this, id)

fun Context.getBoolean(@BoolRes id: Int) = resources.getBoolean(id)

fun Context.getDimension(@DimenRes id: Int) = resources.getDimension(id)

fun Context.getDimensionPixelOffset(@DimenRes id: Int) = resources.getDimensionPixelOffset(id)

fun Context.getDimensionPixelSize(@DimenRes id: Int) = resources.getDimensionPixelSize(id)

fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)

fun Context.getInterpolator(@InterpolatorRes id: Int): Interpolator =
    AnimationUtils.loadInterpolator(this, id)

fun Context.getQuantityString(@PluralsRes id: Int, quantity: Int): String =
    resources.getQuantityString(id, quantity)

fun Context.getQuantityString(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any?): String =
    resources.getQuantityString(id, quantity, *formatArgs)

fun Context.getQuantityText(@PluralsRes id: Int, quantity: Int): CharSequence =
    resources.getQuantityText(id, quantity)

fun Context.getStringArray(@ArrayRes id: Int): Array<String> = resources.getStringArray(id)

fun Context.getTextArray(@ArrayRes id: Int): Array<CharSequence> = resources.getTextArray(id)


val Context.displayWidth: Int
    get() = resources.displayMetrics.widthPixels

val Context.displayHeight: Int
    get() = resources.displayMetrics.heightPixels

@Dimension
fun Context.dpToDimension(@Dimension(unit = Dimension.DP) dp: Float): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

@Dimension
fun Context.dpToDimension(@Dimension(unit = Dimension.DP) dp: Int) = dpToDimension(dp.toFloat())

@Dimension
fun Context.dpToDimensionPixelOffset(@Dimension(unit = Dimension.DP) dp: Float): Int =
    dpToDimension(dp).toInt()

@Dimension
fun Context.dpToDimensionPixelOffset(@Dimension(unit = Dimension.DP) dp: Int) =
    dpToDimensionPixelOffset(dp.toFloat())

@Dimension
fun Context.dpToDimensionPixelSize(@Dimension(unit = Dimension.DP) dp: Float): Int {
    val value = dpToDimension(dp)
    val size = (if (value >= 0) value + 0.5f else value - 0.5f).toInt()
    return when {
        size != 0 -> size
        value == 0f -> 0
        value > 0 -> 1
        else -> -1
    }
}

@Dimension
fun Context.dpToDimensionPixelSize(@Dimension(unit = Dimension.DP) dp: Int) =
    dpToDimensionPixelSize(dp.toFloat())

fun Context.hasSwDp(@Dimension(unit = Dimension.DP) dp: Int): Boolean =
    resources.configuration.smallestScreenWidthDp >= dp

val Context.hasSw600Dp: Boolean
    get() = hasSwDp(600)

fun Context.hasWDp(@Dimension(unit = Dimension.DP) dp: Int): Boolean =
    resources.configuration.screenWidthDp >= dp

val Context.hasW600Dp: Boolean
    get() = hasWDp(600)

val Context.hasW960Dp: Boolean
    get() = hasWDp(960)


val Context.isOrientationLandscape: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

val Context.isOrientationPortrait: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

val Context.shortAnimTime: Int
    get() = getInteger(android.R.integer.config_shortAnimTime)

val Context.mediumAnimTime: Int
    get() = getInteger(android.R.integer.config_mediumAnimTime)

val Context.longAnimTime: Int
    get() = getInteger(android.R.integer.config_longAnimTime)

fun Context.withheme(@StyleRes themeRes: Int): Context =
    if (themeRes != 0) ContextThemeWrapper(this, themeRes) else this

@ColorInt
fun Context.getColorByAttr(@AttrRes attr: Int): Int =
    getColorStateListByAttr(attr).defaultColor

@SuppressLint("RestrictedApi")
fun Context.getColorStateListByAttr(@AttrRes attr: Int): ColorStateList =
    obtainStyledAttributesCompat(attrs = intArrayOf(attr)).use { it.getColorStateList(0) }

fun Context.getColorFromRes(atrr: Int): Int {
    val attrs = intArrayOf(atrr)
    val typedArray = this.obtainStyledAttributes(attrs)
    val tint = typedArray.getColor(0, 0)
    typedArray.recycle()
    return tint
}

fun Activity.toggleOrientation() {
    requestedOrientation =
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED ||
            requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}

fun Context.shareMedia(media: Media) {
    val uri = media.uri
    val filePath = uri.path

    FileUtil().shareFile(filePath!!, this)
}



suspend fun Context.launchEditIntent(media: Media) =
    withContext(Dispatchers.Default) {
        val mimeType = FileUtil().getMimeType(null, media.uri.path)
        val intent = Intent(Intent.ACTION_EDIT).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            setDataAndType(media.uri, mimeType)
            putExtra("mimeType", mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.edit)))
    }

@SuppressLint("QueryPermissionsNeeded")
fun Context.actionEdit(path: String) {
    val file = File(path)
    val uri = FileProvider.getUriForFile(this, this.packageName + ".fileprovider", file)
    val mimeType = FileUtil().getMimeType(uri, null)

    val intent = Intent(Intent.ACTION_EDIT).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    this.startActivity(intent)

}
