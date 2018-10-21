package worker;

import java.util.Observable;

/**
 * Created by Lillo on 21/10/2018.
 */

public class BroadcastGridObserver extends Observable {
    private static final BroadcastGridObserver ourInstance = new BroadcastGridObserver();

    public static BroadcastGridObserver getInstance() {
        return ourInstance;
    }

    private BroadcastGridObserver() {
    }

    public void updateValue(Object data) {
        synchronized (this) {
            setChanged();
            notifyObservers(data);
        }
    }
}
