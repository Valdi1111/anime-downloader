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

public class DownloadListController {
    @FXML
    private VBox downloadsPane;

    @FXML
    private void onDownload() throws IOException {
        // Load new download
        final FXMLLoader loader = new FXMLLoader(App.class.getResource("new-download.fxml"));
        final Scene scene = new Scene(loader.load());
        // Setup stage
        final Stage stage = new Stage();
        final Stage primaryStage = (Stage) this.downloadsPane.getScene().getWindow();
        stage.getIcons().addAll(primaryStage.getIcons());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setTitle(App.NAME);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.showAndWait();
        // Wait until the window is closed
        final NewDownloadController controller = loader.getController();
        final List<IEpisode> episodes = controller.getEpisodes();
        final File folder = controller.getFolder();
        if(episodes.isEmpty()) {
            return;
        }
        // Download episodes
        for(final IEpisode e : episodes) {
            final FXMLLoader itemLoader = new FXMLLoader(App.class.getResource("download-item.fxml"));
            itemLoader.setControllerFactory(c -> new DownloadItemController(e, folder));
            itemLoader.load();
            this.downloadsPane.getChildren().add(itemLoader.getRoot());
        }
    }

    @FXML
    private void onExit() {
        Platform.exit();
        System.exit(0);
    }

}
