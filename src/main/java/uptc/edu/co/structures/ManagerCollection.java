package uptc.edu.co.structures;

public class ManagerCollection<T> {
    private final DoubleList<T> data;
    private CollectionMode mode;

    public ManagerCollection(CollectionMode mode) {
        this.data = new DoubleList<T>();
        this.mode = mode;
    }

    public void add(T element) {
        data.addEnd(element);
    }

    public T remove() {
        if (CollectionMode.STACK == mode) {
            return data.removeEnd();
        }
        return data.removeFirst();
    }

    public T removeLast() {
        return data.removeEnd();
    }

    public T removeAt(int index) {
        return data.removeAt(index);
    }

    public T get(int index) {
        return data.get(index);
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public DoubleList<T> getAll() {
        return data.copy();
    }

    public DoubleList<T> getOrdered() {
        if (CollectionMode.QUEUE == mode) {
            return data.copy();
        }
        return data.reverseCopy();
    }

    public CollectionMode getMode() {
        return mode;
    }

    public void setMode(CollectionMode mode) {
        this.mode = mode;
    }
}
