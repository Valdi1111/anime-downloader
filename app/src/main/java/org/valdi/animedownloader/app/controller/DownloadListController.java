package org.valdi.animedownloader.app.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.valdi.animedownloader.api.episode.IEpisode;
import org.valdi.animedownloader.app.App;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for download-list.fxml
 */
public class DownloadListController {
    @FXML
    private VBox downloadList;

    /**
     * Open new-download gui.
     *
     * @throws IOException on error
     */
    @FXML
    private void onDownload() throws IOException {
        // Load new download
        final FXMLLoader loader = new FXMLLoader(App.class.getResource("new-download.fxml"));
        final Scene scene = new Scene(loader.load());
        // Setup stage
        final Stage stage = new Stage();
        final Stage primary = (Stage) this.downloadList.getScene().getWindow();
        stage.getIcons().addAll(primary.getIcons());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primary);
        stage.setTitle(App.NAME);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.showAndWait();
        // Wait until the window is closed
        final NewDownloadController controller = loader.getController();
        final List<IEpisode> episodes = controller.getEpisodes();
        final File folder = controller.getFolder();
        if (episodes.isEmpty()) {
            return;
        }
        // Download episodes
        for (final IEpisode episode : episodes) {
            final DownloadItemController download = new DownloadItemController(episode, folder);
            this.downloadList.getChildren().add(download);
        }
    }

    /**
     * Close program.
     */
    @FXML
    private void onExit() {
        this.close();
    }

    /**
     * Close the window and stop all downloads.
     */
    public void close() {
        this.downloadList.getChildren().stream()
                .filter(DownloadItemController.class::isInstance)
                .map(DownloadItemController.class::cast)
                .forEach(c -> c.cancel(true));
        Platform.exit();
        System.exit(0);
    }

    /**
     * Remove selected items from download list.
     */
    @FXML
    private void removeDownloads() {
        this.downloadList.getChildren().removeAll(
                this.downloadList.getChildren().stream()
                        .filter(DownloadItemController.class::isInstance)
                        .map(DownloadItemController.class::cast)
                        .filter(DownloadItemController::isSelected)
                        .collect(Collectors.toList())
        );
    }

}
