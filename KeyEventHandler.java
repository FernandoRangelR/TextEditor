package TextEditor;

import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.ArrayList;

public class KeyEventHandler implements EventHandler<KeyEvent> {

    static int currentX;
    static int currentY;
    static Group g;

    /* The Text to display on the screen. */
    private static int fontSize = 12;
    private static String fontName = "Verdana";
    /* This is used to store all the chars */
    static SuperLink charSoFar;
    /* Used to know which line you are at */
    static ArrayList line;
    /* Undo and redo tracking */
    private static TrackingList track;

    KeyEventHandler(final Group root, int windowWidth, int windowHeight) {
        g = new Group();
        charSoFar = new SuperLink();
        line = new ArrayList();
        track = new TrackingList();

        currentX = Editor.textStartX;
        currentY = Editor.textStartY;

        /* All new Nodes need to be added to the root in order to be displayed. */
        root.getChildren().add(g);
    }


    private static int fontSizeGen(){
        return fontSize;
    }

    private static int charWidth(Text t) {
        return (int) Math.round(t.getLayoutBounds().getWidth());
    }

    private static int charHeight(Text t) {
        return (int) Math.round(t.getLayoutBounds().getHeight());
    }

    static int charHeightNow() {
        Text temp = new Text(" ");
        temp.setTextOrigin(VPos.TOP);
        temp.setFont(Font.font(fontName, fontSizeGen()));
        return charHeight(temp);
    }


    /* Long word that cannot fit in the first line */
    private static SuperLink.Node wordStartsWith(SuperLink.Node someLetterNode) {
        SuperLink.Node temp = someLetterNode;
        while (!(((Text)(temp.item)).getText()).equals(" ")) {
            temp = temp.prev;
            if (temp == charSoFar.getNode(0).prev) {
                return someLetterNode;
            }
        }
        return temp;
    }



    static void render() {
        int X = Editor.textStartX;
        int Y = Editor.textStartY;
        if (charSoFar.isEmpty()) {
            currentX = Editor.textStartX;
            currentY = Editor.textStartY;
            Editor.c.updateSize();
            Editor.c.updatePosition(currentX, currentY);
        } else {
            line = new ArrayList();
            SuperLink.Node start = charSoFar.getNode(0);
            SuperLink.Node node = start;

            /* Node has not reached sentinel */
            while (node != start.prev){

                /* Tell the stored data that this is a new line */
                if (X == Editor.textStartX){
                    int lineIndex = (Y - Editor.textStartY) / charHeightNow();
                    line.add(lineIndex, node);
                }

                Text temp = (Text) node.item;
                temp.setTextOrigin(VPos.TOP);
                temp.setFont(Font.font(fontName, fontSizeGen()));
                temp.setX(X);
                temp.setY(Y);
                X += charWidth(temp);


                if (temp.getText().equals("\n") ||temp.getText().equals("\r\n")){
                    Y += charHeightNow();
                    X = Editor.textStartX;
                }

                /* Word wrap */
                if ( X >= Editor.usableScreenWidth - 5 && !(temp.getText()).equals(" ")){
                    X = Editor.textStartX;
                    Y += charHeightNow();
                    node = wordStartsWith(node);
                }
                node = node.next;
            }

            if (line.size() > 32) {
                Editor.scrollBar.setMax(10);
            } else {
                Editor.scrollBar.setMax(0);
            }

            /* Update cursor */
            SuperLink.Node cn = charSoFar.getCurrentNode();
            if (charSoFar.isAtSentinel()) {
                currentX = Editor.textStartX;
                currentY = Editor.textStartY;
            }else if (cn == charSoFar.getNode(0).prev.prev && ((Text) (cn.item)).getText().equals("\n")){
                currentX = Editor.textStartX;
                currentY += charHeightNow();
            }else if (cn == charSoFar.getNode(0).prev.prev && !((Text) (cn.item)).getText().equals("\n")){
                currentX = (int) (((Text) (cn.item)).getX() + charWidth((Text) cn.item));
                currentY = (int) ((Text) (cn.item)).getY();
            } else if (((Text) (cn.item)).getText().equals("\n")){
                if (charSoFar.hasNext() && (int) ((Text) (cn.next.item)).getX() == Editor.textStartX) {
                    currentX = (int) ((Text) (cn.next.item)).getX();
                    currentY = (int) ((Text) (cn.next.item)).getY();
                } else {
                    currentX = (int) ((Text) (cn.item)).getX();
                    currentY = (int) ((Text) (cn.item)).getY();
                }

            } else {
                currentX = (int) (((Text) (cn.item)).getX() + charWidth((Text) cn.item));
                currentY = (int) ((Text) (cn.item)).getY();
            }
            Editor.c.updateSize();
            Editor.c.updatePosition(currentX, currentY);
        }
    }

    private SuperLink.Node closestNode(SuperLink.Node start, SuperLink.Node end, int knownX) {
        int min;
        if (start == charSoFar.getNode(0).prev) {
            min = (int) ((Text) (start.next.item)).getX() - knownX;
        }else {
            min = (int) ((Text) (start.item)).getX() - knownX;
        }
        min = Math.abs(min);
        SuperLink.Node result = start;
        while (start != end.next){
            start = start.next;
            int temp;
            if (start == charSoFar.getNode(0).prev){
                temp = (int) ((Text)(start.prev.item)).getX() + charWidth((Text) (start.prev.item)) - knownX;

            }else {
                temp = (int) ((Text)(start.item)).getX() - knownX;
                temp = Math.abs(temp);
            }


            if (temp < min) {
                min = temp;
                result = start;
            }
        }
        return result;
    }


    /* When cursor is not visible */
    private void snapBack(){
        int currentTop = 0 - (int) g.getLayoutY();
        if (currentTop > currentY){
            int wantedLayOutY = 0 - currentY;
            int textHeight = KeyEventHandler.line.size() * KeyEventHandler.charHeightNow();
            int bottomLayoutY = (Editor.WINDOW_HEIGHT - 20 - textHeight);
            double corresValue = wantedLayOutY/(bottomLayoutY / 10.0);
            Editor.scrollBar.setValue(corresValue); // update the scrollbar?
        }else if (currentTop < currentY - (Editor.WINDOW_HEIGHT - 20)){
            int wantedLayOutY = 0 - currentY + Editor.WINDOW_HEIGHT - 20;
            int textHeight = KeyEventHandler.line.size() * KeyEventHandler.charHeightNow();
            int bottomLayoutY = (Editor.WINDOW_HEIGHT - 20 - textHeight);
            double corresValue = wantedLayOutY/(bottomLayoutY / 10.0);
            Editor.scrollBar.setValue(corresValue);

        }
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        snapBack();
        if (keyEvent.isShortcutDown()) {
            if (keyEvent.getCode() == KeyCode.S) {
                FileSaving file = new FileSaving(Editor.FILENAME);
                System.out.println("Saved.");
            } else if (keyEvent.getCode() == KeyCode.PLUS || keyEvent.getCode() == KeyCode.EQUALS) {
                fontSize += 2;
                render();

            } else if (keyEvent.getCode() == KeyCode.MINUS) {
                fontSize -= 2;
                if (fontSize < 2) {
                    fontSize = 2;
                }
                render();

            } else if (keyEvent.getCode() == KeyCode.Z) { //undo
                track.undoMove();
                render();

            } else if (keyEvent.getCode() == KeyCode.Y) { //redo
                track.redoMove();
                render();

            }

        } else {

            if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
                /* Use the KEY_TYPED event rather than KEY_PRESSED for letter keys, because with
                   the KEY_TYPED event Javafx handles the "Shift" key and associated
                   capitalization. */
                String characterTyped = keyEvent.getCharacter();

                if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                    /* Ignore control keys, which have non-zero length, as well as the backspace key, which is
                       represented as a character of value = 8 on Windows. */
                    Text current;

                    if (characterTyped.charAt(0) == '\r') {
                        current = new Text("\n");
                    } else {
                        current = new Text(characterTyped);
                    }

                    charSoFar.addChar(current);
                    track.add(charSoFar.getCurrentNode());

                    g.getChildren().add(current);
                    render();
                    keyEvent.consume();
                }

            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                /* Arrow keys should be processed using the KEY_PRESSED event, because KEY_PRESSED
                  events have a code that we can check (KEY_TYPED events don't have an associated
                  KeyCode). */
                KeyCode code = keyEvent.getCode();

                if (code == KeyCode.BACK_SPACE) {
                    if (charSoFar.isAtSentinel() != true) {
                        track.add(charSoFar.getCurrentNode());
                        Text removed = (Text) charSoFar.deleteChar();
                        g.getChildren().remove(removed);
                        render();
                        keyEvent.consume();
                    }
                }


                int lineNum = (currentY - Editor.textStartY) / KeyEventHandler.charHeightNow();

                if (code == KeyCode.UP) {
                    if (lineNum != 0) {
                        SuperLink.Node startN = (SuperLink.Node) line.get(lineNum - 1);

                        SuperLink.Node endN;
                        if (lineNum < line.size() - 1) {
                            endN = ((SuperLink.Node) line.get(lineNum)).prev;
                        } else {

                            endN = charSoFar.getNode(0).prev.prev;

                        }
                        SuperLink.Node n = closestNode(startN, endN, currentX);

                        charSoFar.setCurrentNode(n.prev);

                        render();
                        SuperLink.Node cn = charSoFar.getCurrentNode();
                        if (cn != charSoFar.getNode(0).prev && cn.next != charSoFar.getNode(0).prev){
                            if (((Text) (cn.item)).getText().equals("\n") || ((Text) (cn.item)).getText().equals(" ")) {
                                if (charSoFar.hasNext() && (int) ((Text) (cn.next.item)).getX() == Editor.textStartX) {
                                    currentX = (int) ((Text) (cn.next.item)).getX();
                                    currentY = (int) ((Text) (cn.next.item)).getY();
                                } else {
                                    currentX = (int) ((Text) (cn.item)).getX();
                                    currentX = (int) ((Text) (cn.item)).getX();
                                }
                            }
                        }
                        Editor.c.updateSize();
                        Editor.c.updatePosition(currentX, currentY);

                    }else{
                        return;
                    }

                } else if (code == KeyCode.DOWN) {

                    if (lineNum < line.size() - 1) {

                        SuperLink.Node startN = (SuperLink.Node) line.get(lineNum + 1);

                        SuperLink.Node endN;
                        if (lineNum < line.size() - 2) {
                            endN = ((SuperLink.Node) line.get(lineNum + 2)).prev;
                        } else {
                            endN = charSoFar.getNode(0).prev.prev;
                        }

                        SuperLink.Node n = closestNode(startN, endN, currentX);

                        charSoFar.setCurrentNode(n.prev);
                        render();

                        SuperLink.Node cn = charSoFar.getCurrentNode();
                        if (((Text) (cn.item)).getText().equals("\n") || ((Text) (cn.item)).getText().equals(" ")) {
                            if (charSoFar.hasNext() && (int) ((Text) (cn.next.item)).getX() == Editor.textStartX) {
                                currentX = (int) ((Text) (cn.next.item)).getX();
                                currentY = (int) ((Text) (cn.next.item)).getY();
                            } else {
                                currentX = (int) ((Text) (cn.item)).getX();
                                currentX = (int) ((Text) (cn.item)).getX();
                            }
                        }
                        Editor.c.updateSize();
                        Editor.c.updatePosition(currentX, currentY);


                    }else{
                        return;
                    }

                } else if (code == KeyCode.LEFT) {


                    if (!charSoFar.isAtSentinel()) {
                        /* Update the cursor position */
                        charSoFar.setCurrentNode(charSoFar.getCurrentNode().prev);
                        render();
                    } else {
                        return;

                    }


                } else if (code == KeyCode.RIGHT){
                    /* Consider what happens when it is at the last location at current line */
                    if (charSoFar.hasNext()) {
                        charSoFar.setCurrentNode(charSoFar.getCurrentNode().next);
                        render();
                    }else {
                        return;
                    }
                }

                if (!line.isEmpty() && line.size() * charHeightNow() > Editor.WINDOW_HEIGHT - 30) {
                    Editor.scrollBarMax = 5;
                    Editor.scrollBar.setMax(Editor.scrollBarMax);
                }
                keyEvent.consume();
            }
        }
    }
}
