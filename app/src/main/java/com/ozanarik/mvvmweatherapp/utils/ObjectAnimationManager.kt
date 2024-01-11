package com.ozanarik.mvvmweatherapp.utils

import android.animation.ObjectAnimator
import android.view.View
import com.ozanarik.mvvmweatherapp.utils.Constants.Companion.OBJECT_ANIMATION_DURATION

class ObjectAnimationManager {



    fun animateObject(view: View, propertyName:String, valueFrom:Float,valueTo:Float){


        ObjectAnimator.ofFloat(view,propertyName,valueTo,valueFrom).apply {
            duration = OBJECT_ANIMATION_DURATION
        }.start()


    }




}