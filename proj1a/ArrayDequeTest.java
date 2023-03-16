public class ArrayDequeTest {

    /* Utility method for printing out empty checks. */
    public static boolean checkEmpty(boolean expected, boolean actual) {
        if (expected != actual) {
            System.out.println("isEmpty() returned " + actual + ", but expected: " + expected);
            return false;
        }
        return true;
    }

    /* Utility method for printing out empty checks. */
    public static boolean checkSize(int expected, int actual) {
        if (expected != actual) {
            System.out.println("size() returned " + actual + ", but expected: " + expected);
            return false;
        }
        return true;
    }

    private static boolean checkGet(String expected, String actual) {
        if (!expected.equals(actual)) {
            System.out.println("size() returned <" + actual + ">, but expected: <" + expected);
            return false;
        }
        return true;
    }
    /* Prints a nice message based on whether a test passed.
     * The \n means newline. */
    public static void printTestStatus(boolean passed) {
        if (passed) {
            System.out.println("\nTest passed!\n");
        } else {
            System.out.println("\nTest failed!\n");
        }
    }

    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     * && is the "and" operation. */
    public static void addIsEmptySizeTest() {
        System.out.println("Running add/isEmpty/Size test.");


        ArrayDeque<String> ad1 = new ArrayDeque<String>();

        boolean passed = checkEmpty(true, ad1.isEmpty());

        ad1.addFirst("0");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        passed = checkSize(1, ad1.size()) && passed;
        passed = checkEmpty(false, ad1.isEmpty()) && passed;

        ad1.addLast("1");
        passed = checkSize(2, ad1.size()) && passed;

        ad1.addLast("2");
        passed = checkSize(3, ad1.size()) && passed;

        //my test: test AD.addFirst() and AD.addLast()
        for (int i = 0; i < 100; i += 1) {
            ad1.addFirst("3");
        }
        passed = checkSize(103, ad1.size()) && passed;
        passed = checkGet("1", ad1.get(0)) && passed;

        //myTest2: test removeFirst() and removeLast()
        ArrayDeque<Integer> ad2 = new ArrayDeque<Integer>();
        int i;
        for (i = 0; i < 17; i += 1) {
            ad2.addLast(i);
        } // 0 1 2 3 4 5 6 7 8 0 0...
        int first = ad2.removeFirst();
        int last = ad2.removeLast();

        System.out.println("Printing out deque2: ");
        ad2.printDeque();
        System.out.println("\nPrinting out first: ");
        System.out.println(first);
        System.out.println("\nPrinting out last: ");
        System.out.println(last);

        int n;
        for (n = 0; n < 17; n += 1) {
            ad2.addLast(n);
        } // 0 1 2 3 4 5 6 7 8 0 0...
        first = ad2.removeFirst();
        last = ad2.removeLast();

        System.out.println("Printing out deque2: ");
        ad2.printDeque();
        System.out.println("\nPrinting out first: ");
        System.out.println(first);
        System.out.println("\nPrinting out last: ");
        System.out.println(last);

        System.out.println("Printing out deque1: ");
        ad1.printDeque();

        printTestStatus(passed);

    }

    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public static void addRemoveTest() {

        System.out.println("Running add/remove test.");


        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        // should be empty
        boolean passed = checkEmpty(true, ad1.isEmpty());

        ad1.addFirst(10);
        // should not be empty
        passed = checkEmpty(false, ad1.isEmpty()) && passed;

        ad1.removeFirst();
        // should be empty
        passed = checkEmpty(true, ad1.isEmpty()) && passed;

        printTestStatus(passed);

    }

    public static void main(String[] args) {
        System.out.println("Running tests.\n");
        addIsEmptySizeTest();
        addRemoveTest();
    }
}
