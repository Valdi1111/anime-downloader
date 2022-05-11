package org.valdi.animedownloader.app;

import javafx.concurrent.Task;
import org.valdi.animedownloader.api.episode.IEpisode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadTask extends Task<DownloadResult> {
    private final IEpisode episode;
    private final File folder;

    public DownloadTask(final IEpisode episode, final File folder) {
        this.episode = episode;
        this.folder = folder;
    }

    @Override
    protected DownloadResult call() throws Exception {
        this.updateProgress(0D, 1000D);
        // Setup url
        final URL url = new URL(this.episode.getDownloadLink());
        final URLConnection conn = url.openConnection();
        conn.connect();
        // Getting file length
        int len = conn.getContentLength();
        final File file = new File(this.folder, this.episode.getFilename());
        if(file.exists()) {
            return DownloadResult.FILE_ALREADY_EXISTS;
        }

        System.out.println("link: " + episode.getDownloadLink());
        System.out.println("name: " + episode.getFilename());
        System.out.println("file: " + file.getAbsolutePath());
        System.out.println();

        // Input stream to read file - with 8k buffer
        try (InputStream input = new BufferedInputStream(url.openStream(), 8192);
             FileOutputStream output = new FileOutputStream(file)) {
            int count;
            long downloaded = 0;
            byte[] data = new byte[1024];
            while ((count = input.read(data)) != -1) {
                downloaded += count;
                // update progress
                this.updateProgress(downloaded, len);
                // writing data to file
                output.write(data, 0, count);
            }
            // flushing output
            output.flush();
        }
        return DownloadResult.SUCCEEDED;
    }
}
