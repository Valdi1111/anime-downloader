package org.valdi.animedownloader.api.episode;

public interface IEpisode {

    /**
     * Get id for this episode
     *
     * @return the id
     */
    String getId();

    /**
     * Get link for this episode.
     *
     * @return the link
     */
    String getLink();

    /**
     * Get download link for this episode file.
     *
     * @return the download link
     */
    String getDownloadLink();

    /**
     * Set download link for this episode file.
     *
     * @param downloadLink the new download link
     */
    void setDownloadLink(final String downloadLink);

    /**
     * Get file name for this episode.
     *
     * @return the file name
     */
    String getFilename();

    /**
     * Set file name for this episode.
     *
     * @param filename the new file name
     */
    void setFilename(final String filename);

}
