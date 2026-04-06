package com.mj.tic.tac.toe.javafx.java.constant;

import com.mj.tic.tac.toe.javafx.java.util.FXInterface;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */

public enum DInterface implements FXInterface {

    CONFIRM_DIALOG(ResourcePath.DIALOG.concat("confirm_dialog.fxml"));

    private final String value;

    DInterface(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
