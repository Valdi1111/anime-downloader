package org.valdi.animedownloader.api.episode;

public class Episode implements IEpisode {
    private final String id;
    private final String link;
    private String filename;
    private String downloadLink;

    public Episode(final String id, final String link) {
        this.id = id;
        this.link = link;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLink() {
        return link;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDownloadLink() {
        return downloadLink;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDownloadLink(final String downloadLink) {
        this.downloadLink = downloadLink;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFilename() {
        return filename;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFilename(final String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "Episode{" +
                "id='" + id + '\'' +
                ", filename='" + filename + '\'' +
                ", downloadLink='" + downloadLink + '\'' +
                ", link='" + link +
                '}';
    }

}
