package controller;

import javafx.beans.Observable;
import view.mainMenu.onRestartGameListener;

public interface onRestartGameEventListenable {

    void addOnRestartGameEventListener(onRestartGameListener listener);

    void removeOnRestartGameEventListener(onRestartGameListener listener);

    void notifyAllOnRestartGameEventListeners();
}
