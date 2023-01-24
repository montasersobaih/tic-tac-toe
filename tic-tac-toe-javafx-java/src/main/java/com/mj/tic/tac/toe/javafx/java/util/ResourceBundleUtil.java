package com.mj.tic.tac.toe.javafx.java.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 31-10-2022
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResourceBundleUtil {

    private static final String[] resourcesBaseNames = {"controls", "messages"};

    private static final List<ResourceBundle> resources = new ArrayList<>();

    static {
        for (String baseName : resourcesBaseNames) {
            resources.add(ResourceBundle.getBundle(baseName));
        }
    }

    public static String getString(String key) {
        if (Objects.nonNull(key)) {
            for (ResourceBundle resource : resources) {
                if (resource.containsKey(key)) {
                    return resource.getString(key);
                }
            }
        }

        return key;
    }
}
