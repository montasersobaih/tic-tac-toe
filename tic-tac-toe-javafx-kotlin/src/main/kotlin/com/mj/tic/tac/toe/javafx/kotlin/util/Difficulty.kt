package com.mj.tic.tac.toe.javafx.kotlin.util

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

enum class Difficulty(val resourceKey: String) {
    EASY("control.label.difficulty.easy"),
    MEDIUM("control.label.difficulty.medium"),
    HARD("control.label.difficulty.hard");

    override fun toString(): String = resourceKey
}
