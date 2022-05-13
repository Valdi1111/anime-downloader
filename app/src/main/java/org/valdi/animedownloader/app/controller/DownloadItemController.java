package org.valdi.animedownloader.app.controller;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.valdi.animedownloader.api.episode.IEpisode;
import org.valdi.animedownloader.app.DownloadResult;
import org.valdi.animedownloader.app.DownloadTask;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class DownloadItemController extends HBox {
    @FXML
    private CheckBox selectedCheck;
    @FXML
    private Label nameLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label timeLabel;
    @FXML
    private Label percentageLabel;
    @FXML
    private Label progressLabel;
    @FXML
    private Label speedLabel;

    private final ObjectProperty<DownloadResult> downloadResult;
    private final IEpisode episode;
    private final File folder;
    private final DownloadTask task;
    private final Thread thread;
    private final ContextMenu menu;

    public DownloadItemController(final IEpisode episode, final File folder) {
        this.downloadResult = new SimpleObjectProperty<>(DownloadResult.WAITING);
        this.episode = episode;
        this.folder = folder;
        // Setup download task
        this.task = new DownloadTask(this.episode, this.folder);
        this.thread = new Thread(this.task);
        this.setupDownloadTask();
        // Setup context menu
        this.menu = new ContextMenu();
        this.setupContextMenu();
    }

    @FXML
    private void initialize() {
        // Setup components
        this.progressBar.progressProperty().bind(this.task.progressProperty());
        this.progressBar.pseudoClassStateChanged(PseudoClass.getPseudoClass("running"), true);
        this.nameLabel.setText(this.episode.getFilename());
        // Start task
        this.thread.start();
    }

    @FXML
    private void onMousePressed(final MouseEvent event) {
        if (event.isSecondaryButtonDown()) {
            this.menu.show(this, event.getScreenX(), event.getScreenY());
        }
        if (event.isPrimaryButtonDown()) {
            this.selectedProperty().setValue(!this.isSelected());
        }
    }

    private void setupContextMenu() {
        final MenuItem open = new MenuItem("Open folder");
        open.setOnAction(this::onOpen);
        final MenuItem cancel = new MenuItem("Cancel");
        cancel.setOnAction(this::onCancel);
        this.menu.getItems().addAll(open, cancel);
    }

    private void setupDownloadTask() {
        // Setup timer
        long startTime = System.nanoTime();
        final AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // update time
                long elapsedMillis = (now - startTime) / 1_000_000_000;
                long minutes = elapsedMillis / 60;
                long seconds = elapsedMillis % 60;
                timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
            }
        };
        // Setup progress updaters
        this.task.progressProperty().addListener((observable, oldValue, newValue) -> {
            double percentage = newValue.doubleValue() * 100D;
            this.percentageLabel.setText(String.format("%.1f%%", percentage));
        });
        this.task.workDoneProperty().addListener((observable, oldValue, newValue) -> {
            String done = prettyBytes(newValue.longValue());
            String max = prettyBytes(task.totalWorkProperty().longValue());
            this.progressLabel.setText(String.format("%s of %s", done, max));
        });
        // Setup task behaviours
        this.task.setOnRunning(v -> {
            this.downloadResultProperty().setValue(DownloadResult.RUNNING);
            timer.start();
        });
        this.task.setOnSucceeded(v -> {
            this.downloadResultProperty().setValue(this.task.getValue());
            this.menu.getItems().remove(1);
            timer.stop();
            if (this.task.getValue() == DownloadResult.FILE_ALREADY_EXISTS) {
                this.changeState("cancelled", "File already exists!");
                return;
            }
            this.changeState("succeeded", "Done!");
        });
        this.task.setOnCancelled(v -> {
            this.downloadResultProperty().setValue(DownloadResult.CANCELLED);
            this.menu.getItems().remove(1);
            timer.stop();
            this.changeState("cancelled", "Cancelled!");
        });
        this.task.setOnFailed(v -> {
            this.downloadResultProperty().setValue(DownloadResult.FAILED);
            this.menu.getItems().remove(1);
            timer.stop();
            this.changeState("failed", "Failed!");
        });
    }

    private void changeState(final String value, final String name) {
        this.progressBar.pseudoClassStateChanged(PseudoClass.getPseudoClass("running"), false);
        this.progressBar.pseudoClassStateChanged(PseudoClass.getPseudoClass(value), true);
        this.speedLabel.setText(name);
    }

    private String prettyBytes(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + "B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.2f%cB", value / 1024.0, ci.current());
    }

    private void onOpen(final ActionEvent event) {
        try {
            Desktop.getDesktop().open(this.folder);
        } catch (IOException e) {
            System.err.println("Cannot open download folder.");
        }
    }

    private void onCancel(final ActionEvent event) {
        this.cancel();
    }

    public void cancel() {
        if (this.task.isRunning()) {
            this.task.cancel(true);
        }
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public DownloadResult getDownloadResult() {
        return this.downloadResult.get();
    }

    public ObjectProperty<DownloadResult> downloadResultProperty() {
        return this.downloadResult;
    }

    public boolean isSelected() {
        return this.selectedCheck.isSelected();
    }

    public BooleanProperty selectedProperty() {
        return this.selectedCheck.selectedProperty();
    }

}
