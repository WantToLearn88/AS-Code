package app.ascode;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.langs.java.JavaLanguage;
import io.github.rosemoe.sora.widget.CodeEditor;

public class UntitledTab {
    public CodeEditor editor;
    public EditorHandler editorHandler;
    //public TextView tabNameTv;
    public View tabBar;

    public UntitledTab(EditorHandler editorHandler) {
        this.editorHandler = editorHandler;
    }


    public void createTab(){
        tabBar = editorHandler.activity.findViewById(R.id.untitled_tab_bar);
        editor = new CodeEditor(editorHandler.activity);
        editor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        setFontSize(editorHandler.fontSize);
        setLanguage(new JavaLanguage());
        editorHandler.editorContainer.addView(editor);
    }

    public void setLanguage(Language language){
        editor.setEditorLanguage(language);

    }
    public void setFontSize(int size){
        editor.setTextSize(size);

    }


    public void setActive(){
        tabBar.setBackgroundColor(0xffffff00);
        //tabNameTv.setTextColor(0xffffff00);
    }
    public void resetActive(){
        tabBar.setBackgroundColor(0x00000000);
        //tabNameTv.setTextColor(0xff000000);
    }


    void hide(){
        editor.setVisibility(View.GONE);
        editorHandler.isUntitledTabVisible = false;
    }
    void show(){
        editor.setVisibility(View.VISIBLE);
        editorHandler.isUntitledTabVisible = true;
    }


    void destroy(){
        editorHandler.editorContainer.removeView(editor);
        editor.release();
        editor = null;
        tabBar = null;
        try {this.finalize();}
        catch (Throwable ignored) {editorHandler.activity.toast(ignored.getMessage());}
    }

}



















