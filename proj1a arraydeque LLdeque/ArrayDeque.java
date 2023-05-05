
public class ArrayDeque<T> implements Deque<T> {

    private int size;
    private int nextFirst;
    private int nextLast;
    private T[] items;
    private int length;


    /** Creates an empty linked list deque. */
    public ArrayDeque() {
        size = 0;
        items = (T[]) new Object[8];
        nextFirst = items.length - 1;
        nextLast = 0;
        length = items.length;
    }

    /** resize the array (from the front / back)
     * IMPORTANT: have to sort the array (into its natural sequence)
     * while resizing.
     */
    private void resize(int capacity) {
        T[] des = (T[]) new Object[capacity];
        for (int i = 1; i <= size; i += 1) {
            des[i] = items[(++nextFirst) % length];
            //IMPORTANT: have to sort the array (into its natural sequence)
        }
        length = capacity;
        nextFirst = 0;
        nextLast = size + 1;
        items = des;
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
    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }

        items[nextFirst] = item;
        nextFirst = moveOneFront(nextFirst);
        size += 1;
    }

    /** Adds an item of type T to the back of the deque.
     * must not use loop or recursion! */
    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }

        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length; // 改动 not belonging tome
        size += 1;
    }

    /** Returns true if deque is empty, false otherwise. */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns the number of items in the deque. */
    @Override
    public int size() {
        return size;
    }

    /** Prints the items in the deque from first to last, separated by a space. */
    @Override
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
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        nextFirst = (nextFirst + 1) % items.length;
        T returnItem = items[nextFirst];
        items[nextFirst] = null;
        size -= 1;
        if (items.length >= 16 && items.length / size > 4) {    //IMPORTANT!!
            resize(items.length / 2);
        }
        return returnItem;
    }

    /** Removes and returns the item at the back of the deque.
     * If no such item exists, returns null.*/
    @Override
    public T removeLast() {

        if (isEmpty()) {
            return null;
        }

        nextLast = moveOneFront(nextLast);
        T returnItem = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        // save memp
        if (items.length >= 16 && items.length / size > 4) {
            resize(items.length / 2);
        }
        return returnItem;
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth
     * If no such item exists, returns null. Must not alter the deque!
     * Must use iteration.
     */
    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        return items[(index + nextFirst + 1) % items.length];
    }

}

