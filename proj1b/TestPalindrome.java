import org.junit.Test;
import static org.junit.Assert.*;

public class TestPalindrome {
    // You must use this palindrome, and not instantiate
    // new Palindromes, or the autograder might be upset.
    static Palindrome palindrome = new Palindrome();

    @Test
    public void testWordToDeque() {
        Deque d = palindrome.wordToDeque("persiflage");
        String actual = "";
        for (int i = 0; i < "persiflage".length(); i++) {
            actual += d.removeFirst();
        }
        assertEquals("persiflage", actual);
    }
    //Uncomment this class once you've created your Palindrome class.

    @Test
    public void testIsPalindrome() {
        String p1 = "racecar";
        String p2 = "noon";
        String p3 = "a";
        String p4 = "";

        String np1 = "like";
        String np2 = "bat";
        String np3 = "Noon";

        assertTrue(palindrome.isPalindrome(p1));
        assertTrue(palindrome.isPalindrome(p2));
        assertTrue(palindrome.isPalindrome(p3));
        assertTrue(palindrome.isPalindrome(p4));

        assertFalse(palindrome.isPalindrome(np1));
        assertFalse(palindrome.isPalindrome(np2));
        assertFalse(palindrome.isPalindrome(np3));

        System.out.println("Passed!");
    }

    @Test
    public void testIsPalindrome2() {
        String p1 = "acb";
        String p2 = "a";
        String p3 = "&a%";
        String np1 = "acz";

        CharacterComparator offByOne = new OffByOne();
        assertTrue(palindrome.isPalindrome(p1, offByOne));
        assertTrue(palindrome.isPalindrome(p2, offByOne));
        assertTrue(palindrome.isPalindrome(p3, offByOne));
        assertFalse(palindrome.isPalindrome(np1, offByOne));
    }
}
