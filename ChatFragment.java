package com.colorcloud.wifichat;

import android.app.Activity;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.colorcloud.wifichat.WiFiDirectApp.PTPLog;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * chat fragment attached to main activity.
 */
public class ChatFragment extends ListFragment {
	private static final String TAG = "PTP_ChatFrag";

    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
	WiFiDirectApp mApp = null;
    JSONArray mMessageArray = new JSONArray();		// limit to the latest 50 messages
	private static MainActivity mActivity = null;
	
	private ArrayList<MessageRow> mMessageList = null;   // a list of chat msgs.
    private ArrayAdapter<MessageRow> mAdapter= null;
    
    // private String mMyAddr;
    
	/**
     * Static factory to create a fragment object from tab click.
     */
    public static ChatFragment newInstance(Activity activity, String groupOwnerAddr, String msg) {
    	ChatFragment f = new ChatFragment();
    	mActivity = (MainActivity)activity;
    	
        Bundle args = new Bundle();
        args.putString("groupOwnerAddr", groupOwnerAddr);
        args.putString("initMsg", msg);
        f.setArguments(args);
        Log.d(TAG, "newInstance :" + groupOwnerAddr + " : " + msg);
        return f;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {  // this callback invoked after newInstance done.  
        super.onCreate(savedInstanceState);
        mApp = (WiFiDirectApp)mActivity.getApplication();
        
        setRetainInstance(true);   // Tell the framework to try to keep this fragment around during a configuration change.
    }
    
    /**
     * the data you place in the Bundle here will be available in the Bundle given to onCreate(Bundle), etc.
     * only works when your activity is destroyed by android platform. If the user closed the activity, no call of this.
     * http://www.eigo.co.uk/Managing-State-in-an-Android-Activity.aspx
     */
    @Override
    public void onSaveInstanceState(Bundle outState){
    	super.onSaveInstanceState(outState);
    	outState.putParcelableArrayList("MSG_LIST", mMessageList);
    	Log.d(TAG, "onSaveInstanceState. " + mMessageList.get(0).mMsg);
    }
    
    /**
     * no matter your fragment is declared in main activity layout, or dynamically added thru fragment transaction
     * You need to inflate fragment view inside this function. 
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	// inflate the fragment's res layout. 
        View contentView = inflater.inflate(R.layout.chat_frag, container, false);  // no care whatever container is.
        final EditText inputEditText = (EditText)contentView.findViewById(R.id.edit_input);
        final Button sendBtn = (Button)contentView.findViewById(R.id.btn_send);
        final Button sendImgBtn = (Button) contentView.findViewById(R.id.sendImgBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// send the chat text in current line to the server
				String inputMsg = inputEditText.getText().toString();
				inputEditText.setText("");
				InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(inputEditText.getWindowToken(), 0);
				MessageRow row = new MessageRow(mApp.mDeviceName, inputMsg,null ,null,0);
				appendChatMessage(row);
				String jsonMsg = mApp.shiftInsertMessage(row);
				PTPLog.d(TAG, "sendButton clicked: sendOut data : " + jsonMsg);
				mActivity.pushOutMessage(jsonMsg);
			}
        });

        sendImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
            }
        });
        
        String groupOwnerAddr = getArguments().getString("groupOwnerAddr");
        String msg = getArguments().getString("initMsg");
        PTPLog.d(TAG, "onCreateView : fragment view created: msg :" + msg);
        
    	if( savedInstanceState != null ){
            mMessageList = savedInstanceState.getParcelableArrayList("MSG_LIST");
            Log.d(TAG, "onCreate : savedInstanceState: " + mMessageList.get(0).mMsg);
        }else if( mMessageList == null ){
        	// no need to setContentView, just setListAdapter, but listview must be android:id="@android:id/list"
            mMessageList = new ArrayList<MessageRow>();
            jsonArrayToList(mApp.mMessageArray, mMessageList);
            Log.d(TAG, "onCreate : jsonArrayToList : " + mMessageList.size() );
        }else {
        	Log.d(TAG, "onCreate : setRetainInstance good : ");
        }
        
        mAdapter = new ChatMessageAdapter(mActivity, mMessageList);
        
        setListAdapter(mAdapter);  // list fragment data adapter 
        
        PTPLog.d(TAG, "onCreate chat msg fragment: devicename : " + mApp.mDeviceName + " : " + getArguments().getString("initMsg"));
        return contentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = data.getData();
        byte[] fileData = new byte[50000];

        try {
    //        Log.d("ERRRRRRR","INFRITSTSS");
           // String fileuri=data.getData().toString();
            //Toast.makeText(getActivity(),fileuri,Toast.LENGTH_LONG).show();

           //Uri uri2= Uri.parse(uri.toString());

            Log.d("ERROR",uri.toString());
            Log.d("ERROR",uri.getPath());
            Log.d("ERROR",Uri.parse(uri.toString()).toString());

 //           File file = new File(uri.getPath());
          //  File file = new File("/storage/emulated/0/Test.JPG");
            File file = new File("/sdcard/Test.jpg");
      //      Log.d("ERROR",uri.toString());
        //    Log.d("ERROR",uri.getPath());
          //  Log.d("ERROR",Uri.parse(uri.toString()).toString());


            FileInputStream fis = new FileInputStream(file.getAbsoluteFile());


            //ContentResolver cr = getActivity().getContentResolver();
            // InputStream fis = cr.openInputStream(Uri.parse(uri.getPath()));

            //FileInputStream fs = new FileInputStream()
//            Log.d("ERROR",uri.toString());

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            byte buffer[] = new byte[1024];
            int len = 0;

            while(  (len = fis.read(buffer)  ) != -1 ){
                byteArray.write(buffer,0,len);
                Log.d("ERROR","reading " + len);
            }
            fileData = byteArray.toByteArray();
            Log.d("ERROR","SENDIN DATA"+fileData.length);
           // fis.read(fileData);
           // while( fis.read(fileData) != -1){
           //     Log.d("ERROR",fileData.length + fileData.toString());
           // }

           // String str = new String(fileData);
           // Toast.makeText(getActivity(),"STIRNG LENGTH EQUALS " + str.length(),Toast.LENGTH_SHORT).show();

            Log.d("ERROR","##########"+fileData.length +" Length");
          //  Log.d("ERROR","%"+fileData.toString().length());

          // String str=new String(fileData);
         //   str.get(fileData);
           // String str = new String();
            StringBuffer str = new StringBuffer(1024*1024);
            for(int i =0 ; i<fileData.length; i++){
               // str = str + (char)fileData[i];
                //str.concat((char)fileData[i]);
                str.append((char)fileData[i]);
                Log.d("ERROR","reading byte no "+ i);
            }

           Log.d("ERROR","SIZEEEE Sending ########## "+str.length());
           // Log.d("ERROR","SIEEE "+fileData.toString().length());

           MessageRow row = new MessageRow(mApp.mDeviceName, str.toString(), file.getAbsolutePath() , null,1);
           // appendChatMessage(row);
            appendChatMessage(row);
            String jsonMsg = mApp.shiftInsertMessage(row);
            //PTPLog.d(TAG, "sendButton clicked: sendOut data : " + jsonMsg);
            Log.d("ERROR","afetr packing jason "+jsonMsg.length());



            mActivity.pushOutMessage(jsonMsg);

            fis.close();

        }catch(Exception e){
            Log.d("ERROR",e.toString());

            e.getMessage();

            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
        }

    }

    @Override 
    public void onDestroyView(){ 
    	super.onDestroyView(); 
    	Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {  // invoked after fragment view created.
        super.onActivityCreated(savedInstanceState);
        
        if( mMessageList.size() > 0){
        	getListView().smoothScrollToPosition(mMessageList.size()-1);
        }
        
        setHasOptionsMenu(true);
        Log.d(TAG, "onActivityCreated: chat fragment displayed ");
    }
    
    /**
     * add a chat message to the list, return the format the message as " sender_addr : msg "
     */
    public void appendChatMessage(MessageRow row) {
    	Log.d(TAG, "appendChatMessage: chat fragment append msg: " + row.mSender + " ; " + row.mMsg);
    	mMessageList.add(row);
    	getListView().smoothScrollToPosition(mMessageList.size()-1);
    	mAdapter.notifyDataSetChanged();  // notify the attached observer and views to refresh.
    	return;


    }
    
    private void jsonArrayToList(JSONArray jsonarray, List<MessageRow> list) {
    	try{
    		for(int i=0;i<jsonarray.length();i++){
    			MessageRow row = MessageRow.parseMesssageRow(jsonarray.getJSONObject(i));
    			PTPLog.d(TAG, "jsonArrayToList: row : " + row.mMsg);
    			list.add(row);
    		}
    	}catch(JSONException e){
    		PTPLog.e(TAG, "jsonArrayToList: " + e.toString());
    	}
    }
    
    /**
     * chat message adapter from list adapter.
     * Responsible for how to show data to list fragment list view.
     */
    final class ChatMessageAdapter extends ArrayAdapter<MessageRow> {

    	public static final int VIEW_TYPE_MYMSG = 0;
    	public static final int VIEW_TYPE_INMSG = 1;
    	public static final int VIEW_TYPE_COUNT = 2;    // msg sent by me, or all incoming msgs
    	private LayoutInflater mInflater;
    	
		public ChatMessageAdapter(Context context, List<MessageRow> objects){
			super(context, 0, objects);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
		
		@Override
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }
		
		@Override
        public int getItemViewType(int position) {
			MessageRow item = this.getItem(position);
			if ( item.mSender.equals(mApp.mDeviceName )){
				return VIEW_TYPE_MYMSG;
			}
			return VIEW_TYPE_INMSG;			
		}
		
		/**
		 * assemble each row view in the list view.
		 * http://dl.google.com/googleio/2010/android-world-of-listview-android.pdf
		 */
		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;  // old view to re-use if possible. Useful for Heterogeneous list with diff item view type.
			MessageRow item = this.getItem(position);
			boolean mymsg = false;

            if(item.type == 0) {
                if (getItemViewType(position) == VIEW_TYPE_MYMSG) {


                    if (view == null) {
                        view = mInflater.inflate(R.layout.chat_row_mymsg, null);  // inflate chat row as list view row.
                    }
                    mymsg = true;


                    // view.setBackgroundResource(R.color.my_msg_background);
                } else {
                    if (view == null) {
                        view = mInflater.inflate(R.layout.chat_row_inmsg, null);  // inflate chat row as list view row.
                    }
                    // view.setBackgroundResource(R.color.in_msg_background);
                }

                TextView sender = (TextView)view.findViewById(R.id.sender);
                sender.setText(item.mSender);
                TextView time = (TextView)view.findViewById(R.id.time);
                time.setText(item.mTime);

                TextView msgRow = (TextView)view.findViewById(R.id.msg_row);
                msgRow.setText(item.mMsg);
                if( mymsg ){
                    msgRow.setBackgroundResource(R.color.my_msg_background);
                }else{
                    msgRow.setBackgroundResource(R.color.in_msg_background);
                    time.setBackgroundResource(R.color.in_msg_background);
                }
            }else if(item.type ==1){

                if(view == null){
                     view = mInflater.inflate(R.layout.photolayout,null);
                }

                ImageView img = (ImageView) view.findViewById(R.id.imageview);

                File file = new File(item.path);

                if(file.exists()){
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    img.setImageBitmap(bitmap);
                }




            }
			


            

            
            Log.d(TAG, "getView : " + item.mSender + " " + item.mMsg + " " + item.mTime);
            return view;
		}
    }
}
