package com.daisy.common.webviewclients;

import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class BridgeWCClient extends WebChromeClient {

    private static final String TAG = "CLient";

    @Override

    public boolean onJsPrompt(WebView view, String url, String title,

                              String message, JsPromptResult result) {
        Log.e("kali--------",message);

//        if(title.equals(BRIDGE_KEY)){
//
//            JSONObject commandJSON = null;
//
//            try{
//
//                commandJSON = new JSONObject(message);
//
//                processCommand(commandJSON);
//
//            }
//
//            catch(JSONException ex){
//
//                //Received an invalid json
//
//                Log.e(TAG, "Invalid JSON: " + ex.getMessage());
//
//                result.confirm();
//
//                return true;
//
//            }
        return true;


    }

}