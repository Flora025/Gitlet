import edu.princeton.cs.algs4.Queue;
import org.junit.Test;

import java.util.Collections;

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
        for (Item item : items) {
            Queue<Item> tmpQ = new Queue<>();
            // turn the items into a single queue
            tmpQ.enqueue(item);
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
        // merge sort the queues Q1 and Q2
        while (!(q1.isEmpty() && q2.isEmpty())) {
            retQueue.enqueue(getMin(q1, q2));
        }
        return retQueue;

    }


    /** Returns a Queue that contains the given items sorted from least to greatest. */
    public static <Item extends Comparable> Queue<Item> mergeSort(
            Queue<Item> items) {
        // Your code here!
        // make ((1), (2), (3), (4))
        if (items.isEmpty()) {
            return items;
        }
        Queue<Queue<Item>> singleQQ = makeSingleItemQueues(items);


        while (singleQQ.size() != 1) {
            Queue<Item> queue1 = singleQQ.dequeue();
            Queue<Item> queue2 = singleQQ.isEmpty() ? new Queue<>() : singleQQ.dequeue();
            // mergesort the first two single queue
            // and enqueue it again
            singleQQ.enqueue(mergeSortedQueues(queue1, queue2));
        }

        return singleQQ.dequeue();

    }

    @Test
    public void main() {
        Queue<String> students = new Queue<String>();
        students.enqueue("Alice");
        students.enqueue("Zlice");
        students.enqueue("Vanessa");
        students.enqueue("Ethan");
        students.enqueue("Dthan");

        Queue studentsS = MergeSort.mergeSort(students);

        System.out.println(students);
        System.out.println(studentsS);

        Queue<String> qNone = new Queue<String>();

        Queue qNoneS = MergeSort.mergeSort(qNone);
        System.out.println("qNone: " + qNone);
        System.out.println("qNone Sorted: " + qNoneS);

    }
}
