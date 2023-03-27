package lab9;

import edu.princeton.cs.algs4.SET;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

/**
 * Implementation of interface Map61B with BST as core data structure.
 *
 * @author Flora
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class Node {
        /* (K, V) pair stored in this Node. */
        private K key;
        private V value;

        /* Children of this Node. */
        private Node left;
        private Node right;

        private Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    private Node root;  /* Root node of the tree. */
    private int size; /* The number of key-value pairs in the tree */


    /* Creates an empty BSTMap. */
    public BSTMap() {
        this.clear();
    }

    /* Removes all of the mappings from this map. */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /** Returns the value mapped to by KEY in the subtree rooted in P.
     *  or null if this map contains no mapping for the key.
     */
    // TODO: done
    private V getHelper(K key, Node p) {
        // base case
        if (p == null) {
            return null;
        }

        if (key.compareTo(p.key) < 0) {
            return getHelper(key, p.left);
        } else if (key.compareTo(p.key) > 0) {
            return getHelper(key, p.right);
        }
        return p.value;
    }

    /** Returns the value to which the specified key is mapped, or null if this
     *  map contains no mapping for the key.
     */
    // TODO: done
    @Override
    public V get(K key) {
        return getHelper(key, root);
    }

    /** Returns a BSTMap rooted in p with (KEY, VALUE) added as a key-value mapping.
      * Or if p is null, it returns a one node BSTMap containing (KEY, VALUE).
     */
    // TODO: done
    private Node putHelper(K key, V value, Node p) {
        if (p == null) {
            size += 1;
            p = new Node(key, value);
            return p;
        }

        if (key.compareTo(p.key) < 0) {
            p.left = putHelper(key, value, p.left);
        } else if (key.compareTo(p.key) > 0) {
            p.right = putHelper(key, value, p.right);
        } else {
            p.value = value;
        }
        return p;
    }

    /** Inserts the key KEY
     *  If it is already present, updates value to be VALUE.
     */
    //TODO: done
    @Override
    public void put(K key, V value) {
        root = putHelper(key, value, root);
    }

    /* Returns the number of key-value mappings in this map. */
    //TODO: done
    @Override
    public int size() {
        return size;
    }

    //////////////// EVERYTHING BELOW THIS LINE IS OPTIONAL ////////////////

    /* Returns a Set view of the keys contained in this map. */
    // TODO: done
    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        return keyHelper(keys, root);
    }
    private Set<K> keyHelper(Set<K> set, Node p) {
        if (p == null) {
            return set;
        } else {
            set.add(p.key);
            set = keyHelper(set, p.left);
            set = keyHelper(set, p.right);
        }
        return set;
    }

    //////////////// remove and removeHelpers ////////////////

    /** return the smallest Node P */
    private Node min(Node p) {
        Node tmp = p;
        if (tmp == null) {
            return null;
        } else if (tmp.left == null) {
            return tmp;
        }
        return min(tmp.left);
    }

    /** remove the smallest node in P and return updated P */
    private void removeMin() {
        root = removeMin(root);
    }

    public Node removeMin(Node p) {
        if (p.left == null) {
            return p.right;
        }
        p.left = removeMin(p.left);
        size -= 1;
        return p;
    }

    /** Removes KEY from the tree if present
     *  returns VALUE removed,
     *  null on failed removal.
     */
    @Override
    public V remove(K key) {
        rmVal = null;
        root = removeHelper(key, root);
        return rmVal;
    }


    // sth wrong with the return position
    private V rmVal = null;
    private Node removeHelper(K key, Node p) {
        if (p == null) {
            return null;
        }

        if (key.compareTo(p.key) < 0) {
            p.left = removeHelper(key, p.left);
        } else if (key.compareTo(p.key) > 0) {
            p.right = removeHelper(key, p.right);
        } else {
            // suppose we have found the key P
            // CHECK IF IT'S A LEAF OR HAS SUB-NODES
            // In case of a leaf or having only one sub-node, just return the node/null
            rmVal = p.value;
            size -= 1;
            if (p.right == null) {
                return p.left;
            }
            if (p.left == null) {
                return p.right;
            }
            // In case of two sub-nodes, have to do some tweaks:
            Node tmp = p;
            p = min(tmp.right);             // 1. find the new substitute for the deleted node P
            p.right = removeMin(tmp.right); // 2. connect the updated right nodes to the new P
            p.left = tmp.left;              // 3. connect the original left sub-nodes to the new P
        }

        return p;
    }

    /** Removes the key-value entry for the specified key only if it is
     *  currently mapped to the specified value.  Returns the VALUE removed,
     *  null on failed removal.
     **/
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
