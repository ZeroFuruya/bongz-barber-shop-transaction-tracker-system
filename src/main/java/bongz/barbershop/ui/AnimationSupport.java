package bongz.barbershop.ui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBase;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public final class AnimationSupport {

    private static final String BUTTON_ANIMATION_KEY = "button-animation-installed";
    private static final Duration BUTTON_ANIMATION_DURATION = Duration.millis(140);
    private static final Duration VIEW_SWITCH_FADE_OUT = Duration.millis(130);
    private static final Duration VIEW_SWITCH_FADE_IN = Duration.millis(180);
    private static final Duration MODAL_ANIMATION_DURATION = Duration.millis(220);
    private static final Duration ROOT_ENTRANCE_DURATION = Duration.millis(260);

    private AnimationSupport() {
    }

    public static void installInteractiveControls(Parent root) {
        if (root == null) {
            return;
        }

        installOnNode(root);
    }

    public static void playRootEntrance(Node root) {
        if (root == null) {
            return;
        }

        root.setOpacity(0);
        root.setScaleX(0.985);
        root.setScaleY(0.985);

        FadeTransition fadeTransition = new FadeTransition(ROOT_ENTRANCE_DURATION, root);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setInterpolator(Interpolator.EASE_BOTH);

        ScaleTransition scaleTransition = new ScaleTransition(ROOT_ENTRANCE_DURATION, root);
        scaleTransition.setFromX(0.985);
        scaleTransition.setFromY(0.985);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

        new ParallelTransition(fadeTransition, scaleTransition).play();
    }

    public static void switchVisiblePane(Node currentPane, Node targetPane) {
        if (targetPane == null) {
            return;
        }

        if (currentPane == targetPane) {
            ensureShown(targetPane);
            return;
        }

        if (currentPane == null || !currentPane.isVisible()) {
            ensureShown(targetPane);
            playPaneEntrance(targetPane);
            return;
        }

        FadeTransition fadeOut = new FadeTransition(VIEW_SWITCH_FADE_OUT, currentPane);
        fadeOut.setFromValue(currentPane.getOpacity() <= 0 ? 1 : currentPane.getOpacity());
        fadeOut.setToValue(0);
        fadeOut.setInterpolator(Interpolator.EASE_BOTH);
        fadeOut.setOnFinished(event -> {
            currentPane.setVisible(false);
            currentPane.setManaged(false);
            currentPane.setOpacity(1);
            ensureShown(targetPane);
            playPaneEntrance(targetPane);
        });
        fadeOut.play();
    }

    public static void playModalEntrance(Pane overlay, Node content) {
        if (overlay == null) {
            return;
        }

        overlay.setOpacity(0);

        FadeTransition overlayFade = new FadeTransition(MODAL_ANIMATION_DURATION, overlay);
        overlayFade.setFromValue(0);
        overlayFade.setToValue(1);
        overlayFade.setInterpolator(Interpolator.EASE_BOTH);

        if (content == null) {
            overlayFade.play();
            return;
        }

        content.setOpacity(0);
        content.setScaleX(0.96);
        content.setScaleY(0.96);
        content.setTranslateY(18);

        FadeTransition contentFade = new FadeTransition(MODAL_ANIMATION_DURATION, content);
        contentFade.setFromValue(0);
        contentFade.setToValue(1);
        contentFade.setInterpolator(Interpolator.EASE_BOTH);

        ScaleTransition contentScale = new ScaleTransition(MODAL_ANIMATION_DURATION, content);
        contentScale.setFromX(0.96);
        contentScale.setFromY(0.96);
        contentScale.setToX(1);
        contentScale.setToY(1);
        contentScale.setInterpolator(Interpolator.EASE_BOTH);

        TranslateTransition contentSlide = new TranslateTransition(MODAL_ANIMATION_DURATION, content);
        contentSlide.setFromY(18);
        contentSlide.setToY(0);
        contentSlide.setInterpolator(Interpolator.EASE_BOTH);

        new ParallelTransition(overlayFade, contentFade, contentScale, contentSlide).play();
    }

    public static void playModalExit(Pane overlay, Node content, Runnable onFinished) {
        if (overlay == null) {
            if (onFinished != null) {
                onFinished.run();
            }
            return;
        }

        FadeTransition overlayFade = new FadeTransition(MODAL_ANIMATION_DURATION, overlay);
        overlayFade.setFromValue(overlay.getOpacity() <= 0 ? 1 : overlay.getOpacity());
        overlayFade.setToValue(0);
        overlayFade.setInterpolator(Interpolator.EASE_BOTH);

        if (content == null) {
            overlayFade.setOnFinished(event -> runCallback(onFinished));
            overlayFade.play();
            return;
        }

        FadeTransition contentFade = new FadeTransition(MODAL_ANIMATION_DURATION, content);
        contentFade.setFromValue(content.getOpacity() <= 0 ? 1 : content.getOpacity());
        contentFade.setToValue(0);
        contentFade.setInterpolator(Interpolator.EASE_BOTH);

        ScaleTransition contentScale = new ScaleTransition(MODAL_ANIMATION_DURATION, content);
        contentScale.setFromX(content.getScaleX() == 0 ? 1 : content.getScaleX());
        contentScale.setFromY(content.getScaleY() == 0 ? 1 : content.getScaleY());
        contentScale.setToX(0.96);
        contentScale.setToY(0.96);
        contentScale.setInterpolator(Interpolator.EASE_BOTH);

        TranslateTransition contentSlide = new TranslateTransition(MODAL_ANIMATION_DURATION, content);
        contentSlide.setFromY(content.getTranslateY());
        contentSlide.setToY(14);
        contentSlide.setInterpolator(Interpolator.EASE_BOTH);

        ParallelTransition exitTransition = new ParallelTransition(overlayFade, contentFade, contentScale, contentSlide);
        exitTransition.setOnFinished(event -> runCallback(onFinished));
        exitTransition.play();
    }

    private static void playPaneEntrance(Node pane) {
        pane.setOpacity(0);
        pane.setTranslateY(12);

        FadeTransition fadeTransition = new FadeTransition(VIEW_SWITCH_FADE_IN, pane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setInterpolator(Interpolator.EASE_BOTH);

        TranslateTransition slideTransition = new TranslateTransition(VIEW_SWITCH_FADE_IN, pane);
        slideTransition.setFromY(12);
        slideTransition.setToY(0);
        slideTransition.setInterpolator(Interpolator.EASE_BOTH);

        new ParallelTransition(fadeTransition, slideTransition).play();
    }

    private static void ensureShown(Node pane) {
        pane.setManaged(true);
        pane.setVisible(true);
    }

    private static void installOnNode(Node node) {
        if (node instanceof ButtonBase buttonBase) {
            installButtonAnimation(buttonBase);
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                installOnNode(child);
            }
        }
    }

    private static void installButtonAnimation(ButtonBase button) {
        if (button.getProperties().containsKey(BUTTON_ANIMATION_KEY)) {
            return;
        }

        button.getProperties().put(BUTTON_ANIMATION_KEY, true);

        button.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> animateButton(button, 1.02, -1));
        button.addEventHandler(MouseEvent.MOUSE_EXITED, event -> animateButton(button, 1, 0));
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> animateButton(button, 0.97, 1));
        button.addEventHandler(MouseEvent.MOUSE_RELEASED,
                event -> animateButton(button, button.isHover() ? 1.02 : 1, button.isHover() ? -1 : 0));
    }

    private static void animateButton(ButtonBase button, double scale, double translateY) {
        ScaleTransition scaleTransition = new ScaleTransition(BUTTON_ANIMATION_DURATION, button);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

        TranslateTransition translateTransition = new TranslateTransition(BUTTON_ANIMATION_DURATION, button);
        translateTransition.setToY(translateY);
        translateTransition.setInterpolator(Interpolator.EASE_BOTH);

        new ParallelTransition(scaleTransition, translateTransition).play();
    }

    private static void runCallback(Runnable callback) {
        if (callback != null) {
            callback.run();
        }
    }
}
