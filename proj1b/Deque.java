public interface Deque<T> {

    /** Adds an item of type T to the front of the deque */
    public void addFirst(T item);

    public void addLast(T item);

    public boolean isEmpty();

    public int size();

    public void printDeque();

    public T removeFirst();

    public T removeLast();

    public T get(int index);

}
