/**
 * Deque (usually pronounced like “deck”) is an irregular acronym of double-ended queue.
 * Double-ended queues are sequence containers with dynamic sizes
 * that can be expanded or contracted on both ends (either its front or its back).
 */

public class LinkedListDeque<T> {

    private class ItemNode {
        private ItemNode prev;
        private T item;
        private ItemNode next;

        public ItemNode(ItemNode p, T i, ItemNode n) {
            prev = p;
            item = i;
            next = n;
        }
    }

    private int size;
    private ItemNode sentinel;

    /** Creates an empty linked list deque. */
    public LinkedListDeque() {
        size = 0;
        sentinel = new ItemNode(sentinel, null, sentinel);
    }

    /** Adds an item of type T to the front of the deque */
    public void addFirst(T item) {
        sentinel.next = new ItemNode(sentinel, item, sentinel.next);
        sentinel.prev = sentinel.next;        // update the original 1st node's prev
        size += 1;
    }

    /** Adds an item of type T to the back of the deque.
     * must not use loop or recursion! */
    public void addLast(T item) {
        ItemNode curLast = sentinel.prev;
        sentinel.prev = new ItemNode(curLast, item, sentinel);
        curLast.next = sentinel.prev;
        size += 1;
    }

    /** Returns true if deque is empty, false otherwise. */
    public boolean isEmpty() {
        return sentinel.next == null;
    }

    /** Returns the number of items in the deque. */
    public int size() {
        return size;
    }

    /** Prints the items in the deque from first to last, separated by a space. */
    public void printDeque() {
        ItemNode p = sentinel;
        for (int i = 0; i < size; i += 1) {
            p = p.next;
            System.out.print(p.item);
            System.out.print(' ');

        }
    }

    /** Removes and returns the item at the front of the deque.
     * If no such item exists, returns null. */
    public T removeFirst() {
        if (sentinel.next.equals(sentinel)) {
            return null;
        }
        ItemNode front = sentinel.next;
        sentinel.next = front.next;
        front.prev = sentinel;

        size -= 1;
        return front.item;
    }

    /** Removes and returns the item at the back of the deque.
     * If no such item exists, returns null.*/
    public T removeLast() {
        if (sentinel.prev.equals(sentinel)) {
            return null;
        }
        ItemNode back = sentinel.prev;
        sentinel.prev = back.prev;
        back.next = sentinel;

        size -= 1;
        return back.item;
    }


    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth
     * If no such item exists, returns null. Must not alter the deque!
     * Must use iteration.
      */
    public T get(int index) {
        int i;
        ItemNode p = sentinel;

        for (i = 0; i < index; i += 1) {
            if (p.next.equals(sentinel)) {      // if P is the last item
                return null;
            }
            p = p.next;
        }

        return p.item;
    }

    /** helper function that creates a pointer P */
    private T getRecurHelper(ItemNode p, int index) {
        if (p.next.equals(sentinel)) {          // handles index out of range
            return null;
        } else if (index == 0) {
            return p.next.item;
        }
        return getRecurHelper(p.next, index - 1);
    }

    /** Same as GET() but uses recursion. */
    public T getRecursive(int index) {
        return getRecurHelper(sentinel, index - 1);
    }
}
