package com.mj.tic.tac.toe.javafx.java.util;

import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 21-10-2022
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NodeUtil {

    @SuppressWarnings("unchecked")
    public static <R extends Node> Optional<R> lookup(Node startFrom, String selector) {
        return (Optional<R>) Optional.ofNullable(selector).map(startFrom::lookup);
    }

    @SuppressWarnings("unchecked")
    public static <R extends Node> Set<R> lookupAll(Node startFrom, String selector) {
        return (Set<R>) startFrom.lookupAll(selector);
    }

    public static void setVerticalScrolling(Node node, ScrollBar scrollBar) {
        node.setOnScroll(event -> {
            double value = event.getDeltaY();
            if (value < 0) {
                scrollBar.increment();
            } else if (value > 0) {
                scrollBar.decrement();
            }
        });
    }

    public static void setHorizontalScrolling(Node node, ScrollBar scrollBar) {
        node.setOnScroll(event -> {
            double value = event.getDeltaX();
            if (value < 0) {
                scrollBar.increment();
            } else if (value > 0) {
                scrollBar.decrement();
            }
        });
    }

    public static void setVerticalScrolling(Node node, ScrollPane scrollPane) {
        setVerticalScrolling(node, scrollPane, 0.001);
    }

    public static void setVerticalScrolling(Node node, ScrollPane scrollPane, double scrolling) {
        node.setOnScroll(event -> {
            double value = scrollPane.getVvalue();
            if (event.getDeltaY() < 0) {
                if (value < scrollPane.getVmax()) {
                    scrollPane.setVvalue(value + scrolling);
                }
            } else {
                if (value > scrollPane.getVmin()) {
                    scrollPane.setVvalue(value - scrolling);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <R> R extractUserData(Node node) {
        return (R) Optional.ofNullable(node).map(Node::getUserData).orElse(null);
    }

    @SuppressWarnings("unchecked")
    public static <R> List<R> extractUserData(List<Node> nodes) {
        return (List<R>) Optional
                .ofNullable(nodes)
                .map(Collection::parallelStream)
                .orElse(Stream.empty())
                .map(Node::getUserData)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
