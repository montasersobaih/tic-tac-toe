package com.mj.tic.tac.toe.javafx.java.controller;

import com.mj.tic.tac.toe.javafx.java.util.ResourceBundleUtil;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

public abstract class BaseController implements Initializable {

    @Override
    public abstract void initialize(URL url, ResourceBundle resources);

    protected final String getString(String key) {
        return ResourceBundleUtil.getString(key);
    }
}
