package org.valdi.animedownloader.app.controller;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.valdi.animedownloader.api.episode.IEpisode;
import org.valdi.animedownloader.app.App;
import org.valdi.animedownloader.app.download.DownloadState;
import org.valdi.animedownloader.app.download.DownloadTask;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * Controller for download-item.fxml
 */
public class DownloadItemController extends HBox {
    // Body components
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
    // Class fields
    private final ObjectProperty<DownloadState> state;
    private final IEpisode episode;
    private final File folder;
    private final DownloadTask task;
    private final Thread thread;
    private final ContextMenu menu;

    /**
     * Create a new instance of {@link DownloadItemController}, setup context menu,
     * setup download task and load components from fxml file.
     *
     * @param episode the episode to download
     * @param folder  the folder to download the file
     * @throws IOException on error
     */
    public DownloadItemController(final IEpisode episode, final File folder) throws IOException {
        this.state = new SimpleObjectProperty<>(DownloadState.WAITING);
        this.episode = episode;
        this.folder = folder;
        // Setup download task
        this.task = new DownloadTask(this.episode, this.folder);
        this.thread = new Thread(this.task);
        this.setupDownloadTask();
        // Setup context menu
        this.menu = new ContextMenu();
        this.setupContextMenu();
        // Load from fxml
        this.loadFromFile();
    }

    /**
     * Load components from fxml file.
     *
     * @throws IOException on error
     */
    private void loadFromFile() throws IOException {
        final FXMLLoader item = new FXMLLoader(App.class.getResource("download-item.fxml"));
        item.setController(this);
        item.setRoot(this);
        item.load();
    }

    /**
     * Initialize components and start download task.
     */
    @FXML
    private void initialize() {
        // Setup components
        this.progressBar.progressProperty().bind(this.task.progressProperty());
        this.progressBar.pseudoClassStateChanged(PseudoClass.getPseudoClass("running"), true);
        this.nameLabel.setText(this.episode.getFilename());
        // Start task
        this.thread.start();
    }

    /**
     * Execute action on mouse press:
     * <ul>
     *     <li>Select item on left click.</li>
     *     <li>Show context menu on right click.</li>
     * </ul>
     *
     * @param event the event
     */
    @FXML
    private void onMousePressed(final MouseEvent event) {
        // Select item on left click
        if (event.isPrimaryButtonDown()) {
            this.selectedProperty().setValue(!this.isSelected());
        }
        // Show context menu on right click
        if (event.isSecondaryButtonDown()) {
            this.menu.show(this, event.getScreenX(), event.getScreenY());
        }
    }

    /**
     * Setup context menu.
     */
    private void setupContextMenu() {
        final MenuItem open = new MenuItem("Open folder");
        open.setOnAction(this::onOpen);
        final MenuItem cancel = new MenuItem("Cancel");
        cancel.setOnAction(this::onCancel);
        this.menu.getItems().addAll(open, cancel);
    }

    /**
     * Setup download task.
     */
    private void setupDownloadTask() {
        // Setup updater
        final Updater updater = new Updater();
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
            this.stateProperty().setValue(DownloadState.RUNNING);
            updater.start();
        });
        this.task.setOnSucceeded(v -> {
            this.stateProperty().setValue(this.task.getValue());
            this.menu.getItems().remove(1);
            updater.stop();
            if (this.task.getValue() == DownloadState.FILE_ALREADY_EXISTS) {
                this.changeState("cancelled", "File already exists!");
                return;
            }
            this.changeState("succeeded", "Done!");
        });
        this.task.setOnCancelled(v -> {
            this.stateProperty().setValue(DownloadState.CANCELLED);
            this.menu.getItems().remove(1);
            updater.stop();
            this.changeState("cancelled", "Cancelled!");
        });
        this.task.setOnFailed(v -> {
            this.stateProperty().setValue(DownloadState.FAILED);
            this.menu.getItems().remove(1);
            updater.stop();
            this.changeState("failed", "Failed!");
        });
    }

    /**
     * Update task state on gui.
     *
     * @param value progress bar class
     * @param name  speed label text value
     */
    private void changeState(final String value, final String name) {
        this.progressBar.pseudoClassStateChanged(PseudoClass.getPseudoClass("running"), false);
        this.progressBar.pseudoClassStateChanged(PseudoClass.getPseudoClass(value), true);
        this.speedLabel.setText(name);
    }

    /**
     * Convert bytes to pretty string.
     *
     * @param bytes the bytes amount
     * @return the string
     */
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

    /**
     * Context menu action - Open folder
     *
     * @param event the event
     */
    private void onOpen(final ActionEvent event) {
        try {
            Desktop.getDesktop().open(this.folder);
        } catch (IOException e) {
            System.err.println("Cannot open download folder.");
        }
    }

    /**
     * Context menu action - Cancel
     *
     * @param event the event
     */
    private void onCancel(final ActionEvent event) {
        this.cancel(false);
    }

    /**
     * Cancel this download, delete the file.
     *
     * @param wait wait until the task is done
     */
    public void cancel(boolean wait) {
        if (this.task.isRunning()) {
            this.task.cancel(true);
        }
        if (wait) {
            try {
                this.thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Indicates the state of this download item.
     *
     * @return the current download state
     */
    public DownloadState getState() {
        return this.state.get();
    }

    /**
     * Indicates the state of this download item.
     *
     * @return the state property
     */
    public ObjectProperty<DownloadState> stateProperty() {
        return this.state;
    }

    /**
     * Indicates whether this download item is selected.
     *
     * @return true if selected, false otherwise
     */
    public boolean isSelected() {
        return this.selectedCheck.isSelected();
    }

    /**
     * Indicates whether this download item is selected.
     *
     * @return the selected property
     */
    public BooleanProperty selectedProperty() {
        return this.selectedCheck.selectedProperty();
    }

    private class Updater extends AnimationTimer {
        private long startTime;
        private long lastDone = 0L;
        private long lastTime = startTime;

        @Override
        public void start() {
            super.start();
            this.startTime = System.nanoTime();
            this.lastTime = startTime;
            this.lastDone = 0L;
        }

        @Override
        public void handle(long now) {
            // update time
            this.updateTime(now);
            // update speed
            this.updateSpeed(now);
        }

        private void updateTime(long now) {
            long elapsed = (now - this.startTime) / 1_000_000_000;
            long minutes = elapsed / 60;
            long seconds = elapsed % 60;
            timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
        }

        private void updateSpeed(long now) {
            if ((now - this.lastTime) >= 1_000_000_000) {
                this.lastTime = now;
                long done = task.workDoneProperty().longValue();
                long delta = (done - this.lastDone);
                this.lastDone = done;
                speedLabel.setText(String.format("%s/s", prettyBytes(delta)));
            }
        }

    }

}
