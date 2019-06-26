package TextEditor;

import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class MouseClickEventHandler implements EventHandler<MouseEvent> {
    /* A Text object that will be used to print the current mouse position. */
    private Text positionText;

    MouseClickEventHandler(Group root) {
        /* For now, since there's no mouse position yet, just create an empty Text object. */
        positionText = new Text("");
        positionText.setTextOrigin(VPos.BOTTOM);
        root.getChildren().add(positionText);
    }

    private SuperLink.Node closestNode(SuperLink.Node start, SuperLink.Node end, int knownX) {
        if (start == null) {
            return end;
        }
        int min = (int) ((Text)(start.item)).getX() - knownX;
        min = Math.abs(min);
        SuperLink.Node result = start;
        while (start != end) {
            start = start.next;
            int temp = (int) ((Text)(start.item)).getX() - knownX;
            temp = Math.abs(temp);
            if (temp < min) {
                min = temp;
                result = start;
            }
        }
        return result;
    }


    @Override
    public void handle(MouseEvent mouseEvent) {
        double mousePressedX = mouseEvent.getX();
        double mousePressedY = mouseEvent.getY() - Editor.textStartY;

        if (KeyEventHandler.charSoFar.isEmpty()){
            KeyEventHandler.currentX = Editor.textStartX;
            KeyEventHandler.currentY = Editor.textStartY;

        }else {
            int lineNum = (int) (mousePressedY - Editor.textStartY) / KeyEventHandler.charHeightNow();

            int roundX = (int) Math.round(mousePressedX);

            SuperLink.Node startN;

            SuperLink.Node endN;
            if (lineNum < (KeyEventHandler.line).size() - 1) {
                startN = (SuperLink.Node) KeyEventHandler.line.get(lineNum);
                endN = (SuperLink.Node) KeyEventHandler.line.get(lineNum + 1);
            } else {
                startN = null;
                endN = KeyEventHandler.charSoFar.getNode(0).prev.prev;

            }

            SuperLink.Node n = closestNode(startN, endN, roundX);


            KeyEventHandler.charSoFar.setCurrentNode(n.prev);

            KeyEventHandler.render();
        }

        positionText.setX(mousePressedX);
        positionText.setY(mousePressedY);
    }
}
