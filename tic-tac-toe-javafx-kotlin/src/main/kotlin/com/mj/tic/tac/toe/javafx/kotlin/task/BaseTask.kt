package com.mj.tic.tac.toe.javafx.kotlin.task

import javafx.concurrent.Task

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

abstract class BaseTask<V> : Task<V>() {

    @Throws(Exception::class)
    abstract override fun call(): V
}