package com.mj.tic.tac.toe.javafx.java.controller;

import com.mj.tic.tac.toe.javafx.java.util.ResourceBundleUtil;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class BaseController implements Initializable {

    @Override
    public abstract void initialize(URL url, ResourceBundle resources);

    protected final String getString(String key) {
        return ResourceBundleUtil.getString(key);
    }
}
