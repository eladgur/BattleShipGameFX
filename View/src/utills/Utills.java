package utills;

import controller.GameController;
import javafx.event.EventTarget;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import xmlInputManager.Position;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Utills {
    public static void setControlBackground(Control button, String backgroundPath) {
        BackgroundImage backgroundImage;
        //Set Background Image due to relative path
        backgroundImage = new BackgroundImage(new Image(backgroundPath),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        //Set button background to the Background image
        button.setBackground(new Background(backgroundImage));
    }

    public static void bindChildSizeToParent(Region child, float bindProportion) {
        Region parent = (Region) child.getParent();

        child.prefHeightProperty().bind(parent.heightProperty().multiply(bindProportion)); //BIND Height
        child.prefWidthProperty().bind(parent.widthProperty().multiply(bindProportion));   //BIND Width
    }

    //Return the scene and set the controller
    public static <Controller> Scene generateSceneFromFxml(URL fxmlLocation, Controller controller) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Scene scene;

        fxmlLoader.setLocation(fxmlLocation);
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        scene = new Scene(root);

        return scene;
    }

    public static Scene generateSceneFromFxml(URL fxmlLocation) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Scene scene;

        fxmlLoader.setLocation(fxmlLocation);
        Parent root = fxmlLoader.load();
        scene = new Scene(root);

        return scene;
    }

    public static Position convertVisualPositionToLogicPosition(int row, int column) {
        final byte DIFFERNCE = -1;
        Position position = new Position(row + DIFFERNCE, column + DIFFERNCE);

        return position;
    }

    public static Position getNodePositionInGridPane(Node node) {
        int row = GridPane.getRowIndex(node);
        int column = GridPane.getColumnIndex(node);

        return new Position(row, column);
    }

    public static Position convertVisualPositionToLogicPosition(Position visualPosition) {
        Position position = new Position(visualPosition.getX() - 1, visualPosition.getY() - 1);

        return position;
    }

    public static int getOtherPlayerIndex(int playerIndex) {
        return (playerIndex + 1) % GameController.NUM_OF_PLAYERS;
    }

    public static void playSound(String soundFilePath) {
        try {
            Media sound = new Media(soundFilePath);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Error with PlaySounds");
        }
    }
}
