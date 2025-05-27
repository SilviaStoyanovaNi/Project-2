public class MyList<T> {
        private Object[] data;
        private int size;

        public MyList() {
            data = new Object[10];
            size = 0;
        }

        public void add(T item) {
            if (size == data.length) resize();
            data[size++] = item;
        }

        @SuppressWarnings("unchecked")
        public T get(int index) {
            if (index < 0 || index >= size)
                throw new IndexOutOfBoundsException("The index is out of bound.");
            return (T) data[index];
        }

        public boolean remove(T item) {
            for (int i = 0; i < size; i++) {
                if (data[i].equals(item)) {
                    shiftLeft(i);
                    size--;
                    return true;
                }
            }
            return false;
        }

        public int size() {
            return size;
        }

        private void resize() {
            Object[] newData = new Object[data.length * 2];
            System.arraycopy(data, 0, newData, 0, size);
            data = newData;
        }

        private void shiftLeft(int index) {
            for (int i = index; i < size - 1; i++) {
                data[i] = data[i + 1];
            }
        }

        public boolean contains(T item) {
            for (int i = 0; i < size; i++) {
                if (data[i].equals(item)) return true;
            }
            return false;
        }

        public void clear() {
            data = new Object[10];
            size = 0;
        }

    public void set(int index, T item) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        data[index] = item;
    }
}

