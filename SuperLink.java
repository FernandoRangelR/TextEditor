package TextEditor;

class SuperLink<Item> {
    private Node sentinel;
    private Node currentNode;
    int size;

    class Node {
        Item item;
        Node next;
        Node prev;

        Node(Item i, Node n){
            item = i;
            next = n;
            n.prev = this;
        }

        Node(Item i){
            item = i;
        }

    }

    SuperLink(){
        sentinel = new Node(null);
        currentNode = sentinel;
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    Node getCurrentNode(){
        return currentNode;
    }

    void setCurrentNode(Node n){
        currentNode = n;
    }

    boolean hasNext(){
        return currentNode.next != sentinel;
    }

    boolean isAtSentinel(){
        return currentNode == sentinel;
    }

    int size(){
        return size;
    }

    boolean isEmpty(){
        return size() == 0;
    }

    void addChar(Item x) {
        currentNode.next = new Node(x, currentNode.next);
        currentNode.next.prev = currentNode;
        currentNode = currentNode.next;
        size += 1;
    }

    Item deleteChar() {
        if (currentNode == sentinel) {
            return null;
        } else {
            Item deleted = currentNode.item;
            currentNode = currentNode.prev;
            currentNode.next = currentNode.next.next;
            currentNode.next.prev = currentNode;
            size -= 1;

            return deleted;
        }
    }

    Node getNode(int index){
        if (sentinel.next == sentinel){
            return null;
        } else {
            Node temp = sentinel.next;
            while (index > 0){
                if (temp.next == sentinel){
                    return null;
                }
                temp = temp.next;
                index -= 1;
            }
            return temp;
        }
    }
}
