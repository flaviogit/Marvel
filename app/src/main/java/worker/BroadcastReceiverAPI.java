package worker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;



public class BroadcastReceiverAPI extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            final String action = intent.getAction();
            if(ServiceAPI.CHARACTERLIST.equals(action))
                BroadcastListObserver.getInstance().updateValue(intent);
            else if(ServiceAPI.CHARACTERGRID.equals(action))
                BroadcastGridObserver.getInstance().updateValue(intent);
            else if(ServiceAPI.CHARACTERDETAIL.equals(action))
                BroadcastDetailObserver.getInstance().updateValue(intent);

        }

    }
}

