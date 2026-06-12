package com.mj.tic.tac.toe.javafx.kotlin.util

import java.util.ResourceBundle

/**
 * Singleton utility for internationalized (i18n) string lookups.
 *
 * Loads multiple resource bundles at initialization and searches them
 * in order when a key is requested. The first match found is returned.
 * If no bundle contains the requested key, the raw key string is
 * returned as a fallback, preventing null values or exceptions in the UI.
 *
 * Currently configured to load two bundles:
 * - `"controls"` — UI control labels and button text.
 * - `"messages"` — Game-related messages (win, lose, draw alerts).
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 31-10-2022
 */

object ResourceBundleUtil {

    /** Ordered array of resource bundle base names to search. */
    private val resourcesBaseNames = arrayOf("controls", "messages")

    /** The list of loaded [ResourceBundle] instances, populated at init time. */
    private val resources: MutableList<ResourceBundle> = ArrayList()

    init {
        for (baseName in resourcesBaseNames) {
            resources.add(ResourceBundle.getBundle(baseName))
        }
    }

    /**
     * Looks up a localized string by key across all loaded resource bundles.
     *
     * Searches each loaded bundle in insertion order (controls first,
     * then messages). Returns the first match found. If none of the
     * bundles contain the key, the key itself is returned.
     *
     * @param key The resource bundle key to look up.
     * @return The localized string for the key, or the key itself if
     *   not found in any bundle.
     */
    fun getString(key: String): String {
        for (resource in resources) {
            if (resource.containsKey(key)) {
                return resource.getString(key)
            }
        }

        return key
    }
}