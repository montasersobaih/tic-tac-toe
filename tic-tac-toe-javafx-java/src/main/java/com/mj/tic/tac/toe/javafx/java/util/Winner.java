package com.mj.tic.tac.toe.javafx.java.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 22-01-2023
 */

@Data
@AllArgsConstructor
public final class Winner {

    private final char player;

    private final List<Coordinates> locations;
}
