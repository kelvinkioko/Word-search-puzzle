package com.word.search.puzzle.play.ui.game

enum class Direction(val angleDegree: Float) {
    EAST(0.0f),
    NORTH_EAST(315.0f),
    NORTH(270.0f),
    NORTH_WEST(225.0f),
    WEST(180.0f),
    SOUTH_WEST(135.0f),
    SOUTH(90.0f),
    SOUTH_EAST(45.0f);

    val isAngle: Boolean
        get() = this == NORTH_EAST || this == SOUTH_EAST || this == NORTH_WEST || this == SOUTH_WEST
    val isLeft: Boolean
        get() = this == WEST || this == NORTH_WEST || this == SOUTH_WEST
    val isUp: Boolean
        get() = this == NORTH || this == NORTH_WEST || this == NORTH_EAST
    val isDown: Boolean
        get() = this == SOUTH || this == SOUTH_WEST || this == SOUTH_EAST
    val isRight: Boolean
        get() = this == EAST || this == NORTH_EAST || this == SOUTH_EAST

    companion object {
        private const val SNAP_COEFF = 0.785398f
        @JvmStatic
        fun getDirection(radians: Float): Direction {
            return if (radians <= .5 * SNAP_COEFF && radians >= -(.5 * SNAP_COEFF)) {
                EAST
            } else if (radians > .5 * SNAP_COEFF && radians < 1.5 * SNAP_COEFF) {
                NORTH_EAST
            } else if (radians >= 1.5 * SNAP_COEFF && radians <= 2.5 * SNAP_COEFF) {
                NORTH
            } else if (radians > 2.5 * SNAP_COEFF && radians < 3.5 * SNAP_COEFF) {
                NORTH_WEST
            } else if (radians >= 3.5 * SNAP_COEFF || radians <= -(3.5 * SNAP_COEFF)) {
                WEST
            } else if (radians < -(2.5 * SNAP_COEFF) && radians > -(3.5 * SNAP_COEFF)) {
                SOUTH_WEST
            } else if (radians <= -(1.5 * SNAP_COEFF) && radians >= -(2.5 * SNAP_COEFF)) {
                SOUTH
            } else {
                SOUTH_EAST
            }
        }
    }
}