package uptc.edu.co.structures;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoubleList<T> implements Iterable<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public void addFirst(T value) {
        Node<T> node = new Node<T>(value);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head.previous = node;
            head = node;
        }
        size++;
    }

    public void addEnd(T value) {
        Node<T> node = new Node<T>(value);
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.previous = tail;
            tail = node;
        }
        size++;
    }

    public T removeFirst() {
        if (head == null) {
            return null;
        }
        T value = head.value;
        unlinkFirstNode();
        size--;
        return value;
    }

    public T removeEnd() {
        if (tail == null) {
            return null;
        }
        T value = tail.value;
        unlinkLastNode();
        size--;
        return value;
    }

    public T removeAt(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        if (index == 0) return removeFirst();
        if (index == size - 1) return removeEnd();
        Node<T> current = nodeAt(index);
        unlinkMiddleNode(current);
        size--;
        return current.value;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return nodeAt(index).value;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public DoubleList<T> copy() {
        DoubleList<T> snapshot = new DoubleList<T>();
        for (Node<T> current = head; current != null; current = current.next) {
            snapshot.addEnd(current.value);
        }
        return snapshot;
    }

    public DoubleList<T> reverseCopy() {
        DoubleList<T> snapshot = new DoubleList<T>();
        for (Node<T> current = tail; current != null; current = current.previous) {
            snapshot.addEnd(current.value);
        }
        return snapshot;
    }

    public Iterator<T> iterator() {
        return createIterator();
    }

    private Iterator<T> createIterator() {
        return new DoubleListIterator();
    }

    private Node<T> nodeAt(int index) {
        if (index < size / 2) {
            return findFromHead(index);
        }
        return findFromTail(index);
    }

    private Node<T> findFromHead(int index) {
        Node<T> current = head;
        int step = 0;
        while (step < index) {
            current = current.next;
            step++;
        }
        return current;
    }

    private Node<T> findFromTail(int index) {
        Node<T> current = tail;
        int step = size - 1;
        while (step > index) {
            current = current.previous;
            step--;
        }
        return current;
    }

    private void unlinkFirstNode() {
        if (head == tail) {
            clearList();
            return;
        }
        head = head.next;
        head.previous = null;
    }

    private void unlinkLastNode() {
        if (head == tail) {
            clearList();
            return;
        }
        tail = tail.previous;
        tail.next = null;
    }

    private void unlinkMiddleNode(Node<T> current) {
        current.previous.next = current.next;
        current.next.previous = current.previous;
    }

    private void clearList() {
        head = null;
        tail = null;
    }

    private static final class Node<T> {
        private final T value;
        private Node<T> previous;
        private Node<T> next;

        private Node(T value) {
            this.value = value;
        }
    }

    private final class DoubleListIterator implements Iterator<T> {
        private Node<T> current = head;

        public boolean hasNext() {
            return current != null;
        }

        public T next() {
            if (current == null) {
                throw new NoSuchElementException();
            }
            T value = current.value;
            current = current.next;
            return value;
        }
    }
}
