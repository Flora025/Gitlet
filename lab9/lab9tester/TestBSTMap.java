package lab9tester;

import static org.junit.Assert.*;

import org.junit.Test;
import lab9.BSTMap;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Tests by Brendan Hu, Spring 2015, revised for 2018 by Josh Hug
 */
public class TestBSTMap {

    @Test
    public void sanityGenericsTest() {
        try {
            BSTMap<String, String> a = new BSTMap<String, String>();
            BSTMap<String, Integer> b = new BSTMap<String, Integer>();
            BSTMap<Integer, String> c = new BSTMap<Integer, String>();
            BSTMap<Boolean, Integer> e = new BSTMap<Boolean, Integer>();
        } catch (Exception e) {
            fail();
        }
    }

    //assumes put/size/containsKey/get work
    @Test
    public void sanityClearTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        for (int i = 0; i < 455; i++) {
            b.put("hi" + i, 1 + i);
            //make sure put is working via containsKey and get
            assertTrue(null != b.get("hi" + i));
            assertTrue(b.get("hi" + i).equals(1 + i));
            assertTrue(b.containsKey("hi" + i));
        }
        assertEquals(455, b.size());
        b.clear();
        assertEquals(0, b.size());
        for (int i = 0; i < 455; i++) {
            assertTrue(null == b.get("hi" + i) && !b.containsKey("hi" + i));
        }
    }

    // assumes put works
    @Test
    public void sanityContainsKeyTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        assertFalse(b.containsKey("waterYouDoingHere"));
        b.put("waterYouDoingHere", 0);
        assertTrue(b.containsKey("waterYouDoingHere"));
    }

    // assumes put works
    @Test
    public void sanityGetTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        assertEquals(null, b.get("starChild"));
        assertEquals(0, b.size());
        b.put("starChild", 5);
        assertTrue(((Integer) b.get("starChild")).equals(5));
        b.put("KISS", 5);
        assertTrue(((Integer) b.get("KISS")).equals(5));
        assertNotEquals(null, b.get("starChild"));
        assertEquals(2, b.size());
    }

    @Test
    public void testRandomGet() {
        BSTMap<Integer, Integer> b = new BSTMap<Integer, Integer>();
        b.put(0, 0);
        b.put(1, 1);
        b.put(4, 4);
        b.put(3, 3);
        b.put(2, 2);
        assertNull(b.get(5));
        assertEquals(0, (int) b.get(0));
        assertEquals(1, (int) b.get(1));
        assertEquals(4, (int) b.get(4));
    }

    // assumes put works
    @Test
    public void sanitySizeTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        assertEquals(0, b.size());
        b.put("hi", 1);
        assertEquals(1, b.size());
        for (int i = 0; i < 455; i++) {
            b.put("hi" + i, 1);
        }
        assertEquals(456, b.size());
    }

    //assumes get/containskey work
    @Test
    public void sanityPutTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        b.put("hi", 1);
        assertTrue(b.containsKey("hi"));
        assertTrue(b.get("hi") != null);
    }

    @Test
    public void TestKeySet() {
        BSTMap<Integer, Integer> b = new BSTMap<Integer, Integer>();
        b.put(1, 1);
        b.put(2, 2);
        b.put(4, 4);
        b.put(3, 4);
        Set<Integer> intKey = new HashSet<Integer>();
        intKey.add(1);
        intKey.add(2);
        intKey.add(4);
        intKey.add(3);
        assertEquals(b.keySet(), intKey);
    }

    @Test
    public void TestPutRemove() {
        BSTMap<Integer, Integer> b = new BSTMap<Integer, Integer>();
        assertNull(b.remove(1));
        b.put(2, 2);
        b.put(1, 1);
        b.put(4, 4);
        b.put(3, 3);
        int ret = b.remove(4);
        assertEquals(ret, 4);
        assertNull(b.remove(5));

    }

    /** random put() and remove() */
    // TODO prbs w/ remove
    @Test
    public void testRandomPut() {
        Random r = new Random();
        BSTMap<Integer, Integer> b2 = new BSTMap<Integer, Integer>();
        for (int a = 0; a < 10; a += 1) {
            int someKey = r.nextInt(0, 10);
            b2.put(someKey, someKey);
        }

        for (int a = 0; a < 5; a += 1) {
            int someKey = r.nextInt(0, 10);
            if (b2.get(someKey) != null) {
                assertEquals(someKey, (int) b2.remove(someKey));
            } else {
                assertNull(b2.remove(someKey));
            }

        }
    }

    public static void main(String[] args) {
        jh61b.junit.TestRunner.runTests(TestBSTMap.class);
    }
}
