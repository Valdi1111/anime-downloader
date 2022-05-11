package org.valdi.animedownloader.app;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.StringConverter;
import org.valdi.animedownloader.api.episode.IEpisode;

public class EpisodeItem {
    private final IEpisode episode;
    private final BooleanProperty selected;

    public EpisodeItem(final IEpisode episode) {
        this.episode = episode;
        this.selected = new SimpleBooleanProperty(true);
    }

    public IEpisode getEpisode() {
        return this.episode;
    }

    public boolean isSelected() {
        return this.selected.get();
    }

    public BooleanProperty selectedProperty() {
        return this.selected;
    }

    public static class Converter extends StringConverter<EpisodeItem> {

        @Override
        public String toString(final EpisodeItem object) {
            return object.getEpisode().getFilename();
        }

        @Override
        public EpisodeItem fromString(final String string) {
            return null;
        }

    }

}
