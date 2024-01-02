package com.ozanarik.mvvmweatherapp.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.airbnb.lottie.Lottie


fun Context.toast(message:String, length: Int = Toast.LENGTH_LONG){

    Toast.makeText(this,message,length).show()
}

fun Fragment.toast(message:String,length: Int = Toast.LENGTH_LONG){
    requireContext().toast(message,length)
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