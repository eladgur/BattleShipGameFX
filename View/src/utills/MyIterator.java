package utills;

import java.util.List;
import java.util.ListIterator;

public class MyIterator<T>{
    private final ListIterator<T> listIterator;

    private boolean nextWasCalled = false;
    private boolean previousWasCalled = false;

    public MyIterator(List<T> list) {
        this.listIterator = list.listIterator();
    }

    public boolean hasNext() {
        boolean hasNext = listIterator.hasNext();
        if (previousWasCalled) {
            listIterator.next();
            hasNext = listIterator.hasNext();
            listIterator.previous();
        }

        return hasNext;
    }

    public T next() {
        nextWasCalled = true;
        if (previousWasCalled) {
            previousWasCalled = false;
            listIterator.next();
        }
        return listIterator.next();
    }

    public boolean hasPrevious() {
        boolean hasPrev = listIterator.hasPrevious();
        if (nextWasCalled) {
            listIterator.previous();
            hasPrev = listIterator.hasPrevious();
            listIterator.next();
        }

        return hasPrev;
    }

    public T previous() {
        if (nextWasCalled) {
            listIterator.previous();
            nextWasCalled = false;
        }
        previousWasCalled = true;
        return listIterator.previous();
    }
}
