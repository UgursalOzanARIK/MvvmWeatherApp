package com.ozanarik.mvvmweatherapp.ui.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ozanarik.mvvmweatherapp.R
import com.ozanarik.mvvmweatherapp.databinding.FragmentSettingsBinding
import com.ozanarik.mvvmweatherapp.ui.viewmodel.WeatherViewModel
import com.ozanarik.mvvmweatherapp.utils.makeInvisible
import com.ozanarik.mvvmweatherapp.utils.makeVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var weatherViewModel: WeatherViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment



        binding = FragmentSettingsBinding.inflate(inflater,container,false)
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        getDarkMode()



        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigateBackToWeatherForecastFragment()




        binding.apply {

            themeSwitch.setOnCheckedChangeListener { _, isChecked ->

                when(isChecked){
                    true->{
                        weatherViewModel.setDarkMode(true)
                        binding.lottieAnim.makeVisible()
                    }
                    false->{
                        weatherViewModel.setDarkMode(false)
                        binding.lottieAnim.makeVisible()
                    }
                }
            }
        }

    }

    private fun getDarkMode(){

            lottieAnimHandler()

    }

    @SuppressLint("SetTextI18n")
    private fun lottieAnimHandler(){

        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.getDarkMode().collect{isDarkMode->
                when(isDarkMode){
                    true->{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        binding.themeSwitch.isChecked = true
                        binding.lottieAnim.setAnimation(R.raw.night)
                        binding.tvSwitch.text = "Dark Mode"
                        binding.lottieAnim.playAnimation()
                        binding.lottieAnim.setMaxProgress(0.4f)
                        delay(500L)
                        val anim = ObjectAnimator.ofFloat(binding.lottieAnim,"alpha",1.0f,0.0f)
                        val scaleX = ObjectAnimator.ofFloat(binding.lottieAnim,"scaleX",1.0f,0.0f)
                        val scaleY = ObjectAnimator.ofFloat(binding.lottieAnim,"scaleY",1.0f,0.0f)
                        val multiAnim = AnimatorSet().apply {
                            playTogether(anim,scaleX,scaleY)
                            duration = 800L
                        }
                        multiAnim.start()
                    }
                    false->{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        binding.themeSwitch.isChecked = false
                        binding.tvSwitch.text = "Light Mode"
                        binding.lottieAnim.setAnimation(R.raw.night)
                        binding.lottieAnim.playAnimation()
                        binding.lottieAnim.setMinProgress(0.4f)
                        delay(500L)
                        val anim = ObjectAnimator.ofFloat(binding.lottieAnim,"alpha",1.0f,0.0f)
                        val scaleX = ObjectAnimator.ofFloat(binding.lottieAnim,"scaleX",1.0f,0.0f)
                        val scaleY = ObjectAnimator.ofFloat(binding.lottieAnim,"scaleY",1.0f,0.0f)
                        val multiAnim = AnimatorSet().apply {
                            playTogether(anim,scaleX,scaleY)
                            duration = 800L
                        }
                        multiAnim.start()

                    }
                }
            }
        }
    }



    private fun navigateBackToWeatherForecastFragment(){

        binding.imageViewBackButton.setOnClickListener {

            findNavController().navigate(R.id.action_settingsFragment_to_weatherForecastFragment)

        }

    }

}