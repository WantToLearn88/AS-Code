package app.ascode;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import app.ascode.filesystem.FileHandler;
import arman.common.PermissionManager;

public class EditorActivity extends Activity {


    public PermissionManager permissionManager;
	public static int SETTINGS_UPDATE = 127;
    public DrawerHandler drawerHandler;
	public EditorHandler editorHandler;

	public PopupManager popupManager;
    public FileHandler fileHandler;

    public ClipboardManager clipboard;

    public LinearLayout bottomMenu;
    public RelativeLayout dialogueUi/*, topBar*/;
    public EditText textInput;
    public TextView dirNameTv, warningTv;
    int dialogueType = 0;
    final int SAVE_DIALOGUE = 1;
    final int RENAME_DIALOGUE = 2;
    final int DELETE_DIALOGUE = 3;
    final int CLOSE_DIALOGUE = 4;
    final int OPENDOC_DIALOGUE = 5;
    final int EXIT_DIALOGUE = 6;
    final String W1 = "You have 'Unsaved changes'. Are you sure, you want to '";
    final String W2 = "' without Saving Current one?";
    final String CLOSE_WARNING = W1 + "Close the Tab' without Saving?";
    final String OPENDOC_WARNING = W1 + "OPEN DOC" + W2;
    final String EXIT_WARNING = W1 + "EXIT" + W2;
    public SharedPreferences sp;
    public boolean isReadOnly = false, isBottomMenuVisible, isChoosingProject = false, isIDE = false;
    //boolean istopBarEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityLayout(R.layout.activity_editor);
    }

    public void setActivityLayout(int layout){
        setContentView(layout);
        handlePermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //broadcast("addCoderFloatingTrigger");
    }

    @Override
    protected void onPause() {
        super.onPause();
//broadcast("removeCoderFloatingTrigger");
    }

    public void handlePermission() {
        permissionManager = new PermissionManager(this);
        if (permissionManager.hasFilePermission()) initialize();

    }

    public void initialize() {
        sp = getSharedPreferences("settings", 0);
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        popupManager = new PopupManager(this);
        editorHandler = new EditorHandler(this);
        drawerHandler = new DrawerHandler(this);
        fileHandler = new FileHandler(this);


        dialogueUi = findViewById(R.id.dialogue_ui);
        bottomMenu = findViewById(R.id.bottom_menu);
        textInput = findViewById(R.id.text_input);
        warningTv = findViewById(R.id.warning_tv);
        dirNameTv = findViewById(R.id.dir_name_tv);
        initViews();
        bottomMenu.setOnTouchListener((v, e) -> {
            return true;
        });

        fileHandler.initFileHandler();
        editorHandler.initEditorHandler();
        drawerHandler.initDrawerHandler();
        popupManager.initPopupManager();
        
        isIDE = isIde();
        //toast("init");
        fileHandler.restoreProjects();
        handleIntent(getIntent());
        applySettings();
    }
    public void initViews(){}
        
    public void handleIntent(Intent i){
        fileHandler.handleIntent(i);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); 
        //toast("new intent");
        handleIntent(intent);
    }
    
    public void onProjectChange(String name) {
        dirNameTv.setText(name);
        onChangeProject();
    }
    public void onChangeProject(){}

    public boolean isIde(){
        return false;
    }
    private void setLabel(){
        setTaskDescription(new ActivityManager.TaskDescription.Builder().setLabel(getActivityLabel()).build());
    }
    public String getActivityLabel() {
        return "AS-Code";
    }
    
    
    public void applySettings() {
        setFullScreen(true);
        editorHandler.setFontSize(sp.getInt("fontSize", 14));
        drawerHandler.enableSwipers(sp.getBoolean("enableLeftSwiper", true), sp.getBoolean("enableRightSwiper", false));
    }

    public void broadcast(String request) {
        sendBroadcast(new Intent("AdminService").putExtra("request", request));
    }


    public void hideBottomMenu(View v) {
        if (isBottomMenuVisible) {
            hide(bottomMenu);
            isBottomMenuVisible = false;
        }
    }
    public void showBottomMenu() {
        if (!isBottomMenuVisible) {
            show(bottomMenu);
            isBottomMenuVisible = true;
        }
    }

    public void toggleBottomMenu(View v) {
        isBottomMenuVisible = !isBottomMenuVisible;
        if (isBottomMenuVisible) show(bottomMenu);
        else hide(bottomMenu);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            onRotate(true);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            onRotate(false);
        }
    }


    void onRotate(boolean landscape) {
        popupManager.onRotate(landscape);
    }

    public void setFullScreen(boolean isFullScreen) {
        int fullscreen = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        int visibility = View.SYSTEM_UI_FLAG_VISIBLE;
        Window window = getWindow();
        if (isFullScreen) {
            window.addFlags(fullscreen);
            visibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        else window.clearFlags(fullscreen);
        window.getDecorView().setSystemUiVisibility(visibility);
    }
    public void copyToClipboard(String text){
        ClipData clipData = ClipData.newPlainText("lebel", text);
        clipboard.setPrimaryClip(clipData);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        byte result = permissionManager.isGranted(requestCode, permissions, grantResults);

        if (result == PermissionManager.Granted) {
            if (requestCode == PermissionManager.FILE) {
                initialize();
            }
        } else if (result == PermissionManager.Partial) {
            if (requestCode == PermissionManager.OTHERS) {
                toast("Some Permissions are missing");
            }
        } else {
            if (requestCode == PermissionManager.FILE) {
                toast("File Permissions Denied");
                finishAndRemoveTask();
                return;
            }
        }

    }

    @Override
    public void onBackPressed() {
        drawerHandler.onBackPressed();
    }

    String sfName;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        sfName = fileHandler.getSelectedFileName(menuInfo);
        menu.setHeaderTitle(sfName);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rename:
                dialogueType = RENAME_DIALOGUE;
                textInput.setText(sfName);
                hide(warningTv);
                show(textInput);
                show(dialogueUi);
                return true;

            case R.id.delete:
                dialogueType = DELETE_DIALOGUE;
                warningTv.setText("Delete " + sfName + "?");
                hide(textInput);
                show(warningTv);
                show(dialogueUi);
                //fileHandler.deleteSelectedFile();
                return true;
        }
        return false;
    }


    public void emptyClick(View v) {
    }

    public void hidePopupWindow(View v) {
        popupManager.hidePopupWindow();
    }

    public void showProjectOptions(View v) {
        popupManager.showProjectOptions(v);
    }

    public void chooseNewProject(View v) {
        if (isChoosingProject) acceptProjectChoice();
        else {
            isChoosingProject = true;
            dirNameTv.setText("Choose Project");
            fileHandler.chooseNewProject();
        }
        popupManager.hidePopupWindow();
    }
    public void openCurrentDirAsProject(View v) {
        if (isChoosingProject) acceptProjectChoice();
        else {
            fileHandler.openAsProject();
        }
        popupManager.hidePopupWindow();
    }
    public void closeProject(View v) {
        if (isChoosingProject) cancelProjectChoice();
        else fileHandler.closeProject();
        popupManager.hidePopupWindow();
    }


    /*public void previousProject(View v) {
        if (isChoosingProject) cancelProjectChoice();
        else fileHandler.previousProject();
    }*/
    public void nextProject(View v) {
        if (isChoosingProject) acceptProjectChoice();
        //else fileHandler.nextProject();
    }

    public void cancelProjectChoice() {
        isChoosingProject = false;
        fileHandler.cancelProjectChoice();
    }
    public void acceptProjectChoice() {
        isChoosingProject = false;
        fileHandler.openAsProject();
    }


    public void showSaveOptions(View v) {
        editorHandler.clearFocus();
        popupManager.showSaveOptions(v);
    }

    public void showBuildOptions(View v) {
        editorHandler.clearFocus();
        popupManager.showBuildOptions(v);
    }


    public void onSwipeRightTop() {}



    public void closeEditorTab(View v) {
        popupManager.hidePopupWindow();
        if (editorHandler.isCloseable()) editorHandler.closeTab();
        else {
            dialogueType = CLOSE_DIALOGUE;
            warningTv.setText(CLOSE_WARNING);
            hide(textInput);
            show(warningTv);
            show(dialogueUi);
        }

    }


    public void gotoHomeDir(View v) {
        if (isChoosingProject) cancelProjectChoice();
        fileHandler.gotoHomeDir();
    }
    public void gotoSrcDir(View v) {
        if (isChoosingProject) cancelProjectChoice();
        fileHandler.gotoSrcDir();
    }
    public void gotoJavaDir(View v) {
        if (isChoosingProject) cancelProjectChoice();
        fileHandler.gotoJavaDir();
    }
    public void gotoResDir(View v) {
        if (isChoosingProject) cancelProjectChoice();
        fileHandler.gotoResDir();
    }
    public void gotoEditingDir(View v) {
        if (isChoosingProject) cancelProjectChoice();
        fileHandler.gotoEditingDir();
    }



    public void save(View v) {
        popupManager.hidePopupWindow();
        editorHandler.saveActiveFile();
    }

    public void saveAs(View v) {
        popupManager.hidePopupWindow();
        showSaveAsDialogue();
    }

    public void saveAll(View v) {
        saveAll();
    }

    public void saveAll() {
        popupManager.hidePopupWindow();
        editorHandler.saveAll();
    }

    public void openDoc() {
        fileHandler.openClickedFile();

    }

    public void gotoSettings(View v) {
        Intent i = new Intent(this, SettingsActivity.class);


        try {
            startActivityForResult(i, SETTINGS_UPDATE);
        } catch (Exception e) {
            toast("setting: " + e.getMessage());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PermissionManager.FILE) {
            if (Environment.isExternalStorageManager()) {
                initialize();
            } else {
                toast("File Permissions Denied");
                finishAndRemoveTask();
            }
        }
        if (requestCode == SETTINGS_UPDATE && resultCode == SETTINGS_UPDATE) {
            applySettings();
        }
    }

    public void showSaveAsDialogue() {
        dialogueType = SAVE_DIALOGUE;
        textInput.setText(editorHandler.getActiveFileName());
        hide(warningTv);
        show(textInput);
        show(dialogueUi);
        //drawerHandler.closeRight();
    }

    public void exit(View v) {
        if (!editorHandler.isEdited()) {

            finishAndRemoveTask();
            return;
        }
        dialogueType = EXIT_DIALOGUE;
        warningTv.setText(EXIT_WARNING);
        hide(textInput);
        show(warningTv);
        show(dialogueUi);
    }

    public void undo(View v) {
        editorHandler.undo();
    }

    public void redo(View v) {
        editorHandler.redo();
    }

    public void paste(View v) {

    }

    public void cut(View v) {

    }

    public void select(View v) {

    }

    public void deleteText(View v) {

    }

    public void cancelDialogue(View v) {
        hide(dialogueUi);
        editorHandler.hideIme();
    }

    public void dialogueOkey(View v) {
        switch (dialogueType) {
            case SAVE_DIALOGUE:
                editorHandler.saveAsActiveTab(textInput.getText().toString());
                break;

            case RENAME_DIALOGUE:
                fileHandler.renameSelectedFile(textInput.getText().toString());
                break;

            case DELETE_DIALOGUE:
                fileHandler.deleteSelectedFile();
                break;

            case CLOSE_DIALOGUE:
                editorHandler.closeTab();
                break;

            case OPENDOC_DIALOGUE:
                fileHandler.openClickedFile();
                break;

            case EXIT_DIALOGUE:

                finishAndRemoveTask();

                break;
        }

        hide(dialogueUi);
        editorHandler.hideIme();
    }

    public void hide(View v) {
        v.setVisibility(View.GONE);
    }

    public void show(View v) {
        v.setVisibility(View.VISIBLE);
    }


    public void toggleWrap(View v) {
        //editorHandler.toggleWrap();
    }


    public void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void toggleReadOnly(View v) {
        editorHandler.toggleReadOnly();

    }
    public void toggleReadOnlyOld(View v) {
        isReadOnly = !isReadOnly;
        int readonly = R.drawable.ic_edit;
        if (isReadOnly) {
            readonly = R.drawable.ic_readonly;
        }
        ImageButton button = (ImageButton) v;
        editorHandler.setReadOnly(isReadOnly);
        button.setImageResource(readonly);
    }

    public void toggleLeftDrawer(View view) {
        drawerHandler.toggleLeftDrawer();
    }

    public void toggleRightDrawer(View view) {
        drawerHandler.toggleRightDrawer();
    }




/*public void hideProjectDrawer(View view) {
		drawerHandler.closeRight();
	}*/


}














