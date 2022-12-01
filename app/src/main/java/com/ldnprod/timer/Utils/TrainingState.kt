package com.ldnprod.timer.Utils

import com.ldnprod.timer.Entities.Exercise
class TrainingState(private val title: String, exercises: List<Exercise>) {
    private val _exercises: List<Exercise>
    init {
        val tempList = ArrayList<Exercise>()
        exercises.forEach { tempList.add(it.copy()) }
        _exercises = tempList.toList()
    }
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is TrainingState -> {
                hashCode() == other.hashCode()
            }
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + _exercises.hashCode()
        _exercises.forEach { result = 31 * result + it.hashCode() }
        return result
    }
}

