package uk.ac.soton.comp2211.g16.ad;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class SelectCampaign extends javafx.application.Application {
    //private static final Logger LOGGER = Logger.getLogger(selectCampaign.class.getName());
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SelectCampaign.class.getResource("selectCampaign.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("ECS Ad - Please select a Campaign to open");
        stage.getIcons().clear();
        stage.getIcons().add(new Image("ECSAD.png"));
        stage.setScene(scene);
        stage.setMinWidth(600);
        stage.setMinHeight(400);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}