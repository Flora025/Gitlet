import java.util.ArrayDeque;

public class Palindrome {
    /** Given a String, wordToDeque should return a Deque */
    public Deque<Character> wordToDeque(String word) {
        Deque<Character> returnD = new ArrayDeque<Character>();

        for (int i = 0; i < word.length(); i += 1) {
            Character curChar = word.charAt(i);
            returnD.addLast(curChar);
        }
        return returnD;
    }

}
