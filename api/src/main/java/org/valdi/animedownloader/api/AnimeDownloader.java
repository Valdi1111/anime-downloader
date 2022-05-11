package org.valdi.animedownloader.api;

import org.valdi.animedownloader.api.downloader.IDownloader;
import org.valdi.animedownloader.api.episode.IEpisode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnimeDownloader implements IDownloader {
    private final List<IDownloader> downloaders;

    public AnimeDownloader() {
        this.downloaders = new ArrayList<>();
    }

    /**
     * Register new downloader.
     *
     * @param downloader the new downloader
     */
    public void registerHandler(final IDownloader downloader) {
        this.downloaders.add(downloader);
    }

    /**
     * Get registered downloaders.
     *
     * @return the downloaders.
     */
    public List<IDownloader> getHandlers() {
        return new ArrayList<>(this.downloaders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(final String url) {
        return this.downloaders.stream().anyMatch((d) -> d.match(url));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEpisode queryEpisode(final String url) throws IOException {
        final Optional<IDownloader> downloader = this.downloaders.stream().filter((d) -> d.match(url)).findFirst();
        if (downloader.isPresent()) {
            return downloader.get().queryEpisode(url);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IEpisode> queryEpisodes(final String url) throws IOException {
        final Optional<IDownloader> downloader = this.downloaders.stream().filter((d) -> d.match(url)).findFirst();
        if (downloader.isPresent()) {
            return downloader.get().queryEpisodes(url);
        }
        return null;
    }

}
