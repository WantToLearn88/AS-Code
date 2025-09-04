package app.ascode;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import io.github.rosemoe.sora.event.ContentChangeEvent;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.langs.java.JavaLanguage;
import io.github.rosemoe.sora.widget.CodeEditor;

public class EditorTab {
    public TabGroup group;
    public CodeEditor editor;
    public View tabBar;

    public File file;

    public boolean isShadowTab = true, isModified, isReadOnly;
    public EditorHandler editorHandler;
    public TextView tabNameTv;

    public EditorTab(TabGroup group) {
        this.group = group;
        this.editorHandler = group.editorHandler;
    }


    public void createTab(File file){
        this.file = file;
        createTab();
        attachTab();
    }
    private void createTab(){
        tabBar = editorHandler.activity.getLayoutInflater().inflate(R.layout.tab_title_layout, null);

        tabNameTv = tabBar.findViewById(R.id.tab_name_tv);
        View tabOptionBtn = tabBar.findViewById(R.id.tab_option_btn);

        tabNameTv.setText(file.getName());
        tabNameTv.setOnClickListener(v->{
            setActive();
            setContent();
            editorHandler.onClickTabTitle(EditorTab.this);
        });
        tabOptionBtn.setOnClickListener(v->{
            editorHandler.onClickTabOption(EditorTab.this, v);
        });
    }

    public void createCodeEditor(){
        isShadowTab = false;
        editor = new CodeEditor(editorHandler.activity);
        editor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        setFontSize(editorHandler.fontSize);
        setLanguage(new JavaLanguage());
        attachEditor();
    }

    public void attachEditor(){
        editorHandler.editorContainer.addView(editor);
    }

    public void attachTab(){
        group.projectTabContainer.addView(tabBar);
    }

    public void attachToView(){
        attachEditor();
        attachTab();
    }

    public String getContent(){
        return editor.getText().toString();
    }

    public void setLanguage(Language language){
        editor.setEditorLanguage(language);

    }
    public void setFontSize(int size){
        editor.setTextSize(size);

    }

    public void setContent(){
        if (!isShadowTab) return;
        createCodeEditor();
        String text = editorHandler.fileHandler.readFile(file);
        editor.setText(text);
        subscribeContentChange();
    }
    public void setContent(String text){
        if (isShadowTab) createCodeEditor();
        editor.setText(text);
        subscribeContentChange();
    }

    public void setFile(File file){
        this.file = file;
        tabNameTv.setText(file.getName());
    }



    public void onSaved(){
        resetModified();
        subscribeContentChange();
    }


    public void subscribeContentChange(){
        editor.subscribeEvent(ContentChangeEvent.class, (event, unsubscribe) -> {
            setModified();
            unsubscribe.unsubscribe();
        });
    }

    public void setActive(){
        tabBar.setBackgroundColor(0xffffff00);
        //tabNameTv.setTextColor(0xffffff00);
    }
    public void resetActive(){
        tabBar.setBackgroundColor(0x00000000);
        //tabNameTv.setTextColor(0xff000000);
    }

    public void toggleReadOnly(){
        editor.setEditable(isReadOnly);
        isReadOnly = !isReadOnly;
        if (isReadOnly){
            if (isModified) tabNameTv.setBackgroundColor(0xffbd5bee);
            else tabNameTv.setBackgroundColor(0xff6ca2f8);
        }
        else {
            if (isModified) tabNameTv.setBackgroundColor(0xffff9800);
            else tabNameTv.setBackgroundColor(0xff00eb00);
        }
    }

    public void setModified(){
        isModified = true;
        if (isReadOnly) tabNameTv.setBackgroundColor(0xffbd5bee);
        else tabNameTv.setBackgroundColor(0xffff9800);

    }
    public void resetModified(){
        isModified = false;
        if (isReadOnly) tabNameTv.setBackgroundColor(0xff6ca2f8);
        else tabNameTv.setBackgroundColor(0xff00eb00);
    }





    void hide(){
        editor.setVisibility(View.GONE);

    }
    void show(){
        editor.setVisibility(View.VISIBLE);
    }

    void destroy(){
        editorHandler.editorContainer.removeView(editor);
        group.projectTabContainer.removeView(tabBar);
        if (!isShadowTab) editor.release();
        editor = null;
        tabBar = null;
        try {this.finalize();}
        catch (Throwable ignored) {editorHandler.activity.toast(ignored.getMessage());}
    }




}














