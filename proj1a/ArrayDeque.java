public class ArrayDeque<T> {

    private int nextFirst;
    private int nextLast;
    private int capacity;
    private T[]items;
    private int size;
    public ArrayDeque(){
        items = (T[]) new Object[8];
        capacity = items.length;
        nextFirst = capacity - 1;
        nextLast = 0;
        size = 0;
    }
    private void resize(int capacity) {
        T[] des = (T[]) new Object[capacity];
        //由于nextFirst和nextLast的位置不确定，只能一个一个地复制到新的数组中
        //从nextFirst右边的第一个点开始复制
        //到nextLast左边的第一个点复制结束
        for (int i = 1; i <= size; i++)
            des[i] = items[(1 + nextFirst) % capacity];
        this.capacity = capacity;
        //这两个指针指向什么地方已经不重要了
        nextFirst = 0;
        nextLast = size + 1;
        items = des;
    }
    public void addFirst(T item) {
        //直接当size等于capacity时调整大小，而不是看两个指针的相对位置
        if (size == capacity)
            resize(capacity * 2);
        items[nextFirst] = item;
        size += 1;
        //nextFirst有可能越界
        nextFirst = nextFirst==0?capacity-1:nextFirst-1;
    }

    public void addLast(T item) {
        if (size == capacity)
            resize(capacity * 2);
        items[nextLast] = item;
        size++;
        //nextLast有可能越界
        nextLast = (nextLast + 1) % capacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        //nextFirst有可能指向最后一个位置
        for (int i = (nextFirst + 1) % capacity; i != nextLast - 1; i = (i + 1) % capacity)
            System.out.print(items[i] + " ");
        System.out.print(items[nextLast - 1]);
    }

    public T removeFirst() {
        //当数组的内容为空的时候，才无法进行remove操作，而不是取决于nextFirst的位置。
        if (size == 0) {
            return null;
        }
        nextFirst = (nextFirst + 1) % capacity;
        T returnItem = items[nextFirst];
        items[nextFirst] = null;
        size -= 1;
        if (capacity >= 16 && size < capacity / 4)
            resize(capacity / 2);
        return returnItem;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        nextLast = nextLast ==0?capacity-1:nextLast-1;
        T returnItem = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        if (capacity >= 16 && size < capacity / 4)
            resize(capacity / 2);
        return returnItem;
    }

    public T get(int index) {
        if (index >= size)
            return null;
        return items[(nextFirst + 1 + index) % capacity];
    }
}

