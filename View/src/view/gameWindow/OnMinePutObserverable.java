package view.gameWindow;


import javafx.beans.Observable;
import xmlInputManager.Position;

public interface OnMinePutObserverable {
    void addOnMinePutListener(OnMinePutObserver listener);

    void removeOnMinePutListener(OnMinePutObserver listener);

    void notifyAllOnMinePutListenersOnMinePut(Position minePosition);
}
