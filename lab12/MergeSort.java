import edu.princeton.cs.algs4.Queue;
import org.junit.Test;

public class MergeSort {
    /**
     * Removes and returns the smallest item that is in q1 or q2.
     *
     * The method assumes that both q1 and q2 are in sorted order, with the smallest item first. At
     * most one of q1 or q2 can be empty (but both cannot be empty).
     *
     * @param   q1  A Queue in sorted order from least to greatest.
     * @param   q2  A Queue in sorted order from least to greatest.
     * @return      The smallest item that is in q1 or q2.
     */
    private static <Item extends Comparable> Item getMin(
            Queue<Item> q1, Queue<Item> q2) {
        if (q1.isEmpty()) {
            return q2.dequeue();
        } else if (q2.isEmpty()) {
            return q1.dequeue();
        } else {
            // Peek at the minimum item in each queue (which will be at the front, since the
            // queues are sorted) to determine which is smaller.
            Comparable q1Min = q1.peek();
            Comparable q2Min = q2.peek();
            if (q1Min.compareTo(q2Min) <= 0) {
                // Make sure to call dequeue, so that the minimum item gets removed.
                return q1.dequeue();
            } else {
                return q2.dequeue();
            }
        }
    }

    /** Returns a queue of queues that each contain one item from items. */
    private static <Item extends Comparable> Queue<Queue<Item>>
            makeSingleItemQueues(Queue<Item> items) {
        // Your code here!
        Queue<Queue<Item>> returnQ = new Queue<>();
        int size = items.size();
        for (int i = 0; i < size; i += 1) {
            Queue<Item> tmpQ = new Queue<>();
            // turn the items into a single queue
            tmpQ.enqueue(items.dequeue());
            returnQ.enqueue(tmpQ);
        }
        return returnQ;
    }

    /**
     * Returns a new queue that contains the items in q1 and q2 in sorted order.
     *
     * This method should take time linear in the total number of items in q1 and q2.  After
     * running this method, q1 and q2 will be empty, and all of their items will be in the
     * returned queue.
     *
     * @param   q1  A Queue in sorted order from least to greatest.
     * @param   q2  A Queue in sorted order from least to greatest.
     * @return      A Queue containing all of the q1 and q2 in sorted order, from least to
     *              greatest.
     *
     */
    private static <Item extends Comparable> Queue<Item> mergeSortedQueues(
            Queue<Item> q1, Queue<Item> q2) {
        // create a Q
        Queue<Item> retQueue = new Queue<>();

        Item item1 = q1.dequeue();
        Item item2 = q2.dequeue();
        Queue whichEmpty;
        // merge sort the queues Q1 and Q2
        while (!q1.isEmpty() || !q2.isEmpty()) {
            if (item1.compareTo(item2) < 0 || q2.isEmpty()) {
                retQueue.enqueue(item1);
                if (q1.isEmpty()) {
                    whichEmpty = q1;
                    break;
                } else {
                    item1 = q1.dequeue();
                }
            } else {
                retQueue.enqueue(item2);
                if (q2.isEmpty()) {
                    whichEmpty = q2;
                    break;
                } else {
                    item2 = q2.dequeue();
                }
            }
        }

        return retQueue;
    }


    /** Returns a Queue that contains the given items sorted from least to greatest. */
    public static <Item extends Comparable> Queue<Item> mergeSort(
            Queue<Item> items) {
        // Your code here!
        return items;
    }

    @Test
    public void main() {
//        Queue<String> students = new Queue<String>();
//        students.enqueue("Alice");
//        students.enqueue("Vanessa");
//        students.enqueue("Ethan");
//
//        Queue studentsS = MergeSort.mergeSort(students);
//
//        System.out.println(students);
//        System.out.println(studentsS);
        Queue<Integer> numbers1 = new Queue<>();
        Queue<Integer> numbers2 = new Queue<>();
        numbers1.enqueue(0);
        numbers1.enqueue(2);
        numbers1.enqueue(4);

        numbers2.enqueue(1);
        numbers2.enqueue(3);
        numbers2.enqueue(5);
        numbers2.enqueue(7);

        Queue<Integer> mSorted = MergeSort.mergeSortedQueues(numbers1, numbers2);
        System.out.println(mSorted);


    }
}
