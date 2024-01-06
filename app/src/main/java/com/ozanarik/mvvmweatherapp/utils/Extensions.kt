package com.ozanarik.mvvmweatherapp.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar


fun Context.toast(message:String, length: Int = Toast.LENGTH_LONG){

    Toast.makeText(this,message,length).show()
}

fun Fragment.toast(message:String,length: Int = Toast.LENGTH_LONG){
    requireContext().toast(message,length)
}

fun Fragment.showSnackbar(message: String,actionText:String,actionCallback: (()-> Unit)?=null){
    view?.let {
        val snackBar = Snackbar.make(it,message,Snackbar.LENGTH_LONG)

        actionText.let { text->
            snackBar.setAction(text){
                actionCallback?.invoke()
            }
        }
        snackBar.show()

    }
}

fun View.showSnackbar(message: String, actionText:String, length:Int = Snackbar.LENGTH_LONG, actionCallback:(()->Unit )?=null){

    val snackBar = Snackbar.make(this,message,length)

    actionText.let { action->

        snackBar.setAction(action){
            actionCallback?.invoke()
    }
    }
    snackBar.show()
}


fun String.isSplittable(dataToCompare:String):Boolean{
    return this.split("\\s".toRegex()).contains(dataToCompare)
}


fun Double.kelvinToCelsius():Int{

    return this.minus(272.15).toInt()
}

fun View.makeVisible(){

    this.visibility = View.VISIBLE
}
fun View.makeInvisible(){

    this.visibility = View.INVISIBLE
}


fun String?.capitalizeWords():String{

    return this?.split("")?.joinToString(" ") { string->
        string.lowercase().replaceFirstChar { char->
            char.uppercaseChar()
        }
    }?:""





}