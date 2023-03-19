// import java.util.ArrayDeque;

public class Palindrome {

    /** Given a String, wordToDeque should return a Deque */
    public Deque<Character> wordToDeque(String word) {
        Deque<Character> returnD = new ArrayDeque<>();

        for (int i = 0; i < word.length(); i += 1) {
            Character curChar = word.charAt(i);
            returnD.addLast(curChar);
        }
        return returnD;
    }

    /** return true if the given word is a palindrome, and false otherwise */
    public boolean isPalindrome(String word) {
        Deque<Character> ad = wordToDeque(word);
        if (ad.size() == 1 || ad.size() == 0) {
            return true;
        }
        while (ad.size() > 1) {
            Character front = ad.removeFirst();
            Character back = ad.removeLast();
            if (!front.equals(back)) {
                return false;
            }
        }
        return true;
    }

    /** return true if the word is a palindrome
     * according to the character comparison test provided by the CharacterComparator  */
    public boolean isPalindrome(String word, CharacterComparator cc) {
        Deque<Character> ad = wordToDeque(word);
        if (ad.size() == 1 || ad.size() == 0) {
            return true;
        }
        while (ad.size() > 1) {
            Character front = ad.removeFirst();
            Character back = ad.removeLast();
            if (!cc.equalChars(front, back)) {
                return false;
            }
        }
        return true;
    }

}
