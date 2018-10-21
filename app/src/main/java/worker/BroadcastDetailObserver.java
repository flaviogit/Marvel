package worker;

import java.util.Observable;

/**
 * Created by Lillo on 21/10/2018.
 */

public class BroadcastDetailObserver extends Observable {
    private static final BroadcastDetailObserver ourInstance = new BroadcastDetailObserver();

    public static BroadcastDetailObserver getInstance() {
        return ourInstance;
    }

    private BroadcastDetailObserver() {
    }

    public void updateValue(Object data) {
        synchronized (this) {
            setChanged();
            notifyObservers(data);
        }
    }
}
