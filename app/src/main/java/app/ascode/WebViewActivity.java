package app.ascode;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import arman.common.infocodes.InfoCode;
import arman.common.ui.BaseActivity;

public class WebViewActivity extends BaseActivity {
    WebviewHandler webviewHandler;
    LinearLayout consoleLogContainer;



    boolean isConsoleLogVisible = false;

    public void initViews() {
        setFullScreen(true);
        consoleLogContainer = findViewById(R.id.console_log_container);

        //webviewHandler = new WebviewHandler(this);

    }
    public void handleIntent() {
        Intent i = getIntent();
        String filePath = i.getStringExtra("filePath");
        webviewHandler.webview.loadUrl("file://" + filePath);
    }


    public void toggleConsoleLog(View v) {
        isConsoleLogVisible = !isConsoleLogVisible;
        if (isConsoleLogVisible) show(consoleLogContainer);
        else hide(consoleLogContainer);
    }




    public void reload(View v){
        webviewHandler.reload();
    }


    protected int[] getRequiredPermission() {
        return new int[InfoCode.NO_PERMISSION_REQUIRED];
    }

    protected void onCreate() {
        setContentView(R.layout.activity_webview);
        initViews();
        handleIntent();
    }

    protected void onSettingsChange() {

    }

    public void exit(View view) {
        webviewHandler.exit();
        finish();
    }
}














