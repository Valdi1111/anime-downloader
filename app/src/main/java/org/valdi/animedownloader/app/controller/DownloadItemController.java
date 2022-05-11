package org.valdi.animedownloader.app.controller;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import org.valdi.animedownloader.api.episode.IEpisode;
import org.valdi.animedownloader.app.DownloadResult;
import org.valdi.animedownloader.app.DownloadTask;

import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class DownloadItemController {
    @FXML
    private HBox pane;
    @FXML
    private CheckBox selectCheck;
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

    private final IEpisode episode;
    private final File folder;
    private DownloadResult result;

    public DownloadItemController(final IEpisode episode, final File folder) {
        this.episode = episode;
        this.folder = folder;
        this.result = DownloadResult.WAITING;
    }

    @FXML
    private void initialize() {
        long startTime = System.currentTimeMillis();
        this.nameLabel.setText(this.episode.getFilename());
        // Setup download task
        final DownloadTask task = new DownloadTask(this.episode, this.folder);
        this.progressBar.progressProperty().bind(task.progressProperty());
        // Setup timer
        final AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // update time
                long elapsedMillis = (System.currentTimeMillis() - startTime) / 1000;
                long minutes = elapsedMillis / 60;
                long seconds = elapsedMillis % 60;
                timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
                // update progress
                String done = prettyBytes((long) task.getWorkDone());
                String max = prettyBytes((long) task.getTotalWork());
                progressLabel.setText(String.format("%s of %s", done, max));
                // update percentage
                int percentage = (int) task.getProgress() * 100;
                percentageLabel.setText(String.format("%d%%", percentage));
            }
        };
        // Setup task behaviours
        task.setOnRunning(v -> {
            this.result = DownloadResult.RUNNING;
            timer.start();
        });
        task.setOnSucceeded(v -> {
            this.result = task.getValue();
            this.pane.setStyle("-fx-background-color:#90EE90;");
            timer.stop();
        });
        task.setOnCancelled(v -> {
            this.result = DownloadResult.CANCELLED;
            timer.stop();
        });
        task.setOnFailed(v -> {
            this.result = DownloadResult.FAILED;
            timer.stop();
        });
        // Start task and timer
        final Thread thread = new Thread(task);
        thread.start();
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

}
