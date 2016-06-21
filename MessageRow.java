package com.colorcloud.wifichat;

import static com.colorcloud.wifichat.Constants.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.colorcloud.wifichat.WiFiDirectApp.PTPLog;


public class MessageRow implements Parcelable {
	private final static String TAG = "PTP_MSG";
	
	public String mSender;
	public String mMsg;
	public String mTime;
	public String path;
    int type;
	public static final String mDel = "^&^";
	
	private MessageRow() { 
	    this.mSender = null;
		this.mTime = null;
		this.mMsg = null;
	}
	
	public MessageRow(String sender, String msg,String path, String time, int type){
		mTime = time;
		if( time == null ){
			Date now = new Date();
			//SimpleDateFormat timingFormat = new SimpleDateFormat("mm/dd hh:mm");
			//mTime = new SimpleDateFormat("dd/MM HH:mm").format(now);
			mTime = new SimpleDateFormat("h:mm a").format(now);
		} 
		mSender = sender;
		mMsg = msg;
		this.path = path;
        Log.d("ERROR", "inside Message Row MAking" + msg.length());
        this.type = type;
	}
	
	public MessageRow(Parcel in) {
        readFromParcel(in);
    }
	
	public String toString() {
		return mSender + mDel + mMsg + mDel + mTime;
	}
	
	
	public static JSONObject getAsJSONObject(MessageRow msgrow) {
		JSONObject jsonobj = new JSONObject();
		try{
			jsonobj.put(MSG_SENDER, msgrow.mSender);
			jsonobj.put(MSG_TIME, msgrow.mTime);
			jsonobj.put(MSG_CONTENT, msgrow.mMsg);
			jsonobj.put(MSG_PATH,msgrow.path);
            Log.d("ERROR","inbside meaasge object making  Json "+ msgrow.mMsg.length());
            jsonobj.put(MSG_TYPE,msgrow.type);
		}catch(JSONException e){
			PTPLog.e(TAG, "getAsJSONObject : " + e.toString());
		}
		return jsonobj;
	}
	
	/**
	 * convert json object to message row.
	 */
	public static MessageRow parseMesssageRow(JSONObject jsonobj) {
		MessageRow row = null;
		if( jsonobj != null ){
			try{
				row = new MessageRow(jsonobj.getString(MSG_SENDER), jsonobj.getString(MSG_CONTENT),jsonobj.getString(MSG_PATH) ,jsonobj.getString(MSG_TIME),jsonobj.getInt(MSG_TYPE));
			}catch(JSONException e){
				PTPLog.e(TAG, "parseMessageRow: " + e.toString());
			}
		}
		return row;
	}
	
	/**
	 * convert a json string representation of messagerow into messageRow object.
	 */
	public static MessageRow parseMessageRow(String jsonMsg){
        Log.d("ERROR","BEFORE MAKING JASON STR LEN "+jsonMsg.length());

		JSONObject jsonobj = JSONUtils.getJsonObject(jsonMsg);

        Log.d("ERROR","After packicg Json "+jsonobj.length());

		PTPLog.d(TAG, "parseMessageRow : " + jsonobj.toString());
		return parseMesssageRow(jsonobj);
	}

	public static final Parcelable.Creator<MessageRow> CREATOR = new Parcelable.Creator<MessageRow>() {
        public MessageRow createFromParcel(Parcel in) {
            return new MessageRow(in);
        }
 
        public MessageRow[] newArray(int size) {
            return new MessageRow[size];
        }
    };
    
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mSender);
		dest.writeString(mMsg);
		dest.writeString(mTime);
	}
	
	public void readFromParcel(Parcel in) {
		mSender = in.readString();
		mMsg = in.readString();
		mTime = in.readString();
    }
}
