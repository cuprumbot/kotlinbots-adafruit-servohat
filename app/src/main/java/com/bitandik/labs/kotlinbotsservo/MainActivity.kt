package com.bitandik.labs.kotlinbotsservo

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.zugaldia.robocar.hardware.adafruit2348.AdafruitPwm
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

const val I2C_DEVICE_NAME = "I2C1"

const val RIGHT_ADDRESS = 0x40
const val LEFT_ADDRESS = 0x41

// Original
// const val MIN_PULSE_MS = 1.0
// Raspi
const val MIN_PULSE_MS = 0.5
const val MAX_PULSE_MS = 2.0
const val MIN_ANGLE_DEG = 0.0
const val MAX_ANGLE_DEG = 180.0
const val MIN_CHANNEL = 0
const val MAX_CHANNEL = 15

const val TURN_DELAY = 1000L
const val WALK_DELAY = 1000L

const val TESTING = true

class MainActivity : Activity() {
    private var rightServoHat: ServoHat = ServoHat(RIGHT_ADDRESS)
    private var leftServoHat: ServoHat = ServoHat(LEFT_ADDRESS)
    private var twoHats: ServoDoubleHat = ServoDoubleHat(rightServoHat, leftServoHat)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launch {
            delay(1000L)
            moveServo()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rightServoHat.close()
        leftServoHat.close()
    }

    private suspend fun moveServo(){

        if (TESTING) {
            twoHats.test()
            delay(1000)
        } else {
            twoHats.forward()
            delay(WALK_DELAY)
            twoHats.forward()
            delay(WALK_DELAY)
            twoHats.forward()
            delay(WALK_DELAY)
            twoHats.forward()
            delay(WALK_DELAY)
            twoHats.turnCounterClockwise()
            delay(TURN_DELAY)
            twoHats.turnCounterClockwise()
            delay(TURN_DELAY)
            twoHats.turnCounterClockwise()
            delay(TURN_DELAY)
            twoHats.turnCounterClockwise()
            delay(TURN_DELAY)
            twoHats.turnCounterClockwise()
            delay(TURN_DELAY)
            twoHats.turnCounterClockwise()
            delay(TURN_DELAY)
            twoHats.turnCounterClockwise()
            delay(TURN_DELAY)
            twoHats.turnCounterClockwise()
            delay(TURN_DELAY)
            twoHats.turnCounterClockwise()
            delay(TURN_DELAY)
            twoHats.turnCounterClockwise()
            delay(TURN_DELAY)
        }

        moveServo()
    }
}

class ServoHat(address:Int) {
    //private val adafruitPWM: AdafruitPwm = AdafruitPwm(I2C_DEVICE_NAME, SERVO_HAT_I2C_ADDRESS)
    private val adafruitPWM: AdafruitPwm = AdafruitPwm(I2C_DEVICE_NAME, address)
    /*
     https://github.com/adafruit/Adafruit_Python_PCA9685/blob/master/examples/simpletest.py
     1,000,000 us per second
     40 Hz
     12 bits of resolution
    */
    // Original
    //private val pulseLength = (1000000 / 40) / 4096
    // Raspi
    private val pulseLength = (1000000 / 50) / 4096
    private val servos: HashMap<Int, Double> = HashMap()

    init {
        // Raspi
        adafruitPWM.setPwmFreq(50)
        for (channel in 1..15) {
            servos[channel] = 0.0
        }
    }

    fun setAngle(channel: Int, angle: Double) {
        if (angle in MIN_ANGLE_DEG..MAX_ANGLE_DEG && channel in MIN_CHANNEL..MAX_CHANNEL) {
            val normalizedAngleRatio = (angle - MIN_ANGLE_DEG) / (MAX_ANGLE_DEG - MIN_ANGLE_DEG)
            val pulse = MIN_PULSE_MS + (MAX_PULSE_MS - MIN_PULSE_MS) * normalizedAngleRatio
            val dutyCycle = (pulse * 1000 / pulseLength).toInt()
            servos[channel] = angle
            adafruitPWM.setPwm(channel, 0, dutyCycle)
            //Log.i("TAG50HZ", "channel: " + channel + " angle: " + angle + " dutyCycle: " + dutyCycle);
        }
    }

    fun getAngle(channel: Int): Double {
        var angle = 0.0
        if (channel in MIN_CHANNEL..MAX_CHANNEL) {
            servos[channel]?.let{
                angle = it
            }
        }
        return angle
    }

    fun close() {
        adafruitPWM.close()
    }
}
