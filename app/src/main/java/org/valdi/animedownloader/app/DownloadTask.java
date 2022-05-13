package org.valdi.animedownloader.app;

import javafx.concurrent.Task;
import org.valdi.animedownloader.api.Download;
import org.valdi.animedownloader.api.episode.IEpisode;

import java.io.File;

public class DownloadTask extends Task<DownloadResult> {
    private final IEpisode episode;
    private final File folder;
    private Download download;

    public DownloadTask(final IEpisode episode, final File folder) {
        this.episode = episode;
        this.folder = folder;
    }

    @Override
    protected DownloadResult call() throws Exception {
        // Setup destination file
        final File file = new File(this.folder, this.episode.getFilename());
        if (file.exists()) {
            return DownloadResult.FILE_ALREADY_EXISTS;
        }
        // Start download
        this.download = new Download(this.episode, file);
        this.download.setHandler(this::updateProgress);
        this.download.start();
        return DownloadResult.SUCCEEDED;
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
