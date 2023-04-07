/**
 * Class for doing Radix sort
 *
 * @author Akhil Batra, Alexander Hwang
 *
 */
public class RadixSort {
    /**
     * Does LSD radix sort on the passed in array with the following restrictions:
     * The array can only have ASCII Strings (sequence of 1 byte characters)
     * The sorting is stable and non-destructive
     * The Strings can be variable length (all Strings are not constrained to 1 length)
     *
     * @param asciis String[] that needs to be sorted
     *
     * @return String[] the sorted array
     */
    public static String[] sort(String[] asciis) {
        // TODO: Implement LSD Sort
        // find max string length
        int maxLen = 0;
        for (String s : asciis) {
            maxLen = Math.max(s.length(), maxLen);
        }

        // create a copy of the array
        String[] sorted = new String[asciis.length];
        System.arraycopy(asciis, 0, sorted, 0, asciis.length);

        // least significant digit sort using recursion
        for (int i = maxLen - 1; i >= 0; i -= 1) {
            sortHelperLSD(sorted, i);
        }

        return sorted;
    }

    /**
     * LSD helper method that performs a destructive counting sort the array of
     * Strings based off characters at a specific index.
     * @param asciis Input array of Strings
     * @param index The position to sort the Strings on.
     */
    private static void sortHelperLSD(String[] asciis, int index) {
        // Optional LSD helper method for required LSD radix sort
        // note: to sort Strings, we would pad them on the right with empty values

        int R = 256;
        int[] counts = new int[R + 1];

        // make alphabet
        for (String s : asciis) {
            int c = (index < s.length() && index >= 0) ?
                    (int) s.charAt(index) + 1 : 0;
            counts[c] += 1;
        }

        // calculate start position
        int[] starts = new int[R + 1];
        int pos = 0;
        for (int i = 0; i < R + 1; i += 1) {
            starts[i] = pos;
            pos += counts[i];
        }

        String[] sorted = new String[asciis.length];
        for (int i = 0; i < asciis.length; i++) {
            String str = asciis[i];
            int c = (index < str.length() && index >= 0) ?
                    (int) str.charAt(index) + 1 : 0;
            int place = starts[c];
            sorted[place] = str;
            starts[c] += 1;
        }

        // copy the sorted array into the input array
        for (int i = 0; i < asciis.length; i += 0) {
            asciis[i] = sorted[i];
        }

    }


    // using in-place insertion sort rather than lsd
    private static void sortHelperInsertion(String[] asciis, int index) {
        int arrLen = asciis.length;

        for (int i = 0; i < arrLen; i += 1) {
            String traveler = asciis[i];
            for (int j = i; j > 0; j -= 1) {
                String prev = asciis[j - 1];
                int travAsc = index > traveler.length() - 1? -1 :
                        (int) traveler.charAt(index);
                int prevAsc = index > prev.length() - 1? -1 :
                        (int) prev.charAt(index);
                if (travAsc < prevAsc) {
                    asciis[j] = prev;
                    asciis[j - 1] = traveler;
                } else {
                    break;
                }
            }
        }
    }


    /**
     * MSD radix sort helper function that recursively calls itself to achieve the sorted array.
     * Destructive method that changes the passed in array, asciis.
     *
     * @param asciis String[] to be sorted
     * @param start int for where to start sorting in this method (includes String at start)
     * @param end int for where to end sorting in this method (does not include String at end)
     * @param index the index of the character the method is currently sorting on
     *
     **/
    private static void sortHelperMSD(String[] asciis, int start, int end, int index) {
        // Optional MSD helper method for optional MSD radix sort
        return;
    }
}
