package com.treel.androidsdkdemo.utility

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.treel.androidsdkdemo.R

/**
 * Singleton class responisble for displaying Dialogs and Snackbar across the app
 * @author Nitin Karande
 */

object DialogUtils {
    var isShowing: Boolean = false
    var alertDialog: Dialog? = null
    fun showYesNoAlert(
        context: Context?,
        title: String,
        message: String,
        listner: OnDialogYesNoActionListener?
    ) {
        //if (isShowing) return
        if (isShowing) {
            alertDialog?.dismiss()
        }
        showYesNoAlert(
            context,
            android.R.string.ok,
            android.R.string.cancel,
            title,
            message,
            listner
        )

    }

    fun showYesNoAlert(
        context: Context?,
        okButtonText: Int? = null,
        cancelButtonText: Int? = null,
        title: String,
        message: String,
        listner: OnDialogYesNoActionListener?
    ) {
        // if (isShowing) return
        if (isShowing) {
            alertDialog?.dismiss()
        }

        if (context != null && !(context as Activity).isFinishing) {
            val builder = AlertDialog.Builder(context)

            //builder.setTitle(Html.fromHtml("<font color='#000000'>" + title + "</font>")).setMessage(message);
            builder.setTitle(title).setMessage(message)

            val accept: Int = okButtonText ?: android.R.string.ok
            val decline: Int = cancelButtonText ?: android.R.string.cancel


            // Add the buttons
            builder.setPositiveButton(accept) { dialog, _ ->
                isShowing = false
                dialog.dismiss()
                listner?.onYesClick()
            }
            builder.setNegativeButton(decline) { dialog, _ ->
                isShowing = false
                dialog.dismiss()
                listner?.onNoClick()
            }

            // Create the AlertDialog
            alertDialog = builder.create()
            alertDialog?.setCancelable(false)
            alertDialog?.setOnDismissListener {
                isShowing = false
            }
            alertDialog?.show()
            isShowing = true
        }

    }

    /***********************************************************************
     * @ Purpose : This methods are used to show Alert with Ok button
     */
    fun showOkAlert(context: Context, title: String, message: String, func: (() -> Unit)? = null) {
        if (isShowing) {
            alertDialog?.dismiss()
        }
        //var alertDialog: Dialog?

        /*if(alertDialog != null)
        {
			if(alertDialog.isShowing())
			{
				alertDialog.dismiss();
			}
			alertDialog = null;
		}*/

        val builder = AlertDialog.Builder(context)

        builder.setTitle(title).setMessage(message)


        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
            isShowing = false
            func?.invoke()
        }
        alertDialog = builder.create()
        alertDialog?.setCancelable(false)
        alertDialog?.setOnDismissListener {
            isShowing = false
        }
        isShowing = true
        alertDialog?.show()
    }
    fun showSnackBarMsg(context: Context, parentView: View?, string: String?, duration: Int) {
        val snackbar = Snackbar.make(parentView!!, string!!, duration)
        customizeSnackbar(context, snackbar)

        snackbar.show()
    }

    fun showSnackBarMsg(context: Context, parentView: View, string: String, duration: Int, actionMsg: String?, actionClickListener: (Any) -> Unit) {
        val snackbar = Snackbar.make(parentView, string, duration)
        snackbar.setAction(actionMsg, actionClickListener)
        customizeSnackbar(context, snackbar)
        snackbar.show()
    }

    private fun customizeSnackbar(context: Context, snackbar: Snackbar) {
        snackbar.setActionTextColor(Color.WHITE)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_200))
        //TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);

    }

    interface OnDialogYesNoActionListener {
        fun onYesClick()

        fun onNoClick()
    }

    interface OnDialogOkActionListener {
        fun onOkClick()
    }

    interface OnInputDialogYesNoActionListener {
        fun onYesClick(input: String)

        fun onNoClick()
    }

}
