package app.ascode;

import android.webkit.JavascriptInterface;

import java.util.ArrayList;
import java.util.Set;

public class WebAppInterface {

    WebviewHandler webviewHandler;
    JsonDataHelper jsonDataHelper;
    public String savedPageDir = "/sdcard/Working/Browser/SavedPages/", entryPoint = "";


    public WebAppInterface(WebviewHandler webviewHandler){
        this.webviewHandler = webviewHandler;
        init();
    }

    public void init(){
        jsonDataHelper = new JsonDataHelper(this);


        jsonDataHelper.init();
    }

    String encode(String[] strings) {
        return jsonDataHelper.encodeStringArray(strings);

    }
    String encode(Set<String> strings) {
        return jsonDataHelper.encodeStringArray(strings);

    }
    String encodeList(ArrayList<String> strings) {
        return jsonDataHelper.encodeStringArray(strings);

    }


    @JavascriptInterface
    public void copyToClipboard(String text) {
        webviewHandler.activity.copyToClipboard(text);

    }

    @JavascriptInterface
    public void toast(String text) {
        webviewHandler.activity.toast(text);

    }
    
    @JavascriptInterface
    public void save(String fileName, String content) {
        webviewHandler.save(savedPageDir + fileName, content);

    }
    @JavascriptInterface
    public void save(String dirPath, String fileName, String content) {
        webviewHandler.save(dirPath + fileName, content);

    }
    @JavascriptInterface
    public void setSystemVolumeLevel(int level) {
        webviewHandler.setVolume(level);

    }
    @JavascriptInterface
    public int getSystemVolume() {
        return webviewHandler.audioManager.getStreamVolume(3);

    }    









}



















