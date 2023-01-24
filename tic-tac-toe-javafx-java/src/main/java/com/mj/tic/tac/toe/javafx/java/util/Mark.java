package com.mj.tic.tac.toe.javafx.java.util;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 22-01-2023
 */

@Data
@AllArgsConstructor
public final class Mark {

    private final char value;

    private final Coordinates coordinates;
}
