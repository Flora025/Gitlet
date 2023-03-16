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


        ArrayDeque1<String> ad1 = new ArrayDeque1<String>();

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
        for (int i = 0; i < 10; i += 1) {
            ad1.addLast("3");
        }
        passed = checkSize(13, ad1.size()) && passed;
        //passed = checkGet("1", ad1.get(0)) && passed;

        System.out.println("\nPrinting out deque1: ");
        ad1.printDeque();

        //myTest2: test addFirst() and removeLast()
        ArrayDeque1<Integer> ad2 = new ArrayDeque1<Integer>();
        int i;
        for (i = 0; i < 17; i += 1) {
            ad2.addFirst(i);

        } // [17, 16, 15 ,... ,3, 2, 1, 0]
        for (int n = 0; n < 17; n += 1) {
            ad2.removeLast();
        }

        System.out.println("\nPrinting out deque2: ");
        ad2.printDeque();

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
