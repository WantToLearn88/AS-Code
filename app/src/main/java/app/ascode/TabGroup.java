package app.ascode;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import io.github.rosemoe.sora.langs.java.JavaLanguage;
import io.github.rosemoe.sora.util.ArrayList;

public class TabGroup {

    public View tabGroupLayout;
    public LinearLayout projectTabContainer;

    public EditorHandler editorHandler;

    public TextView tabGroupNameTv;
    public ArrayList<EditorTab> editorTabs;

    public String projectPath = "";


    //LinearLayout editorContainer, tabListContainer;
    public TabGroup(EditorHandler editorHandler, String projectPath) {
        this.editorHandler = editorHandler;
        this.projectPath = projectPath;
        createProject();
    }


    private void createProject() {
        editorTabs = new ArrayList<>();

        tabGroupLayout = editorHandler.activity.getLayoutInflater().inflate(R.layout.tab_group_layout, null);
        tabGroupNameTv = tabGroupLayout.findViewById(R.id.tab_group_name_tv);
        projectTabContainer = tabGroupLayout.findViewById(R.id.tab_group_container);

        String projectName = projectPath.substring(projectPath.lastIndexOf("/") + 1);
        tabGroupNameTv.setText(projectName);
        tabGroupNameTv.setOnClickListener(v->{
            editorHandler.onClickProjectGroup(projectPath);
        });

        editorHandler.projectContainer.addView(tabGroupLayout);
    }

    public void openFile(File file) {
        for (EditorTab tab : editorTabs) {
            if (file.getPath().equals(tab.file.getPath())){
                if (tab.isShadowTab) tab.setContent();
                showTab(tab);
                return;
            }
        }
        createTab(file);
    }


    public EditorTab createShadowTab(File file) {
        EditorTab editorTab = new EditorTab(this);
        editorTab.createTab(file);
        editorTabs.add(editorTab);
        return editorTab;
        //editorTab.attachTab();
    }

    private void createTab(File file) {
        EditorTab editorTab = new EditorTab(this);
        editorTab.createTab(file);
        editorTab.setContent();
        editorTabs.add(editorTab);
        showTab(editorTab);
        editorHandler.storeTabChanges(this);
        //editorTab.attachToView();
    }

    public EditorTab showFirstTab() {
        if (editorTabs.isEmpty()) return null;
        EditorTab activeTab = editorHandler.activeTab;
        if (activeTab != null) {
            activeTab.hide();
            activeTab.resetActive();
        }
        EditorTab tab = editorTabs.get(0);
        tab.show();
        tab.setActive();
        return tab;
    }

    void showTab(EditorTab tab) {
        EditorTab activeTab = editorHandler.activeTab;
        if (tab == activeTab) return;
        if (activeTab != null) {
            activeTab.hide();
            activeTab.resetActive();
        }
        else if (editorHandler.isUntitledTab){
            editorHandler.untitledTab.hide();
            editorHandler.untitledTab.resetActive();
        }
        tab.show();
        tab.setActive();
        editorHandler.activeTab = tab;
    }

    public void closeProject() {
        for (EditorTab tab : editorTabs) {
            tab.destroy();
        }
        editorTabs.clear();
        editorHandler.projectContainer.removeView(tabGroupLayout);
        try {this.finalize();}
        catch (Throwable ignored) {editorHandler.activity.toast(ignored.getMessage());}
    }




    public boolean isModified(){
        for (EditorTab tab : editorTabs) {
            if (tab.isModified) return true;
        }
        return false;
    }


}


















