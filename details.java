package com.colorcloud.wifichat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.internal.widget.AppCompatPopupWindow;




public class details extends ActivityExtender{
   public static WifiP2pDevice device;
    public static final String TAG = "PTP_Activity";

    WiFiDirectApp mApp = null;
    public static Context con;
    boolean mHasFocus = false;


    DeviceDetailFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //con=get();


         fragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);

        fragment.showDetails(device);
        Toast.makeText(this,device.deviceName,Toast.LENGTH_SHORT).show();
        mApp = (WiFiDirectApp)getApplication();

        mApp.mHomeActivity = (details)this;

    }


    @Override
    protected void onResume(){
        super.onResume();
        mHasFocus=true;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public  void onConnectionInfoAvailable(final WifiP2pInfo info) {


        DeviceDetailFragment fragment=(DeviceDetailFragment)getFragmentManager().findFragmentById(R.id.frag_detail);
        fragment.onConnectionInfoAvailable(info);

        //DeviceDetailFragment.frag.onConnectionInfoAvailable(info);

    }




    public void resetData() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
     //           DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);


       //         if (fragmentList != null) {
         //           fragmentList.clearPeers();
           //     }

                DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
                if (fragmentDetails != null) {
                    fragmentDetails.resetViews();
                }
            }
        });
    }

    @Override
    public void updateThisDevice(WifiP2pDevice dev) {

    }

    public void onPeersAvailable(final WifiP2pDeviceList peerList){
        runOnUiThread(new Runnable() {
            @Override public void run() {
     /*           DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
                fragmentList.onPeersAvailable(mApp.mPeers);  // use application cached list.
                //DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);

                for(WifiP2pDevice d : peerList.getDeviceList()){
                    if( d.status == WifiP2pDevice.FAILED ){
                        WiFiDirectApp.PTPLog.d(TAG, "onPeersAvailable: Peer status is failed " + d.deviceName);
                        //  	fragmentDetails.resetViews();
                    }
                }
            }*/
               // WiFiDirectActivity wifi=new WiFiDirectActivity();
                //wifi.onPeersAvailable(peerList);

            }

        });
    }



    public void connect(WifiP2pConfig config) {
     //   WiFiDirectApp.PTPLog.d(TAG, "connect : connect to server : " + config.deviceAddress);
        // perform p2p connect upon users click the connect button. after connection, manager request connection info.
        Toast.makeText(this,device.deviceName,Toast.LENGTH_SHORT).show();
        mApp.mP2pMan.connect(mApp.mP2pChannel, config, new WifiP2pManager.ActionListener() {


            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
       //         Toast.makeText(WiFiDirectActivity.this, "Connect success..", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
         //       Toast.makeText(WiFiDirectActivity.this, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void disconnect() {
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        fragment.resetViews();

     //   WiFiDirectApp.PTPLog.d(TAG, "disconnect : removeGroup ");
        mApp.mP2pMan.removeGroup(mApp.mP2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
         //       WiFiDirectApp.PTPLog.d(TAG, "Disconnect failed. Reason : 1=error, 2=busy; " + reasonCode);
       //         Toast.makeText(WiFiDirectActivity.this, "disconnect failed.." + reasonCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
           //     WiFiDirectApp.PTPLog.d(TAG, "Disconnect succeed. ");
               // fragment.getView().setVisibility(View.GONE);
                Intent intent = new Intent(getBaseContext(),WiFiDirectActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * The channel to the framework(WiFi direct) has been disconnected.
     * This is diff than the p2p connection to group owner.
     */
    public void onChannelDisconnected() {
        Toast.makeText(this, "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",Toast.LENGTH_LONG).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("WiFi Direct down, please re-enable WiFi Direct")
                .setCancelable(true)
                .setPositiveButton("Re-enable WiFi Direct", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                })
                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog info = builder.create();
        info.show();
    }


    public void startChatActivity(final String initMsg) {
        if( ! mApp.mP2pConnected ){
            Log.d(TAG, "startChatActivity : p2p connection is missing, do nothng...");
            return;
        }

        WiFiDirectApp.PTPLog.d(TAG, "startChatActivity : start chat activity fragment..." + initMsg);
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Intent i = mApp.getLauchActivityIntent(MainActivity.class, initMsg);
                startActivity(i);
            }
        });
    }

    @Override
    public void onStop() {  // the activity is no long visible
        super.onStop();
        mHasFocus = false;
    }




}
