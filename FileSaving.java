package TextEditor;

import javafx.scene.text.Text;
import java.io.*;

class FileSaving {

    FileSaving(String name) {
        try {
            FileWriter writer = new FileWriter(name);
            SuperLink.Node n = KeyEventHandler.charSoFar.getNode(0);
            for (int i =0; i < KeyEventHandler.charSoFar.size(); i++) {
                String charRead= ((Text)(n.item)).getText();
                writer.write(charRead);
                n = n.next;
            }
            writer.close();
        } catch (IOException ioException){
            System.out.println("Error when copying. Exception was: " + ioException);
        }

    }
}
