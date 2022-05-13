package org.valdi.animedownloader.api;

import org.valdi.animedownloader.api.episode.IEpisode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.BiConsumer;

/**
 * Utility class to download an episode.
 */
public class Download {
    private final IEpisode episode;
    private final File file;
    private boolean running;
    private BiConsumer<Long, Long> handler;

    /**
     * Create a new instance of {@link Download} given an episode and a destination file.
     *
     * @param episode the episode
     * @param file    the new file of the episode
     */
    public Download(final IEpisode episode, final File file) {
        this.episode = episode;
        this.file = file;
        this.running = true;
    }

    /**
     * An action to perform every time there is an update.
     *
     * @param handler the handler
     */
    public void setHandler(final BiConsumer<Long, Long> handler) {
        this.handler = handler;
    }

    /**
     * Start download.
     *
     * @throws Exception on error
     */
    public void start() throws Exception {
        // Setup url
        final URL url = new URL(this.episode.getDownloadLink());
        final URLConnection conn = url.openConnection();
        conn.connect();
        // Getting file length
        long max = conn.getContentLength();
        // Handle progress
        if (this.handler != null) {
            this.handler.accept(0L, max);
        }
        // Input stream to read file - with 8k buffer
        try (InputStream in = new BufferedInputStream(url.openStream(), 8192);
             FileOutputStream out = new FileOutputStream(this.file)) {
            int count;
            long done = 0;
            byte[] data = new byte[1024];
            while (this.running && (count = in.read(data)) != -1) {
                done += count;
                // handle progress
                if (this.handler != null) {
                    this.handler.accept(done, max);
                }
                // writing data to file
                out.write(data, 0, count);
            }
            // flushing output
            out.flush();
        }
        // Delete file if download has been stopped
        if (!this.running) {
            this.file.delete();
        }
    }

    /**
     * Stop download.
     */
    public void stop() {
        this.running = false;
    }

}
