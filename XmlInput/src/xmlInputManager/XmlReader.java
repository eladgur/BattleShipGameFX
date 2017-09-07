package xmlInputManager;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class XmlReader {
    public static GameInfo getDataFromXml(String xmlPath) throws InvalidXmFormatException {
        GameInfo data = null;
        JAXBContext jaxbContext = null;
        XmlValidator.validate(xmlPath);
        try {
            jaxbContext = JAXBContext.newInstance(GameInfo.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            File file = new File(xmlPath);
            data = (GameInfo) unmarshaller.unmarshal(file);
        }catch (JAXBException je){
            throw new InvalidXmFormatException("Unvalid format in XML file");
        }catch (Exception e) {
            throw new InvalidXmFormatException("Unvalid format in XML file");
        }

        return data;
    }
}
