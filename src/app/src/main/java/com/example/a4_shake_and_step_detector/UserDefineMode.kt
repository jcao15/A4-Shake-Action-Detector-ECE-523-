package com.example.a4_shake_and_step_detector

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs
import kotlin.math.sqrt


class UserDefineMode : AppCompatActivity() {

    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    private lateinit var sensorManager2: SensorManager
    private lateinit var accelSensor2: Sensor
    lateinit var mediaPlayer: MediaPlayer

    lateinit var tvCount2: TextView
    lateinit var etTargetValue: EditText
    private lateinit var btnStop2: Button
    private lateinit var ringtoneT: TextView


    //    private var alpha = 0.75f
//    private var accelGravity = FloatArray(3)
//    private var xf = 0.0f
//    private var yf = 0.0f
//    private var zf = 0.0f
    private var movingAvg = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_define_mode)

        // CREATE SENSOR MANAGER
        sensorManager2 = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // CREATE ACCELERATION SENSOR
        accelSensor2 = sensorManager2.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        tvCount2 = findViewById(R.id.tvCount2)
        etTargetValue = findViewById(R.id.etTargetValue)
        btnStop2 = findViewById(R.id.btnStop2)
        ringtoneT = findViewById(R.id.ringtoneTv)

        mediaPlayer = MediaPlayer.create(this, R.raw.ringtone)

        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

        //Reset count
        btnStop2.setOnClickListener {
            tvCount2.text = "0"
        }

    }

    private val sensorListener2: SensorEventListener = object : SensorEventListener {
        var slidingWindow = FloatArray(5)

        override fun onSensorChanged(event: SensorEvent) {
            Log.i("SensorClassActivity", "Got Sensor update")
            if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

            if (etTargetValue.text.toString() == "") return


            // initialize acceleration in different directions
            val x = event.values[0] // X value
            val y = event.values[1] // Y value
            val z = event.values[2] // Z value

            //High Pass Filter
//            accelGravity[0] = alpha * accelGravity[0] + (1 - alpha) * x
//            accelGravity[1] = alpha * accelGravity[1] + (1 - alpha) * y
//            accelGravity[2] = alpha * accelGravity[2] + (1 - alpha) * z
//
//            xf = x - accelGravity[0]
//            yf = y - accelGravity[1]
//            zf = z - accelGravity[2]

            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
//            val delta: Float = currentAcceleration - lastAcceleration
//            acceleration = acceleration * 0.9f + delta
            Log.i("MovingAvg", movingAvg.toString())
            acceleration = currentAcceleration - movingAvg
            addToWindow(acceleration)

            // adjust threshold here
            val threshold = 15.toFloat()
            if (abs(slidingWindow[4]) - abs(slidingWindow[3]) > threshold) {
                var count = tvCount2.text.toString().toInt()
                count += 1
                tvCount2.text = count.toString()
            }

            // get max number
            val nMax = etTargetValue.text.toString().toInt()
            //if the count amount reach the max number, it plays a ringtone
            if (tvCount2.text.toString().toInt() == nMax) {
                Log.i("SensorClassActivity", "play ringtone!!!!!!!!")

                mediaPlayer.start()
                ringtoneT.visibility = VISIBLE

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
        // REGISTERING OUR SENSOR
        sensorManager2.registerListener(
            sensorListener2, sensorManager2.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager2.unregisterListener(sensorListener2)
    }


}