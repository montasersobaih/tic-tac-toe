package com.mj.tic.tac.toe.javafx.kotlin.util

import java.util.*

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 31-10-2022
 */

object ResourceBundleUtil {

    private val resourcesBaseNames = arrayOf("controls", "messages")

    private val resources: MutableList<ResourceBundle> = ArrayList()

    init {
        for (baseName in resourcesBaseNames) {
            resources.add(ResourceBundle.getBundle(baseName))
        }
    }

    fun getString(key: String): String {
        if (Objects.nonNull(key)) {
            for (resource in resources) {
                if (resource.containsKey(key)) {
                    return resource.getString(key)
                }
            }
        }

        return key
    }
}