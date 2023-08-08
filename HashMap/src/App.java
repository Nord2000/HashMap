import java.util.Iterator;
import java.util.NoSuchElementException;

public class HashMap<K, V> implements Iterable<HashMap.Entry<K, V>> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75;

    private Entry<K, V>[] buckets;
    private int size;
    private int capacity;
    private float loadFactor;

    public HashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public HashMap(int capacity, float loadFactor) {
        this.capacity = capacity;
        this.loadFactor = loadFactor;
        this.buckets = (Entry<K, V>[]) new Entry[capacity];
    }

    public void put(K key, V value) {
        int index = getIndex(key);
        Entry<K, V> entry = buckets[index];

        while (entry != null) {
            if (keyEquals(entry.key, key)) {
                entry.value = value;
                return;
            }
            entry = entry.next;
        }

        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = buckets[index];
        buckets[index] = newEntry;
        size++;

        if (size >= capacity * loadFactor) {
            resize();
        }
    }

    public V get(K key) {
        int index = getIndex(key);
        Entry<K, V> entry = buckets[index];

        while (entry != null) {
            if (keyEquals(entry.key, key)) {
                return entry.value;
            }
            entry = entry.next;
        }

        return null;
    }

    public void remove(K key) {
        int index = getIndex(key);
        Entry<K, V> entry = buckets[index];
        Entry<K, V> prev = null;

        while (entry != null) {
            if (keyEquals(entry.key, key)) {
                if (prev == null) {
                    buckets[index] = entry.next;
                } else {
                    prev.next = entry.next;
                }
                size--;
                return;
            }
            prev = entry;
            entry = entry.next;
        }
    }

    private int getIndex(K key) {
        return key.hashCode() % capacity;
    }

    private boolean keyEquals(K key1, K key2) {
        return key1 == null ? key2 == null : key1.equals(key2);
    }

    private void resize() {
        capacity *= 2;
        Entry<K, V>[] newBuckets = (Entry<K, V>[]) new Entry[capacity];

        for (Entry<K, V> entry : buckets) {
            while (entry != null) {
                Entry<K, V> next = entry.next;
                int newIndex = getIndex(entry.key);
                entry.next = newBuckets[newIndex];
                newBuckets[newIndex] = entry;
                entry = next;
            }
        }

        buckets = newBuckets;
    }

    @Override
    public Iterator<HashMap.Entry<K, V>> iterator() {
        return new HashMapIterator();
    }

    public class Entry<K, V> {
        private K key;
        private V value;
        private Entry<K, V> next;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public Entry<K, V> getNext() {
            return next;
        }
    }

    private class HashMapIterator implements Iterator<Entry<K, V>> {
        private int index;
        private Entry<K, V> currentEntry;
        
        public HashMapIterator() {
            this.index = 0;
            this.currentEntry = null;
            findNextEntry();
        }

        @Override
        public boolean hasNext() {
            return currentEntry != null;
        }

        @Override
        public Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Entry<K, V> entry = currentEntry;
            currentEntry = currentEntry.next;

            if (currentEntry == null) {
                findNextEntry();
            }

            return entry;
        }
    }
}
