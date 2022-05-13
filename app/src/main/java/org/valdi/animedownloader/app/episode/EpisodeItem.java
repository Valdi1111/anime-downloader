package org.valdi.animedownloader.app.episode;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.valdi.animedownloader.api.episode.IEpisode;
import org.valdi.animedownloader.app.controller.NewDownloadController;

/**
 * Utility class representing an episode in {@link NewDownloadController}.
 */
public class EpisodeItem {
    private final IEpisode episode;
    private final BooleanProperty selected;

    /**
     * Create a new instance of {@link EpisodeItem} for a given episode.
     *
     * @param episode the episode
     */
    public EpisodeItem(final IEpisode episode) {
        this.episode = episode;
        this.selected = new SimpleBooleanProperty(true);
    }

    /**
     * Get the episode for this item.
     *
     * @return the episode
     */
    public IEpisode getEpisode() {
        return this.episode;
    }

    /**
     * Indicates whether this episode item is selected.
     *
     * @return true if selected, false otherwise
     */
    public boolean isSelected() {
        return this.selected.get();
    }

    /**
     * Indicates whether this episode item is selected.
     *
     * @return the selected property
     */
    public BooleanProperty selectedProperty() {
        return this.selected;
    }

}
