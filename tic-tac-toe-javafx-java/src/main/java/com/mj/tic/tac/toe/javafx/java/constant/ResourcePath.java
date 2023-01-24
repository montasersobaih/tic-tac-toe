package com.mj.tic.tac.toe.javafx.java.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ResourcePath {

    //==================================================={Interfaces}===================================================
    public static final String INTERFACE = "/interface/";

    public static final String DIALOG = INTERFACE + "dialog/";

    public static final String LAYOUT = INTERFACE + "layout/";

    //====================================================={Assist}=====================================================
    private static final String ASSIST = "/assist/";

    public static final String STYLE_SHEET = ASSIST + "css/";

    public static final String STYLE_CONTROL = STYLE_SHEET + "control/";

    public static final String STYLE_USER_INTERFACE = STYLE_SHEET + "interface/";

    public static final String STYLE_DIALOG_INTERFACE = STYLE_USER_INTERFACE + "dialog/";

    public static final String IMAGE = ASSIST + "image/";
}
