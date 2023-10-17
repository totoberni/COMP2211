package uk.ac.soton.comp2211.g16.ad;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class Application extends javafx.application.Application {
  private static final Logger LOGGER = Logger.getLogger(Application.class.getName());
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("dashboard.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
    stage.setTitle("ECS Ad - Group 16 Underdevelopment");
    stage.getIcons().clear();
    stage.getIcons().add(new Image("ECSAD.png"));

    stage.setScene(scene);
    stage.setMinHeight(720);
    stage.setMinWidth(1280);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}