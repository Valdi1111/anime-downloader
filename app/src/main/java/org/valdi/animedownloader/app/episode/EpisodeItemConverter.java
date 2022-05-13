package org.valdi.animedownloader.app.episode;

import javafx.util.StringConverter;

/**
 * Implementation of {@link StringConverter} for {@link EpisodeItem}.
 */
public class EpisodeItemConverter extends StringConverter<EpisodeItem> {

    @Override
    public String toString(final EpisodeItem object) {
        return object.getEpisode().getFilename();
    }

    @Override
    public EpisodeItem fromString(final String string) {
        return null;
    }

}