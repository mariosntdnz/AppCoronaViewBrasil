
package com.example.coronaview.utils
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import java.util.*

class CustomAlertDialog(
    context: Context?,
    private val alertDialog: AlertDialog)
    :  AlertDialog(context) {

    private var colorPositive = 0
    private var textSizePositive = 0
    private var colorNegative = 0
    private var textSizeNegative = 0

    var positiveClick : MutableLiveData<Boolean> = MutableLiveData()
    var negativeClick : MutableLiveData<Boolean> = MutableLiveData()

    fun customButtonPositive(colorResources: Int, textSize: Int): CustomAlertDialog {
        colorPositive = colorResources
        textSizePositive = textSize
        return this
    }

    fun customButtonNegative(colorResources: Int, textSize: Int): CustomAlertDialog {
        colorNegative = colorResources
        textSizeNegative = textSize
        return this
    }

    override fun show() {
        alertDialog.setOnShowListener(showListener())
        alertDialog.show()
    }

    private fun showListener(): OnShowListener {
        return OnShowListener { dialog: DialogInterface ->
            val positive = alertDialog.getButton(
                DialogInterface.BUTTON_POSITIVE
            )
            val positiveColor = Optional.of(colorPositive)
            positiveColor
                .ifPresent { c: Int? ->
                    positive.setTextColor(ContextCompat.getColor(context, c!!))
                    positive.textSize = textSizePositive.toFloat()
                }
            val negativeColor = Optional.of(colorNegative)
            negativeColor
                .ifPresent { c: Int? ->
                    val negative = alertDialog.getButton(
                        DialogInterface.BUTTON_NEGATIVE
                    )
                    negative.setTextColor(ContextCompat.getColor(context, c!!))
                    negative.textSize = textSizeNegative.toFloat()
                    negative.setOnClickListener {
                        negativeClick.value = true
                        alertDialog.dismiss()
                    }
                }

            positive.setOnClickListener{
                positiveClick.value = true
                alertDialog.dismiss()
            }
        }
    }


}