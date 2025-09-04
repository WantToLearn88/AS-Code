package app.ascode;

import android.app.Activity;
import android.view.View;

public interface EditorTabCallback {
    Activity getActivity();
    void onClickTabTitle(EditorTab editorTab);
    void onClickTabOption(EditorTab editorTab, View anchor);

}
