package TextEditor;

import javafx.event.EventHandler;
import javafx.scene.text.Text;
import java.io.*;

class FileReading {
    FileReading(EventHandler k, File f) {
        try {
            /* Check to make sure that the input file exists! */
            if (!f.exists()) {
                System.out.println("Unable to read because file" + " does not exist");
                return;
            }
            FileReader reader = new FileReader(f);
            BufferedReader bufferedReader = new BufferedReader(reader);

            int intRead = -1;
            /* Keep reading from the file input read() returns -1, which means the end of the file
               was reached. */
            ((KeyEventHandler) k).charSoFar = new SuperLink();
            while ((intRead = bufferedReader.read()) != -1){
                char read = (char) intRead;
                String charRead = String.valueOf(read);
                Text t;
                if (read == '\r' || read == '\n'){
                    t = new Text("\n");
                }else{
                    t = new Text(charRead);
                }

                ((KeyEventHandler) k).charSoFar.addChar(t);
                ((KeyEventHandler) k).g.getChildren().add(t);

            }
            KeyEventHandler.charSoFar.setCurrentNode(KeyEventHandler.charSoFar.getNode(0).prev);
            ((KeyEventHandler) k).render();
            Editor.c.updatePosition(Editor.textStartX, Editor.textStartY);

            bufferedReader.close();

        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found. Exception was: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when copying. Exception was: " + ioException);
        }
    }
}
