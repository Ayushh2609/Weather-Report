package com.example.weatherreport

import android.graphics.Color
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherreport.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchWeatherData("haridwar")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    Toast.makeText(this@MainActivity, "Searching: $p0", Toast.LENGTH_SHORT).show()
                    fetchWeatherData(p0)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName : String) {
        val retroFit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retroFit.getWeatherData(cityName , "35c7535df7dc23e1ac7b734f327610d2" ,"metric")

        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity.toString()
                    val windSpeed = responseBody.wind.speed.toString()
                    val seaLevel = responseBody.main.pressure.toString()
                    val maxTemp = responseBody.main.temp_max.toString()
                    val minTemp = responseBody.main.temp_min.toString()
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"

                    val sunRiseTimeStamp = responseBody.sys.sunrise * 1000L
                    val dateRise = Date(sunRiseTimeStamp)
                    val formatR = SimpleDateFormat("hh:mm a" , Locale.getDefault())
                    val readableSunRise = formatR.format(dateRise)

                    val sunSetTimeStamp = responseBody.sys.sunset * 1000L
                    val dateUp = Date(sunSetTimeStamp)
                    val formatS = SimpleDateFormat("hh:mm a" , Locale.getDefault())
                    val readableSunSet = formatS.format(dateUp)

                    binding.temperature.text = "${temperature}°C"
                    binding.humidity.text = humidity
                    binding.sunRise.text = readableSunRise
                    binding.sunSet.text = readableSunSet
                    binding.windSpeed.text = windSpeed
                    binding.seaLevel.text = seaLevel
                    binding.condition.text = condition
                    binding.maxTemp.text = "MAX: $maxTemp"
                    binding.minTemp.text = "MIN: $minTemp"
                    binding.condition.text = condition
                    binding.conditionWeather.text = condition

                    binding.day.text = day(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = cityName


                    changeImageAccordingToWeather(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }
        })
    }

    private fun changeImageAccordingToWeather(condition: String) {
        when(condition){
            "Clear Sky" , "Sunny" , "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.back_sun)
                binding.lottieAnimationView4.setAnimation(R.raw.sun)
            }

            "Partly Clouds" , "Clouds" , "Overcast" , "Mist" , "Foggy"-> {
                binding.root.setBackgroundResource(R.drawable.back_clouds)
                binding.lottieAnimationView4.setAnimation(R.raw.clouds)
            }

            "Light Rain" , "Drizzle" , "Moderate Rain" , "Showers" , "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.back_rain)
                binding.lottieAnimationView4.setAnimation(R.raw.rain)
            }

            "Light Snow" ,"Moderate Snow" , "Heavy Snow" , "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.back_snow)
                binding.lottieAnimationView4.setAnimation(R.raw.snow)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.back_sun)
                binding.lottieAnimationView4.setAnimation(R.raw.sun)
            }
        }
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy" , Locale.getDefault())
        return sdf.format(Date())
    }

    private fun day(currentTimeMillis: Long): String? {
        val sdf = SimpleDateFormat("EEEE" , Locale.getDefault())
        return sdf.format(Date())
    }
}