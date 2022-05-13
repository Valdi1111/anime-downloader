package org.valdi.animedownloader.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;
import org.valdi.animedownloader.api.AnimeDownloader;
import org.valdi.animedownloader.api.downloader.AnimeWorldDownloder;
import org.valdi.animedownloader.api.episode.IEpisode;
import org.valdi.animedownloader.app.App;
import org.valdi.animedownloader.app.episode.EpisodeItem;
import org.valdi.animedownloader.app.episode.EpisodeItemConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for new-download.fxml
 */
public class NewDownloadController {
    // Body
    @FXML
    private TextField linkField;
    @FXML
    private TextField folderField;
    @FXML
    private CheckListView<EpisodeItem> episodesList;
    // Toolbar
    @FXML
    private Button queryButton;
    @FXML
    private Button downloadButton;
    @FXML
    private Button closeButton;

    private final AnimeDownloader downloader;
    private final List<IEpisode> episodes;
    private File folder;

    /**
     * Create a new instance of {@link NewDownloadController}.
     */
    public NewDownloadController() {
        this.downloader = new AnimeDownloader();
        this.downloader.registerHandler(new AnimeWorldDownloder());
        this.episodes = new ArrayList<>();
    }

    /**
     * Initialize components.
     */
    @FXML
    private void initialize() {
        this.folderField.setText(App.getFolder().getAbsolutePath());
        this.episodesList.setCellFactory(f -> new CheckBoxListCell<>(EpisodeItem::selectedProperty, new EpisodeItemConverter()));
    }

    /**
     * Select download folder for episodes.
     */
    @FXML
    private void selectFolder() {
        final DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select download folder");
        chooser.setInitialDirectory(App.getFolder());
        this.folder = chooser.showDialog(this.episodesList.getParent().getScene().getWindow());
        this.folderField.setText(this.folder.getAbsolutePath());
    }

    /**
     * Query episodes from url.
     */
    @FXML
    private void onQuery() {
        // Query episodes
        String link = this.linkField.getText();
        if (link == null || link.isBlank()) {
            this.showError("Invalid Link!");
            return;
        }
        // Disable query button
        this.queryButton.setDisable(true);
        // Check if the link can be handled
        link = link.trim();
        if (!this.downloader.match(link)) {
            System.out.println("Can't find a downloader for this website!");
            return;
        }
        // Query links
        List<IEpisode> episodes;
        try {
            episodes = this.downloader.queryEpisodes(link);
        } catch (Exception e) {
            System.out.println("Something went wrong while querying episodes!");
            return;
        }
        // Enable ep list and add episodes
        this.episodesList.setDisable(false);
        this.episodesList.getItems().addAll(episodes
                .stream()
                .map(EpisodeItem::new)
                .collect(Collectors.toList()));
        this.downloadButton.setDisable(false);
    }

    /**
     * Start download of episodes.
     */
    @FXML
    private void onDownload() {
        if (this.folder == null) {
            this.showError("Folder not selected!");
            return;
        }
        this.episodes.addAll(this.episodesList.getItems()
                .stream()
                .filter(EpisodeItem::isSelected)
                .map(EpisodeItem::getEpisode)
                .collect(Collectors.toList()));
        this.onClose();
    }

    /**
     * Close gui.
     */
    @FXML
    private void onClose() {
        final Stage primaryStage = (Stage) this.closeButton.getScene().getWindow();
        primaryStage.close();
    }

    /**
     * Show alert error with a message.
     *
     * @param msg error message
     */
    private void showError(final String msg) {
        final Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.CLOSE);
        final Stage stage = (Stage) this.episodesList.getScene().getWindow();
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(stage.getIcons());
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(stage);
        alert.show();
    }

    /**
     * Get episodes found in website url.
     *
     * @return the list of episodes
     */
    public List<IEpisode> getEpisodes() {
        return this.episodes;
    }

    /**
     * Get download folder.
     *
     * @return the download folder, null if nothing has been selected.
     */
    public File getFolder() {
        return this.folder;
    }

}
