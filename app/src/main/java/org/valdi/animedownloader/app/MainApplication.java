package org.valdi.animedownloader.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApplication extends Application {

    @Override
    public void start(final Stage stage) throws IOException {
        final FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("download-list.fxml"));
        final Scene scene = new Scene(loader.load());
        final URL url = MainApplication.class.getResource("favicon-32x32.png");
        if (url != null) {
            stage.getIcons().add(new Image(url.toString()));
        }
        stage.setTitle(App.NAME);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

}