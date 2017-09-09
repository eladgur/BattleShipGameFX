package controller;

public enum ScenesNames {
    MAINMENU("MAIN MENU"),
    GAMEWINDOW("GAME WINDOW"),
    GAMEENDWINDOW("GAME END WINDOW");

    private final String value;

    ScenesNames(String s) {
        this.value = s;
    }

    public String value() {
        return
                 this.value;
    }
}
