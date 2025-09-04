package app.ascode;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ASCodeActivity extends EditorActivity {
    WebviewHandler webviewHandler;
    ViewGroup.LayoutParams bottomMenuParams;
    LinearLayout consoleLogContainer, untitledTabContainer;



    boolean isFullSize = false, isConsoleLogVisible = false;

    TextView dirPathTv, clockInfoTv, batteryInfoTv;

    int smallHeight = 1080;

    String filePath = "";


    @Override
    public void initViews() {
        dirPathTv = findViewById(R.id.dir_path_tv);
        clockInfoTv = findViewById(R.id.clock_info_tv);
        batteryInfoTv = findViewById(R.id.battery_info_tv);
        consoleLogContainer = findViewById(R.id.console_log_container);
        untitledTabContainer = findViewById(R.id.untitled_container);

        webviewHandler = new WebviewHandler(this);
        bottomMenuParams = bottomMenu.getLayoutParams();
        smallHeight = bottomMenuParams.height;
        maximizeWebviewHeight(null);
    }



    @Override
    public void onChangeProject() {
        String path = fileHandler.linkedList.getLast().getPath();
        if (!path.equals("")) {
            path = path.replace(fileHandler.sdCard, "/sdcard");

        }
        if (path.equals("/sdcard")) {
            dirNameTv.setText("sdcard");
            path = "/";
        }
        else path = path.substring(0, path.lastIndexOf("/"));
        dirPathTv.setText(path);
    }

    boolean isUntitledTabCreated = false, isUntitledTabVisible = false;
    public void toggleUntitledTab(View v) {
        isUntitledTabCreated = !isUntitledTabCreated;
        isUntitledTabVisible = isUntitledTabCreated;
        if (isUntitledTabCreated) {
            drawerHandler.closeRight();
            show(untitledTabContainer);
            editorHandler.createUntitledTab();
        }
        else {
            hide(untitledTabContainer);
            editorHandler.closeUntitledTab();
        }
    }


    public void showUntitledTab(View v) {
        drawerHandler.closeRight();
        editorHandler.showUntitledTab();
    }


    public void toggleConsoleLog(View v) {
        editorHandler.clearFocus();
        isConsoleLogVisible = !isConsoleLogVisible;
        if (isConsoleLogVisible) show(consoleLogContainer);
        else hide(consoleLogContainer);
    }

    public void updateBottomMenuParam(){
        bottomMenu.setLayoutParams(bottomMenuParams);

    }

    public void maximizeWebviewHeight(View v){
        editorHandler.clearFocus();
        if (!isFullSize) {
            bottomMenuParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            updateBottomMenuParam();
            isFullSize = true;
        }
    }

    public void normalizeWebviewHeight(View v){
        editorHandler.clearFocus();
        if (isFullSize) {
            bottomMenuParams.height = smallHeight;
            updateBottomMenuParam();
            isFullSize = false;
        }
    }



    public void runProject(View v){
        popupManager.hidePopupWindow();
        runProjectInWebview();
    }

    public void runInWebApps(View v){
        popupManager.hidePopupWindow();
        runProjectInWebApps();
    }
    public void onSwipeRightTop() {
        runProjectInWebApps();
    }
	
	
    public boolean isEntryPoint(){
		String path = editorHandler.getActiveFilePath();
        if (path.endsWith(".html") || path.endsWith(".md")) {
            filePath = path;
		}
		else if (filePath.equals("")) {
            toast("Set an Entry Point First!");
            return false;
        }
		return true;
	}
	
    public void setAsEntryPoint(View v){
        popupManager.hidePopupWindow();
        String path = editorHandler.getActiveFilePath();
        if (path.endsWith(".html") || path.endsWith(".md")) {
            filePath = path;
        }
        else toast("Entry needs to be Html");
    }

    public void runProjectInWebview(){
        if (!isEntryPoint()) return;
        if (isBottomMenuVisible) webviewHandler.exit();
        saveAll();
        showBottomMenu();
        /*Intent i = new Intent("AsCode.RunProject");
        ComponentName cn = new ComponentName("app.browser", "app.browser.MainActivity");
        i.setComponent(cn);*/
        /*Intent i = new Intent(this, WebViewActivity.class);
        i.putExtra("filePath", filePath);
        startActivity(i);*/
        webviewHandler.loadFilePath(filePath);
    }
	
	
    public void runProjectInWebApps(){
		if (!isEntryPoint()) return;

        saveAll();
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(filePath));
        i.addCategory(Intent.CATEGORY_DEFAULT);
        ComponentName cn = new ComponentName("app.webapps", "app.webapps.WebAppsRuntime");
        i.setComponent(cn);
        startActivity(i);

    }

    /*public void openBottomMenu(View v) {
        popupManager.hidePopupWindow();
        showBottomMenu();
    }*/

    public void closeWebview(View v) {
        editorHandler.clearFocus();
        webviewHandler.exit();
        if (isConsoleLogVisible) {
            isConsoleLogVisible = false;
            hide(consoleLogContainer);
        }
        hide(bottomMenu);
        isBottomMenuVisible = false;

    }

    public void reload(View v){
        editorHandler.clearFocus();
        runProjectInWebview();
    }


}














