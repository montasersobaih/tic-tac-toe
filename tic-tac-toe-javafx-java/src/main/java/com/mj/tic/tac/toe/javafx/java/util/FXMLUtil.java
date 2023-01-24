package com.mj.tic.tac.toe.javafx.java.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @since 31-07-2021
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FXMLUtil {

    public static FXMLLoader getFXMLLoader(FXInterface fxInterface) {
        return Stream.of(fxInterface)
                .map(FXInterface::toString)
                .map(FXMLUtil.class::getResource)
                .map(FXMLLoader::new)
                .peek(loader -> Optional
                        .of("controls")
                        .map(ResourceBundle::getBundle)
                        .ifPresent(loader::setResources))
                .findFirst()
                .orElse(null);
    }

    public static Pane loadInterface(FXMLLoader loader) {
        Pane pane = null;

        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pane;
    }

    public static Pane loadInterface(FXInterface fxInterface) {
        return Optional.of(fxInterface)
                .map(FXMLUtil::getFXMLLoader)
                .map(FXMLUtil::loadInterface)
                .orElse(null);

    }
}
