package logic.data.enums;

public enum GameType {
    BASIC("BASIC"),
    ADVANCE("ADVANCE");

    String value;

    GameType(String value) {
        this.value = value;
    }

    public String getName() {
        return value;
    }
    public static boolean isValidGameTypeName(String typeName){
        return typeName.equals(BASIC.value) || typeName.equals(ADVANCE.value);
    }
}
