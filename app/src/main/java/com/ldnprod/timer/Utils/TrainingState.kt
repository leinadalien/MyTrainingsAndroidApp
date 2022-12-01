package com.ldnprod.timer.Utils

import com.ldnprod.timer.Entities.Exercise

class TrainingState(private val title: String, private var exercises: List<Exercise>) {
    init {
        exercises = exercises.toList()
    }
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is TrainingState -> {
                if(title != other.title) false else {
                    if (exercises.size != other.exercises.size) false else {
                        var success = true
                        for(i in exercises.indices) {
                            if (!exercises[i].equals(other.exercises[i])) success = false
                            break
                        }
                        success
                    }
                }
            }
            else -> false
        }
    }
}

