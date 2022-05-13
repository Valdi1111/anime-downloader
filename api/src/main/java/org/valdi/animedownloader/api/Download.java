package org.valdi.animedownloader.api;

import org.valdi.animedownloader.api.episode.IEpisode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Download {
    private final IEpisode episode;
    private final File file;
    private boolean running;
    private Handler handler;

    public Download(final IEpisode episode, final File file) {
        this.episode = episode;
        this.file = file;
        this.running = true;
    }

    public void setHandler(final Handler handler) {
        this.handler = handler;
    }

    public void start() throws Exception {
        // Setup url
        final URL url = new URL(this.episode.getDownloadLink());
        final URLConnection conn = url.openConnection();
        conn.connect();
        // Getting file length
        int len = conn.getContentLength();
        // Handle progress
        if (this.handler != null) {
            this.handler.onProgress(0L, len);
        }
        // Input stream to read file - with 8k buffer
        try (InputStream input = new BufferedInputStream(url.openStream(), 8192);
             FileOutputStream output = new FileOutputStream(this.file)) {
            int count;
            long downloaded = 0;
            byte[] data = new byte[1024];
            while (this.running && (count = input.read(data)) != -1) {
                downloaded += count;
                // handle progress
                if (this.handler != null) {
                    this.handler.onProgress(downloaded, len);
                }
                // writing data to file
                output.write(data, 0, count);
            }
            // flushing output
            output.flush();
        }
        // Delete file if download has been stopped
        if (!this.running) {
            this.file.delete();
        }
    }

    public void stop() {
        this.running = false;
    }

    public interface Handler {

        void onProgress(long workDone, long max);

    }

}
