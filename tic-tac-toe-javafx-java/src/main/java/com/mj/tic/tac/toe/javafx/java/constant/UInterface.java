package com.mj.tic.tac.toe.javafx.java.constant;

import com.mj.tic.tac.toe.javafx.java.util.FXInterface;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */

public enum UInterface implements FXInterface {

    APPLICATION_PAGE(ResourcePath.INTERFACE.concat("view.fxml"));

    private final String value;

    UInterface(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
