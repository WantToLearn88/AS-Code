package app.ascode;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class WebviewHandler {

    public WebView webview;
    ASCodeActivity activity;
    public AudioManager audioManager;


    public TextView consoleLogTv;




    public WebviewHandler(ASCodeActivity codeActivity){
        activity = codeActivity;
        init();
    }

    public void init(){
        webview = activity.findViewById(R.id.webview);
        consoleLogTv = activity.findViewById(R.id.console_log_tv);
        customizeWebView();
        customizeWebClient();

        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
    }


    public void customizeWebView() {
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setBlockNetworkLoads(true);
        settings.setDisplayZoomControls(false);
        settings.setBuiltInZoomControls(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        webview.addJavascriptInterface(new WebAppInterface(this), "Android");

    }

    public void customizeWebClient(){
        webview.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {return false;}
            public void onPageStarted(WebView v, String u, Bitmap i) {}
            public void onPageFinished(WebView v, String u) {}
        });
        webview.setWebChromeClient(new WebChromeClient(){
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                String message = consoleMessage.message();
                if (!message.contains("Slow network is detected")) {
                    consoleLogTv.append(consoleMessage.lineNumber() + ". " + message + "\n\n");
                }
                return false;
            }
        });
    }

    public void setVolume(int level) {
        audioManager.setStreamVolume(3, level, 0);
    }

    public void save(String filePath, String content) {
        File file = new File(filePath);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            bw.write(content);
            activity.toast("Saved");
        }
        catch (Exception e) {
            activity.toast(e.getLocalizedMessage());
        }
    }


    public void loadFilePath(String filePath){
        webview.loadUrl("file://" + filePath);
    }

    public void reload(){
        webview.clearHistory();
        webview.clearCache(true);
        consoleLogTv.setText("");
        webview.reload();
    }

    public void loadFile(File file){
        loadFilePath(file.getPath());
    }


    /*public void executeScript(String script){
        webview.loadUrl("javascript: " + script);
    }*/

    public void evaluateIFFE(String script){
        evaluateScript("(() => {" + script + "})();");
    }

    public void evaluateScript(String script){
        webview.evaluateJavascript(script, null);
    }

    public void exit() {
        webview.stopLoading();
        webview.loadUrl("about:blank"); // stops media playback
        webview.clearHistory();
        webview.clearCache(true);
        consoleLogTv.setText("");

    }




}



















