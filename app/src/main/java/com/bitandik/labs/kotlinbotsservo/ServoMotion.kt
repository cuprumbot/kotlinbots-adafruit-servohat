package com.bitandik.labs.kotlinbotsservo

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

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

private const val HORIZONTAL_TURN_CLOCKWISE = -30.0
private const val HORIZONTAL_TURN_COUNTERCW = 30.0
private const val HORIZONTAL_RIGHT_FORWARD  = 15.0
private const val HORIZONTAL_LEFT_FORWARD   = -15.0
private const val HORIZONTAL_RETURN_TO_BASE = 0.0

private const val KNEE_BASE = 90.0

private const val DELAY_TURN            = 80
private const val DELAY_FORWARD         = 80

class ServoMotion (sHat: ServoHat) {
    private var hat = sHat

    private var legs = arrayOf  (
                                arrayOf(0, 1, 2),
                                arrayOf(3, 4, -1),
                                arrayOf(5, 6, 7),
                                arrayOf(8, 9, 10),
                                arrayOf(11, 12, -1),
                                arrayOf(13, 14, 15)
                                )

    private var horizontalBase = arrayOf(70.0, 90.0, 110.0, 110.0, 90.0, 70.0)
    private var verticalBase = arrayOf(70.0, 70.0, 70.0, 110.0, 110.0, 110.0)

    init {
        // set knees
        for (i in 0..5) {
            hat.setAngle(legs[i][KNEE], KNEE_BASE)
        }
    }

    fun turnClockwise () {
        launch {
            moveHorizontalRLR(HORIZONTAL_TURN_CLOCKWISE)
            moveHorizontalLRL(HORIZONTAL_TURN_CLOCKWISE)
            delay(DELAY_TURN)

            moveVerticalRLR(VERTICAL_LEFT_RISE, VERTICAL_RIGHT_RISE)
            moveHorizontalRLR(HORIZONTAL_RETURN_TO_BASE)
            delay(DELAY_TURN)

            moveVerticalRLR(VERTICAL_RETURN_TO_BASE)
            delay(DELAY_TURN)

            moveVerticalLRL(VERTICAL_LEFT_RISE, VERTICAL_RIGHT_RISE)
            moveHorizontalLRL(HORIZONTAL_RETURN_TO_BASE)
            delay(DELAY_TURN)

            moveVerticalLRL(VERTICAL_RETURN_TO_BASE)
            delay(DELAY_TURN)
        }
    }

    fun turnCounterClockwise () {
        launch {
            moveHorizontalLRL(HORIZONTAL_TURN_COUNTERCW)
            moveHorizontalRLR(HORIZONTAL_TURN_COUNTERCW)
            delay(DELAY_TURN)

            moveVerticalLRL(VERTICAL_LEFT_RISE, VERTICAL_RIGHT_RISE)
            moveHorizontalLRL(HORIZONTAL_RETURN_TO_BASE)
            delay(DELAY_TURN)

            moveVerticalLRL(VERTICAL_RETURN_TO_BASE)
            delay(DELAY_TURN)

            moveVerticalRLR(VERTICAL_LEFT_RISE, VERTICAL_RIGHT_RISE)
            moveHorizontalRLR(HORIZONTAL_RETURN_TO_BASE)
            delay(DELAY_TURN)

            moveVerticalRLR(VERTICAL_RETURN_TO_BASE)
            delay(DELAY_TURN)
        }
    }

    fun forward () {
        launch {
            for (i in 1..5) {
                moveVerticalRLR(VERTICAL_LEFT_RISE, VERTICAL_RIGHT_RISE)
                moveVerticalLRL(VERTICAL_RETURN_TO_BASE)
                moveHorizontalRLR(HORIZONTAL_LEFT_FORWARD, HORIZONTAL_RIGHT_FORWARD)
                moveHorizontalLRL(-HORIZONTAL_LEFT_FORWARD, -HORIZONTAL_RIGHT_FORWARD)
                delay(DELAY_FORWARD)

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
            moveHorizontalRLR(HORIZONTAL_RETURN_TO_BASE)
            moveHorizontalLRL(HORIZONTAL_RETURN_TO_BASE)
            moveVerticalRLR(VERTICAL_RETURN_TO_BASE)
            moveVerticalLRL(VERTICAL_RETURN_TO_BASE)
        }
    }

    private suspend fun moveHorizontalRLR (delta : Double) {moveHorizontalRLR(delta, delta)}
    private suspend fun moveHorizontalRLR (deltaLeft : Double, deltaRight : Double) {
        hat.setAngle(legs[RIGHT_FRONT][HORIZONTAL], horizontalBase[RIGHT_FRONT] + deltaRight)
        hat.setAngle(legs[LEFT_MID][HORIZONTAL], horizontalBase[LEFT_MID] + deltaLeft)
        hat.setAngle(legs[RIGHT_BACK][HORIZONTAL], horizontalBase[RIGHT_BACK] + deltaRight)
    }

    private suspend fun moveHorizontalLRL (delta : Double) {moveHorizontalLRL(delta, delta)}
    private suspend fun moveHorizontalLRL (deltaLeft : Double, deltaRight : Double) {
        hat.setAngle(legs[LEFT_FRONT][HORIZONTAL], horizontalBase[LEFT_FRONT] + deltaLeft)
        hat.setAngle(legs[RIGHT_MID][HORIZONTAL], horizontalBase[RIGHT_MID] + deltaRight)
        hat.setAngle(legs[LEFT_BACK][HORIZONTAL], horizontalBase[LEFT_BACK] + deltaLeft)
    }

    // un argumento = bajar, dos argumentos = subir
    private suspend fun moveVerticalRLR (delta : Double) {moveVerticalRLR(delta, delta)}
    private suspend fun moveVerticalRLR (deltaLeft : Double, deltaRight : Double) {
        hat.setAngle(legs[RIGHT_FRONT][VERTICAL], verticalBase[RIGHT_FRONT] + deltaRight)
        hat.setAngle(legs[LEFT_MID][VERTICAL], verticalBase[LEFT_MID] + deltaLeft)
        hat.setAngle(legs[RIGHT_BACK][VERTICAL], verticalBase[RIGHT_BACK] + deltaRight)
    }

    // un argumento = bajar, dos argumentos = subir
    private suspend fun moveVerticalLRL (delta : Double) {moveVerticalLRL(delta, delta)}
    private suspend fun moveVerticalLRL (deltaLeft : Double, deltaRight : Double) {
        hat.setAngle(legs[LEFT_FRONT][VERTICAL], verticalBase[LEFT_FRONT] + deltaLeft)
        hat.setAngle(legs[RIGHT_MID][VERTICAL], verticalBase[RIGHT_MID] + deltaRight)
        hat.setAngle(legs[LEFT_BACK][VERTICAL], verticalBase[LEFT_BACK] + deltaLeft)
    }
}
