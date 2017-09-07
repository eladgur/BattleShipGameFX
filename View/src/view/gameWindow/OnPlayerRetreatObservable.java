package view.gameWindow;

public interface OnPlayerRetreatObservable {
    void addOnPlayerRetreatListener(OnPlayerRetreatObserver listener);

    void removeOnPlayerRetreatListener(OnPlayerRetreatObserver listener);

    void notifyAllOnPlayerRetreatListeners(String playerName);
}
