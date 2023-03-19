public class OffByOne implements CharacterComparator {

    /** returns true for characters that are different by exactly one
     * e.g. equalChars('e', 'f') == true
     *      equalChars('e', 'g') == false
     *      '&' - '%' == 1 */
    @Override
    public boolean equalChars(char x, char y) {
        return Math.abs(x - y) == 1;
    }
}
