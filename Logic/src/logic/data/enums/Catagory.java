package logic.data.enums;

public enum Catagory {
    REGULAR("REGULAR"),
    L_SHAPE("L_SHAPE");
    String name;

    Catagory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static boolean isACatagory(String catagoryName, GameType gameType) {
        boolean isValidName = false;

        catagoryName = catagoryName.toUpperCase();
        if (gameType.equals(GameType.BASIC)) {
            isValidName = REGULAR.name.equals(catagoryName);
        } else {
            for (Catagory catagory : values()) {
                if (catagory.getName().equals(catagoryName)) {
                    isValidName = true;
                    break;
                }
            }
        }

        return isValidName;
    }
}

