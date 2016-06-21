package com.colorcloud.wifichat;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Administrator on 3/14/2016.
 */
public abstract class ActivityExtender extends ActionBarActivity {

    public abstract void onPeersAvailable(WifiP2pDeviceList peers);
    public abstract void onConnectionInfoAvailable(WifiP2pInfo info);
    public abstract void resetData();
    public abstract void updateThisDevice(WifiP2pDevice dev);
    public abstract void onChannelDisconnected();
    public abstract void startChatActivity(String msg);

    public static boolean mHasFocus;

}
