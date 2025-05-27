import java.util.LinkedList;

public class MyHashMap<K, V> {
        private static final int INITIAL_CAPACITY = 16;

        private LinkedList<Node<K, V>>[] buckets;

        @SuppressWarnings("unchecked")
        public MyHashMap() {
            buckets = new LinkedList[INITIAL_CAPACITY];
        }

        private int getIndex(K key) {
            return Math.abs(key.hashCode()) % buckets.length;
        }

        public void put(K key, V value) {
            int index = getIndex(key);
            if (buckets[index] == null) {
                buckets[index] = new LinkedList<>();
            }

            for (Node<K, V> node : buckets[index]) {
                if (node.key.equals(key)) {
                    node.value = value;
                    return;
                }
            }

            buckets[index].add(new Node<>(key, value));
        }
        public V get(K key) {
            int index = getIndex(key);
            if (buckets[index] == null) return null;

            for (Node<K, V> node : buckets[index]) {
                if (node.key.equals(key)) return node.value;
            }
            return null;
        }

        public boolean containsKey(K key) {
            return get(key) != null;
        }

        public void remove(K key) {
            int index = getIndex(key);
            if (buckets[index] == null) return;

            buckets[index].removeIf(node -> node.key.equals(key));
        }

        private static class Node<K, V> {
            K key;
            V value;

            Node(K k, V v) {
                key = k;
                value = v;
            }
        }
    }
