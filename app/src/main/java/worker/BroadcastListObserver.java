package worker;

import java.util.Observable;

/**
 * Created by Lillo on 21/10/2018.
 */

public class BroadcastListObserver extends Observable {
    private static final BroadcastListObserver ourInstance = new BroadcastListObserver();

    public static BroadcastListObserver getInstance() {
        return ourInstance;
    }

    private BroadcastListObserver() {

    }

    public void updateValue(Object data) {
        synchronized (this) {
            setChanged();
            notifyObservers(data);
        }
    }
}
