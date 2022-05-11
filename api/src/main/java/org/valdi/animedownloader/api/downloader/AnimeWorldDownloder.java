package org.valdi.animedownloader.api.downloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.valdi.animedownloader.api.episode.Episode;
import org.valdi.animedownloader.api.episode.IEpisode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnimeWorldDownloder implements IDownloader {
    public static final String WEBSITE = "https://www.animeworld.tv";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl() {
        return WEBSITE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEpisode queryEpisode(final String url) throws IOException {
        final Document doc = Jsoup.connect(url).get();
        final Element elem = doc.select("div.server")
                .not(".hidden")
                .get(0)
                .getElementsByClass("episode")
                .select("a.active")
                .get(0);
        return this.createEpisode(elem);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IEpisode> queryEpisodes(final String url) throws IOException {
        final Document doc = Jsoup.connect(url).get();
        final Elements elems = doc.select("div.server")
                .not(".hidden")
                .get(0)
                .getElementsByClass("episode")
                .select("a");
        final List<IEpisode> episodes = new ArrayList<>();
        for (final Element elem : elems) {
            episodes.add(this.createEpisode(elem));
        }
        return episodes;
    }

    /**
     * Create episode from document element.
     *
     * @param elem the element
     * @return an episode
     * @throws IOException on error
     */
    private IEpisode createEpisode(final Element elem) throws IOException {
        final String id = elem.attr("data-episode-num");
        final String link = this.getUrl() + elem.attr("href");
        final IEpisode episode = new Episode(id, link);
        // Connect to episode link
        final Document doc = Jsoup.connect(episode.getLink()).get();
        // Get download link
        final String dlLink = doc.getElementById("downloadLink")
                .attr("href")
                .replace("download-file.php?id=", "");
        episode.setDownloadLink(dlLink);
        // Set file name
        final String filename = dlLink.substring(dlLink.lastIndexOf('/') + 1);
        episode.setFilename(filename);
        return episode;
    }

}
