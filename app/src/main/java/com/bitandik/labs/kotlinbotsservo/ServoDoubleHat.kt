package com.bitandik.labs.kotlinbotsservo

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import android.util.Log

private const val HORIZONTAL    = 0
private const val VERTICAL      = 1
private const val KNEE          = 2

private const val RIGHT_FRONT   = 0
private const val RIGHT_MID     = 1
private const val RIGHT_BACK    = 2
private const val LEFT_FRONT    = 3
private const val LEFT_MID      = 4
private const val LEFT_BACK     = 5

private const val VERTICAL_RIGHT_RISE       = 30.0
private const val VERTICAL_LEFT_RISE        = -30.0
private const val VERTICAL_RETURN_TO_BASE   = 0.0

private const val HORIZONTAL_TURN_CLOCKWISE = 30.0
private const val HORIZONTAL_TURN_COUNTERCW = -30.0
private const val HORIZONTAL_RIGHT_FORWARD  = 15.0
private const val HORIZONTAL_LEFT_FORWARD   = -15.0
private const val HORIZONTAL_RIGHT_AGGRO    = 45.0
private const val HORIZONTAL_LEFT_AGGRO     = -45.0
private const val HORIZONTAL_RETURN_TO_BASE = 0.0

private const val KNEE_BASE = 90.0
private const val DELAY_TURN            = 125
private const val DELAY_FORWARD         = 125

class ServoDoubleHat (rHat: ServoHat, lHat: ServoHat) {
    private var rightHat = rHat
    private var leftHat = lHat

    private var legs = arrayOf  (
                                    arrayOf(0, 1, 2),   // RIGHT FRONT
                                    arrayOf(4, 5, 6),   // RIGHT MID
                                    arrayOf(8, 9, 10),  // RIGHT BACK
                                    arrayOf(0, 1, 2),   // LEFT FRONT
                                    arrayOf(4, 5, 6),   // LEFT MID
                                    arrayOf(8, 9, 10)   // LEFT BACK
                                )
    private var horizontalBase = arrayOf(
                                            85.0-10.0,  // RIGHT FRONT
                                            90.0+15.0,  // RIGHT MID
                                            110.0+10.0, // RIGHT BACK
                                            95.0,       // LEFT FRONT
                                            90.0+10.0,  // LEFT MID
                                            70.0        // LEFT BACK
                                        )
    private var verticalBase = arrayOf(
                                            70.0+5.0,   // RIGHT FRONT
                                            70.0-5.0,   // RIGHT MID - REPLACEMENT SERVO
                                            70.0+5.0,   // RIGHT BACK
                                            110.0+10.0, // LEFT FRONT
                                            110.0,      // LEFT MID - REPLACEMENT SERVO
                                            110.0+20.0  // LEFT BACK
                                        )

    init {
        // set knees
        for (i in 0..2) {
            leftHat.setAngle(legs[i+3][KNEE], KNEE_BASE)
            rightHat.setAngle(legs[i+0][KNEE], KNEE_BASE)
        }
    }

    fun turnClockwise () {
        launch {
            // Slightly rotate legs while they are on the ground
            moveHorizontalRLR(HORIZONTAL_TURN_CLOCKWISE)
            moveHorizontalLRL(HORIZONTAL_TURN_CLOCKWISE)
            delay(DELAY_TURN)

            // Raise three legs while returning them to base position
            moveVerticalRLR(VERTICAL_LEFT_RISE, VERTICAL_RIGHT_RISE)
            moveHorizontalRLR(HORIZONTAL_RETURN_TO_BASE)
            delay(DELAY_TURN)

            // Lower the three legs
            moveVerticalRLR(VERTICAL_RETURN_TO_BASE)
            delay(DELAY_TURN)

            // Raise the other three legs while returning them to base position
            moveVerticalLRL(VERTICAL_LEFT_RISE, VERTICAL_RIGHT_RISE)
            moveHorizontalLRL(HORIZONTAL_RETURN_TO_BASE)
            delay(DELAY_TURN)

            // Lower the three legs
            moveVerticalLRL(VERTICAL_RETURN_TO_BASE)
            delay(DELAY_TURN)
        }
    }

    fun turnCounterClockwise () {
        launch {
            // Slightly rotate legs while they are on the ground
            moveHorizontalLRL(HORIZONTAL_TURN_COUNTERCW)
            moveHorizontalRLR(HORIZONTAL_TURN_COUNTERCW)
            delay(DELAY_TURN)

            // Raise three legs while returning them to base position
            moveVerticalLRL(VERTICAL_LEFT_RISE, VERTICAL_RIGHT_RISE)
            moveHorizontalLRL(HORIZONTAL_RETURN_TO_BASE)
            delay(DELAY_TURN)

            // Lower the three legs
            moveVerticalLRL(VERTICAL_RETURN_TO_BASE)
            delay(DELAY_TURN)

            // Raise the other three legs while returning them to base position
            moveVerticalRLR(VERTICAL_LEFT_RISE, VERTICAL_RIGHT_RISE)
            moveHorizontalRLR(HORIZONTAL_RETURN_TO_BASE)
            delay(DELAY_TURN)

            // Lower the three legs
            moveVerticalRLR(VERTICAL_RETURN_TO_BASE)
            delay(DELAY_TURN)
        }
    }

    fun forward () {
        launch {
            for (i in 1..5) {
                // Raise three legs and move them forward
                // The legs on the ground move backwards
                moveVerticalRLR(VERTICAL_LEFT_RISE, VERTICAL_RIGHT_RISE)
                moveVerticalLRL(VERTICAL_RETURN_TO_BASE)
                moveHorizontalRLR(HORIZONTAL_LEFT_FORWARD, HORIZONTAL_RIGHT_FORWARD)
                moveHorizontalLRL(-HORIZONTAL_LEFT_FORWARD, -HORIZONTAL_RIGHT_FORWARD)
                delay(DELAY_FORWARD)

                // Raise the other three legs and move them forward
                // The legs on the ground move backwards
                moveVerticalLRL(VERTICAL_LEFT_RISE, VERTICAL_RIGHT_RISE)
                moveVerticalRLR(VERTICAL_RETURN_TO_BASE)
                moveHorizontalLRL(HORIZONTAL_LEFT_FORWARD, HORIZONTAL_RIGHT_FORWARD)
                moveHorizontalRLR(-HORIZONTAL_LEFT_FORWARD, -HORIZONTAL_RIGHT_FORWARD)
                delay(DELAY_FORWARD)
            }

            standStill()
            delay(DELAY_FORWARD)
        }
    }

    fun standStill () {
        launch {
            // Return all legs to base position
            moveHorizontalRLR(HORIZONTAL_RETURN_TO_BASE)
            moveHorizontalLRL(HORIZONTAL_RETURN_TO_BASE)
            moveVerticalRLR(VERTICAL_RETURN_TO_BASE)
            moveVerticalLRL(VERTICAL_RETURN_TO_BASE)
        }
    }

    fun attack () {
        launch {
            bodyBack()
        }
    }

    fun test () {
        val TEST_RISE = 0.0

        launch {
            moveHorizontalRLR(HORIZONTAL_RETURN_TO_BASE)
            moveHorizontalLRL(HORIZONTAL_RETURN_TO_BASE)

            // Move the legs to their base position + some offset
            // Useful for aligning the legs (servo shaft or horns may be in slightly different position)
            // Middle servos have 180 - ANGLE because the replacement servos move in the opposite direction
            leftHat.setAngle(legs[3][VERTICAL], (verticalBase[3] - TEST_RISE))
            leftHat.setAngle(legs[4][VERTICAL], 180.0 - (verticalBase[4] - TEST_RISE))
            leftHat.setAngle(legs[5][VERTICAL], (verticalBase[5] - TEST_RISE))
            rightHat.setAngle(legs[0][VERTICAL], (verticalBase[0] + TEST_RISE))
            rightHat.setAngle(legs[1][VERTICAL], 180.0 - (verticalBase[1] + TEST_RISE))
            rightHat.setAngle(legs[2][VERTICAL], (verticalBase[2] + TEST_RISE))
        }
    }

    private suspend fun bodyBack () {
        moveHorizontalRLR(HORIZONTAL_LEFT_AGGRO, HORIZONTAL_RIGHT_AGGRO)
        moveHorizontalLRL(HORIZONTAL_LEFT_AGGRO, HORIZONTAL_RIGHT_AGGRO)
        moveVerticalRLR(VERTICAL_RETURN_TO_BASE)
        moveVerticalLRL(VERTICAL_RETURN_TO_BASE)
    }

    private suspend fun moveHorizontalRLR (delta : Double) {moveHorizontalRLR(delta, delta)}
    private suspend fun moveHorizontalRLR (deltaLeft : Double, deltaRight : Double) {
        rightHat.setAngle(legs[RIGHT_FRONT][HORIZONTAL], horizontalBase[RIGHT_FRONT] + deltaRight)
        leftHat.setAngle(legs[LEFT_MID][HORIZONTAL], horizontalBase[LEFT_MID] + deltaLeft)
        rightHat.setAngle(legs[RIGHT_BACK][HORIZONTAL], horizontalBase[RIGHT_BACK] + deltaRight)
    }

    private suspend fun moveHorizontalLRL (delta : Double) {moveHorizontalLRL(delta, delta)}
    private suspend fun moveHorizontalLRL (deltaLeft : Double, deltaRight : Double) {
        leftHat.setAngle(legs[LEFT_FRONT][HORIZONTAL], horizontalBase[LEFT_FRONT] + deltaLeft)
        rightHat.setAngle(legs[RIGHT_MID][HORIZONTAL], horizontalBase[RIGHT_MID] + deltaRight)
        leftHat.setAngle(legs[LEFT_BACK][HORIZONTAL], horizontalBase[LEFT_BACK] + deltaLeft)
    }

    // un argumento = bajar, dos argumentos = subir
    private suspend fun moveVerticalRLR (delta : Double) {moveVerticalRLR(delta, delta)}
    private suspend fun moveVerticalRLR (deltaLeft : Double, deltaRight : Double) {
        rightHat.setAngle(legs[RIGHT_FRONT][VERTICAL], verticalBase[RIGHT_FRONT] + deltaRight)
        leftHat.setAngle(legs[LEFT_MID][VERTICAL], 180.0-verticalBase[LEFT_MID] + deltaLeft)    // Replacement servo
        rightHat.setAngle(legs[RIGHT_BACK][VERTICAL], verticalBase[RIGHT_BACK] + deltaRight)
    }

    // un argumento = bajar, dos argumentos = subir
    private suspend fun moveVerticalLRL (delta : Double) {moveVerticalLRL(delta, delta)}
    private suspend fun moveVerticalLRL (deltaLeft : Double, deltaRight : Double) {
        leftHat.setAngle(legs[LEFT_FRONT][VERTICAL], verticalBase[LEFT_FRONT] + deltaLeft)
        rightHat.setAngle(legs[RIGHT_MID][VERTICAL], 180.0-verticalBase[RIGHT_MID] + deltaRight)    // Replacement servo
        leftHat.setAngle(legs[LEFT_BACK][VERTICAL], verticalBase[LEFT_BACK] + deltaLeft)
    }
}
