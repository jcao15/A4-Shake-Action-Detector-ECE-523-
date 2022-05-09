package com.example.a4_shake_and_step_detector

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs
import kotlin.math.sqrt


class FreeMode : AppCompatActivity() {

    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    private lateinit var sensorManager: SensorManager
    private lateinit var accelSensor: Sensor

    lateinit var tvCount: TextView
    private lateinit var btnStop: Button

//    private var alpha = 0.75f
//    private var accelGravity = floatArrayOf(0.0F,0.0F,0.0F)
//    private var xf = 0.0f
//    private var yf = 0.0f
//    private var zf = 0.0f
    private var movingAvg = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_free_mode)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        tvCount = findViewById(R.id.tvCount)
        btnStop = findViewById(R.id.btnStop)

        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

        //Reset count
        btnStop.setOnClickListener {
            tvCount.text = "0"
        }

    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        var slidingWindow = FloatArray(5)

        override fun onSensorChanged(event: SensorEvent) {

            // initialize acceleration in different directions
            val x = event.values[0] // X value
            val y = event.values[1] // Y value
            val z = event.values[2] // Z value

//            //High Pass Filter
//            accelGravity[0] = alpha * accelGravity[0] + (1 - alpha) * x
//            accelGravity[1] = alpha * accelGravity[1] + (1 - alpha) * y
//            accelGravity[2] = alpha * accelGravity[2] + (1 - alpha) * z
//
//            xf = x - accelGravity[0]
//            yf = y - accelGravity[1]
//            zf = z - accelGravity[2]

            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            //val delta: Float = currentAcceleration - lastAcceleration
            //acceleration = acceleration * 0.9f + delta
            acceleration = currentAcceleration - movingAvg

            addToWindow(acceleration)

            // adjust threshold here
            val threshold = 15.toFloat()
            if (abs(slidingWindow[4]) - abs(slidingWindow[3]) > threshold) {
                var count = tvCount.text.toString().toInt()
                count += 1
                tvCount.text = count.toString()
            }
        }

        fun movingAvgFilter(window: FloatArray): Float {

            var mean = 0.0f
            for (element in window) {
                mean += element
            }
            return mean / window.size
        }
        fun addToWindow(mag: Float) {
            for (i in 1 until slidingWindow.size) {
                slidingWindow[i - 1] = slidingWindow[i]
            }
            slidingWindow[slidingWindow.size - 1] = mag
            movingAvg = movingAvgFilter(slidingWindow)
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            sensorListener, sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)
    }


}