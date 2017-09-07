package logic.data.enums;

public enum Direction {
    ROW("ROW"),
    COLUMN("COLUMN"),
    DOWN_RIGHT("DOWN_RIGHT"),
    UP_RIGHT("UP_RIGHT"),
    RIGHT_UP("RIGHT_UP"),
    RIGHT_DOWN("RIGHT_DOWN");
    String name;

    Direction(String name) {
        this.name = name;
    }

    public static boolean isValidDirectionName(String directionName) {
        boolean valid = false;

        for (Direction direction : values()) {
            if (directionName.toUpperCase().equals(direction.name)) {
                valid = true;
                break;
            }
        }

        return valid;
    }
}
