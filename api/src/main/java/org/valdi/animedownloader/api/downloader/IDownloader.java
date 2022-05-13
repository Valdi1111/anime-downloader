package org.valdi.animedownloader.api.downloader;

import org.valdi.animedownloader.api.episode.IEpisode;

import java.io.IOException;
import java.util.List;

/**
 * Interface representing a downloader for a specific website.
 */
public interface IDownloader {

    /**
     * Get url to the website of this downloader.
     *
     * @return the url
     */
    String getUrl();

    /**
     * Check if the given url can be handled with this downloader.
     *
     * @param url the url
     * @return true or false
     */
    default boolean match(final String url) {
        return url != null && url.startsWith(this.getUrl());
    }

    /**
     * Get episode from the given url.
     *
     * @param url the url
     * @return the created episode
     * @throws IOException on error
     */
    IEpisode queryEpisode(final String url) throws IOException;

    /**
     * Get all episodes from the given url.
     *
     * @param url the url
     * @return the list of created episodes
     * @throws IOException on error
     */
    List<IEpisode> queryEpisodes(final String url) throws IOException;

}
