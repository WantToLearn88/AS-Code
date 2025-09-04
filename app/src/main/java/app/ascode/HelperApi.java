package app.ascode;

import android.app.Activity;
import android.content.SharedPreferences;

import app.ascode.filesystem.FileHandler;

public interface HelperApi {
    Activity getActivity();
    SharedPreferences getSP();
    FileHandler getFileHandler();
    DrawerHandler getDrawerHandler();
    PopupManager getPopupManager();
    EditorHandler getEditorHandler();
    void onProjectChange(String name);
    void onSaveAs();
}
