package org.valdi.animedownloader.app;

import javafx.application.Application;

import java.io.File;
import java.net.URISyntaxException;

public class App {
    public static final String NAME = "Download Serie";
    private static File folder;

    public static void main(final String... args) throws URISyntaxException {
        App.folder = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        Application.launch(MainApplication.class, args);
    }

    public static File getFolder() {
        return App.folder;
    }

}
