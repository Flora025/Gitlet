public class ArrayDeque<T> {

    private int size;
    private int nextFirst;
    private int nextLast;
    private T[] items;
    private double R;


    /** Creates an empty linked list deque. */
    public ArrayDeque() {
        size = 0;
        items = (T[]) new Object[8];
        nextFirst = 0;
        nextLast = 0;
    }

    /** moves one position front */
    private int moveOneFront(int nextWhat) {
        if (nextWhat == 0) {
            return items.length - 1;
        } else {
            return nextWhat - 1;
        }
    }

    /** moves one position back */
    private int moveOneBack(int nextWhat) {
        nextWhat %= items.length;
        if (nextWhat == items.length - 1) {
            return 0;
        } else {
            return nextWhat + 1;
        }
    }

    /** Adds an item of type T to the front of the deque */
    public void addFirst(T item) {
        ifFullThenResize(size * 2);
        nextFirst = moveOneFront(nextFirst);
        items[nextFirst] = item;
        size += 1;
    }

    /** Adds an item of type T to the back of the deque.
     * must not use loop or recursion! */
    public void addLast(T item) {
        ifFullThenResize(size * 2);
        items[nextLast] = item;
        nextLast = moveOneBack(nextLast);
        size += 1;
    }

    /** Returns true if deque is empty, false otherwise. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns the number of items in the deque. */
    public int size() {
        return size;
    }

    /** Prints the items in the deque from first to last, separated by a space. */
    public void printDeque() {
        for (T item: items) {
            if (item != null) {
                System.out.print(item);
                System.out.print(' ');
            }
        }
    }

    /** Removes and returns the item at the front of the deque.
     * If no such item exists, returns null. */
    public T removeFirst() {
        //saveMemory();
        if (isEmpty()) {
            return null;
        }
        T returnItem = items[nextFirst];
        nextFirst = moveOneBack(nextFirst);
        size -= 1;
        return returnItem;

    }

    /** Removes and returns the item at the back of the deque.
     * If no such item exists, returns null.*/
    public T removeLast() {
        //saveMemory();
        if (isEmpty()) {
            return null;
        }

        nextLast = moveOneFront(nextLast);
        size -= 1;
        return items[nextLast];
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth
     * If no such item exists, returns null. Must not alter the deque!
     * Must use iteration.
     */
    public T get(int index) {
        if (this.isEmpty()) {
            return null;
        }
        int startIndex = moveOneBack(nextFirst);
        return items[(index + startIndex) % items.length];
    }

    /** helps to judge if the array is full and initialize indexes.
     * if so, automatically implements RESIZE().
     * to use: ifFullThenResize(size * 2);
     */
    private void ifFullThenResize(int capacity) {
        if (size == items.length) {
            resize(capacity);
            nextFirst = items.length - 1;
            nextLast = size;
        }
    }

    /** resize the array (from the front / back) */
    private void resize(int capacity) {
        T[] des = (T[]) new Object[capacity];
        System.arraycopy(items, 0, des, 0, size);
        items = des;
    }

    private void saveMemory() {
        // calculate usage ratio
        R = Math.round(size * 100 / items.length) / 100.0;
        // 4 5 6 0 0 0
        if (items.length >= 16 && R < 0.25) {
            T[] des = (T[]) new Object[items.length / 2];
            System.arraycopy(items, 0, des, 0, size);
        }
    }
}
