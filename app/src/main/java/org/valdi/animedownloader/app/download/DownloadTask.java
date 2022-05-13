package org.valdi.animedownloader.app.download;

import javafx.concurrent.Task;
import org.valdi.animedownloader.api.Download;
import org.valdi.animedownloader.api.episode.IEpisode;

import java.io.File;

/**
 * Utility class to download an episode using {@link Task}.
 */
public class DownloadTask extends Task<DownloadState> {
    private final IEpisode episode;
    private final File folder;
    private Download download;

    /**
     * Create a new instance of {@link DownloadTask} with a given episode and a download folder.
     *
     * @param episode the episode
     * @param folder  the download folder
     */
    public DownloadTask(final IEpisode episode, final File folder) {
        this.episode = episode;
        this.folder = folder;
    }

    @Override
    protected DownloadState call() throws Exception {
        // Setup destination file
        final File file = new File(this.folder, this.episode.getFilename());
        if (file.exists()) {
            return DownloadState.FILE_ALREADY_EXISTS;
        }
        // Start download
        this.download = new Download(this.episode, file);
        this.download.setHandler(this::updateProgress);
        this.download.start();
        return DownloadState.SUCCEEDED;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean val = super.cancel(mayInterruptIfRunning);
        if (this.download != null) {
            this.download.stop();
        }
        return val;
    }

}
