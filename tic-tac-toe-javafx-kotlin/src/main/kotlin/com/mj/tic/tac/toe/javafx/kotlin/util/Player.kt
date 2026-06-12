package com.mj.tic.tac.toe.javafx.kotlin.util

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

enum class Player(val value: Byte) {
    HUMAN(1),
    COMPUTER(2);

    companion object {
        fun from(value: Byte): Player = values().firstOrNull {
            it.value == value
        } ?: throw IllegalArgumentException("Unknown player value: $value")
    }

    fun opponent(): Player = if (this == HUMAN) COMPUTER else HUMAN
}
