package logic.data.enums;

public enum BoardSize {
    MIN_SIZE(5),
    MAX_SIZE(20);

    int value;

    BoardSize(int value) {
        this.value = value;
    }
}
