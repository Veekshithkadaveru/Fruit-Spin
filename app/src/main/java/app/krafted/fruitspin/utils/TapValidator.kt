package app.krafted.fruitspin.utils

import app.krafted.fruitspin.viewmodel.Fruit
import kotlin.math.abs

object TapValidator {

    private const val SEGMENT_ANGLE = 360f / 7f
    private const val POINTER_ANGLE = 90f
    private const val TOLERANCE = 15f

    fun validateTap(rotationAngle: Float): Fruit? {
        val fruits = Fruit.values()
        
        val normalizedRotation = ((rotationAngle % 360f) + 360f) % 360f
        
        for (i in fruits.indices) {
            val fruitStartAngle = i * SEGMENT_ANGLE
            val fruitCenterAngle = fruitStartAngle + (SEGMENT_ANGLE / 2f)
            
            val currentCenterAngle = (fruitCenterAngle + normalizedRotation) % 360f
            
            var diff = abs(currentCenterAngle - POINTER_ANGLE)
            if (diff > 180f) {
                diff = 360f - diff
            }
            
            if (diff <= TOLERANCE) {
                return fruits[i]
            }
        }
        
        return null
    }
}