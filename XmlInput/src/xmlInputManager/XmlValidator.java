package xmlInputManager;

import jdk.internal.util.xml.impl.ReaderUTF8;

import java.io.*;

public class XmlValidator {

    public static void validate(String filePath) throws InvalidXmFormatException {
        validateFileXmlExtention(filePath);
        validateFileExist(filePath);
    }

    public static void validateFileXmlExtention(String filePath) throws InvalidXmFormatException {
        if (!filePath.endsWith(".xml")) {
            throw new InvalidXmFormatException("File extension is not xml, Please provide an valid xml file");
        }
    }

    public static void validateFileExist(String filePath) throws InvalidXmFormatException {

        File file = new File(filePath);
        if (!file.exists()) {
            throw new InvalidXmFormatException("Xml File not exist");
        }
    }
}
