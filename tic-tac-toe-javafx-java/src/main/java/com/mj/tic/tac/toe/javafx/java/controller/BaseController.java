package com.mj.tic.tac.toe.javafx.java.controller;

import com.mj.tic.tac.toe.javafx.java.util.ResourceBundleUtil;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

public abstract class BaseController implements Initializable {

    protected static final ExecutorService executor = Executors.newSingleThreadExecutor();

    protected static final byte[][] matrix = new byte[3][3];

    private byte count = 0;

    @Override
    public abstract void initialize(URL url, ResourceBundle resources);

    protected void resetCount() {
        this.count = 0;
    }

    protected int nextCount() {
        return ++this.count;
    }

    protected byte getCount() {
        return this.count;
    }

    protected byte getTurn() {
        return (byte) (this.count % 2);
    }

    protected final String getString(String key) {
        return ResourceBundleUtil.getString(key);
    }
}
