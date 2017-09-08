package utills;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;

import java.util.List;
import java.util.ListIterator;

public class MyIterator<T>{
    private final ListIterator<T> listIterator;
    private T lastItem;
    private boolean nextWasCalled = false;
    private boolean previousWasCalled = false;
    private BooleanProperty hasNext;
    private BooleanProperty hasPrevious;


    public MyIterator(List<T> list) {
        this.hasNext = new SimpleBooleanProperty(false);
        this.hasPrevious = new SimpleBooleanProperty(false);

        if (list != null) {
            this.listIterator = list.listIterator(list.size()); //Create Iterator point to the end of the list
            this.hasNext.set(hasNext());
            this.hasPrevious.set(hasPrevious());
        } else {
            this.listIterator = null;
        }
    }

    public boolean hasNext() {
        boolean hasNext = listIterator.hasNext();
        if (hasNext) {
            T next = listIterator.next();
            if (next == lastItem) {
                hasNext = listIterator.hasNext();
            }
            listIterator.previous();
        }

        return hasNext;
    }

    public boolean hasPrevious() {
        boolean hasPrev = listIterator.hasPrevious();
        if (hasPrev) {
            T previous = listIterator.previous();
            if (previous == lastItem) {
                hasPrev = listIterator.hasPrevious();
            }
            listIterator.next();
        }

        return hasPrev;
    }

    public T next() {
        this.nextWasCalled = true;
        if (previousWasCalled) {
            previousWasCalled = false;
            listIterator.next();
        }
        T result = listIterator.next();
        this.lastItem = result;
        hasNext.set(hasNext());
        hasPrevious.set(hasPrevious());

        return result;
    }

    public T previous() {
        if (nextWasCalled) {
            listIterator.previous();
            nextWasCalled = false;
        }
        T result = listIterator.previous();
        this.lastItem = result;
        hasPrevious.set(hasPrevious());
        hasNext.set(hasNext());
        previousWasCalled = true;

        return result;
    }

    public BooleanProperty getHasNextProperty() {
        return this.hasNext;
    }

    public BooleanProperty getHasPreviousProperty() {
        return this.hasPrevious;
    }
}
