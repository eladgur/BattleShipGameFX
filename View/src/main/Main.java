package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.mainMenu.MainMenuController;

public class Main extends Application {
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader();
        MainMenuController mainMenuController;

        fxmlLoader.setLocation(MainMenuController.class.getResource("mainMenu.fxml"));
        Parent root = fxmlLoader.load();

        mainMenuController = fxmlLoader.getController();
        mainMenuController.setMain(this);
        primaryStage.centerOnScreen();
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(370);
        primaryStage.setMinHeight(250);
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
