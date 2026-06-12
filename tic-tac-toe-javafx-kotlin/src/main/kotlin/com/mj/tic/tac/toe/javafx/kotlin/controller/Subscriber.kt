package com.mj.tic.tac.toe.javafx.kotlin.controller

import com.mj.tic.tac.toe.javafx.kotlin.util.GameState

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

fun interface Subscriber<T> {
    fun update(state: GameState, value: T?)
}
