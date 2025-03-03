package com.neko.themeengine

import android.content.Context
import android.content.DialogInterface.BUTTON_NEUTRAL
import android.content.DialogInterface.BUTTON_POSITIVE
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.neko.databinding.RecyclerviewBinding

/**
 * Builder class to create a Theme Chooser Dialog
 * @param context Context to use
 * @return ThemeChooserDialogBuilder
 * @author Prathamesh M
 */
class ThemeChooserDialogBuilder(private val context: Context) {

    private lateinit var builder: MaterialAlertDialogBuilder

    private lateinit var colorAdapter: ColorAdapter
    private val themes = Theme.values()

    init {
        createDialog()
    }

    private fun createDialog() {
        val binding = RecyclerviewBinding.inflate(LayoutInflater.from(context))

        val themeEngine = ThemeEngine.getInstance(context)
        val colorArray = themes.map { it.primaryColor }
        colorAdapter = ColorAdapter(colorArray)
        colorAdapter.setCheckedPosition(themeEngine.staticTheme)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 4)
            adapter = colorAdapter
        }
        builder = MaterialAlertDialogBuilder(context)
            .setView(binding.root)
    }

    /**
     * Set title of the Dialog using the given string resource id
     * @param res id of string resource
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun setTitle(@StringRes res: Int): ThemeChooserDialogBuilder {
        builder.setTitle(res)
        return this
    }

    /**
     * Set icon for the Dialog using the given drawable resource id
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun setIcon(@DrawableRes iconId: Int): ThemeChooserDialogBuilder {
        builder.setIcon(iconId)
        return this
    }

    /**
     * Set positive button text and a listener to be invoked when the positive button of the dialog is pressed
     * @param text The text to display in the positive button
     * @param listener The OnClickListener to use when the button is clicked.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun setPositiveButton(text: String, listener: OnClickListener): ThemeChooserDialogBuilder {
        builder.setPositiveButton(text) { _, which ->
            if (which == BUTTON_POSITIVE) {
                val checkedPosition = colorAdapter.checkedPosition
                listener.onClick(checkedPosition, themes[checkedPosition])
            }
        }
        return this
    }

    /**
     * Set positive button text and a listener to be invoked when the positive button of the dialog is pressed
     * @param res id of string resource
     * @param listener The OnClickListener to use when the button is clicked.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun setPositiveButton(
        @StringRes res: Int,
        listener: OnClickListener
    ): ThemeChooserDialogBuilder {
        setPositiveButton(context.getString(res), listener)
        return this
    }

    /**
     * Set negative button tex
     * @param text The text to display in the positive button
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun setNegativeButton(text: String): ThemeChooserDialogBuilder {
        builder.setNegativeButton(text, null)
        return this
    }

    /**
     * Set negative button text
     * @param res id of string resource
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun setNegativeButton(@StringRes res: Int): ThemeChooserDialogBuilder {
        builder.setNegativeButton(res, null)
        return this
    }

    /**
     * Set neutral button text and a listener to be invoked when the neutral button of the dialog is pressed
     * @param text The text to display in the neutral button
     * @param listener The OnClickListener to use when the button is clicked.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun setNeutralButton(text: String, listener: OnClickListener): ThemeChooserDialogBuilder {
        builder.setNeutralButton(text) { _, which ->
            if (which == BUTTON_NEUTRAL) {
                val checkedPosition = colorAdapter.checkedPosition
                val theme = themes[checkedPosition]
                listener.onClick(checkedPosition, theme)
            }
        }
        return this
    }

    /**
     * Set neutral button text and a listener to be invoked when the neutral button of the dialog is pressed
     * @param res id of string resource
     * @param listener The OnClickListener to use when the button is clicked.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun setNeutralButton(
        @StringRes res: Int,
        listener: OnClickListener
    ): ThemeChooserDialogBuilder {
        setNeutralButton(context.getString(res), listener)
        return this
    }

    /**
     * Creates and return an {@link AlertDialog}
     */
    fun create(): AlertDialog {
        return builder.create()
    }

    fun interface OnClickListener {
        fun onClick(position: Int, theme: Theme)
    }
}