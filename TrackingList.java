package TextEditor;

import javafx.scene.Node;

class TrackingList<Item> {
    private Item[] items;
    private int size;
    private int currentPos;
    private int countNewUndoMove = 0;

    TrackingList() {
        size = 0;
        currentPos = 0;
        items = (Item[]) new Object[100];
    }


    /* This modulo helper function is borrowed from stackoverflow:
       http://stackoverflow.com/questions/90238/whats-the-syntax-for-mod-in-java */
    private int mod(int x, int y){
        int result = x % y;
        if (result < 0){
            result += y;
        }
        return result;
    }

    /* This list does not remove,it only adds
       if a character is typed in or deleted, that node is going to be added here */
    void add(Item x) {
        items[currentPos] = x;
        currentPos = mod(currentPos + 1, 100);
        size += 1;
        if (size > 100) {
            size = 100;
        }
        countNewUndoMove = 0;
    }

    void undoMove(){
        if (!isEmpty() && currentPos > 0) {
            currentPos = mod(currentPos - 1, 100);
            Item target = items[currentPos];
            /* Undo */
            if (((SuperLink.Node) target).next.prev == target) {
                ((SuperLink.Node) target).prev.next = (((SuperLink.Node) target).next);
                ((SuperLink.Node) target).next.prev = (((SuperLink.Node) target).prev);
                if (KeyEventHandler.charSoFar.getCurrentNode() == target) {
                    KeyEventHandler.charSoFar.setCurrentNode(((SuperLink.Node) target).prev);
                }
                KeyEventHandler.charSoFar.size -= 1;
                KeyEventHandler.g.getChildren().remove(((SuperLink.Node) target).item);
            } else {
                ((SuperLink.Node) target).prev.next = (((SuperLink.Node) target));
                ((SuperLink.Node) target).next.prev = (((SuperLink.Node) target));

                KeyEventHandler.charSoFar.setCurrentNode((SuperLink.Node) target);

                KeyEventHandler.charSoFar.size += 1;
                KeyEventHandler.g.getChildren().add((Node) ((SuperLink.Node) target).item);

            }
            countNewUndoMove += 1;
        }
    }

    void redoMove(){
        if (!isEmpty() && items[currentPos] != null && countNewUndoMove != 0){
            Item target = items[currentPos];
            /* Redo */
            if (((SuperLink.Node) target).next.prev == target){
                ((SuperLink.Node) target).prev.next = ((SuperLink.Node) target).next;
                ((SuperLink.Node) target).next.prev = ((SuperLink.Node) target).prev;
                if (KeyEventHandler.charSoFar.getCurrentNode() == target){
                    KeyEventHandler.charSoFar.setCurrentNode(((SuperLink.Node) target).prev);
                }
                KeyEventHandler.charSoFar.size -= 1;
                KeyEventHandler.g.getChildren().remove(((SuperLink.Node) target).item);

            }
            else{
                ((SuperLink.Node) target).prev.next = ((SuperLink.Node) target);
                ((SuperLink.Node) target).next.prev = ((SuperLink.Node) target);
                KeyEventHandler.charSoFar.setCurrentNode((SuperLink.Node) target);
                KeyEventHandler.charSoFar.size += 1;
                KeyEventHandler.g.getChildren().add((Node) ((SuperLink.Node) target).item);
            }
            currentPos = mod(currentPos + 1, 100);
            countNewUndoMove -= 1;
        }
    }

    private boolean isEmpty(){
        return size == 0;
    }

}
