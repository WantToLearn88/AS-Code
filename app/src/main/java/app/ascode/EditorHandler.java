package app.ascode;
import android.view.View;
import android.view.inputmethod.*;
import android.content.*;
import android.widget.LinearLayout;


import java.io.File;
import java.util.ArrayList;

import app.ascode.filesystem.FileHandler;

public class EditorHandler{

	public EditorActivity activity;
	public InputMethodManager imm;
	public PopupManager popupManager;
	public FileHandler fileHandler;
	public DrawerHandler drawerHandler;

	public LinearLayout projectContainer, editorContainer;

	public ArrayList<TabGroup> projects;
	public ArrayList<String> projectPaths = new ArrayList<>(), openedFilePaths;

	public EditorTab activeTab, openedOptionsForTab;

	public int fontSize = 14;

	public boolean isWrapped = false;





	public EditorHandler(EditorActivity activity){
		this.activity = activity;

	}

	void initEditorHandler(){
		fileHandler = activity.fileHandler;
		drawerHandler = activity.drawerHandler;
		popupManager = activity.popupManager;

		editorContainer = activity.findViewById(R.id.editor_container);
		projectContainer = activity.findViewById(R.id.peoject_group_container);

		projects = new ArrayList<>();
		openedFilePaths = new ArrayList<>();

		imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

	}

	public void createProject(String path){
		TabGroup group = new TabGroup(this, path);
		projects.add(group);
		projectPaths.add(path);
	}

	public void closeProject(int index) {
		TabGroup group = projects.get(index);
		projects.remove(index);
		projectPaths.remove(index);
		if (activeTab != null && group == activeTab.group) activeTab = null;
		fileHandler.removeStoredProject(group.projectPath);
		group.closeProject();
		refreshView();
	}


	UntitledTab untitledTab;
	boolean isUntitledTab = false;
	boolean isUntitledTabVisible = false;
	public void createUntitledTab(){
		untitledTab = new UntitledTab(this);
		untitledTab.createTab();
		if (activeTab != null) {
			activeTab.hide();
			activeTab.resetActive();
		}
		untitledTab.show();
		untitledTab.setActive();
		activeTab = null;
		isUntitledTab = true;
	}

	public void closeUntitledTab(){
		if (!isUntitledTab) return;
		int i = fileHandler.currentProjectIndex;
		if (activeTab == null) activeTab = projects.get(i).showFirstTab();
		untitledTab.destroy();
		untitledTab = null;
		refreshView();
		isUntitledTab = false;
	}
	public void showUntitledTab(){
		if (isUntitledTabVisible) return;
		if (activeTab != null) {
			activeTab.hide();
			activeTab.resetActive();
		}
		untitledTab.show();
		untitledTab.setActive();
		activeTab = null;
	}

	public void openFile(File file){
		int index = fileHandler.currentProjectIndex;
		projects.get(index).openFile(file);
	}

	public EditorTab createShadowTab(int projectIndex, File file){
		return createShadowTab(projects.get(projectIndex), file);

	}

	public EditorTab createShadowTab(TabGroup group, File file){
		return group.createShadowTab(file);
	}
	/*public void openFile(File file, String content){
		int index = fileHandler.currentProjectIndex;
		projects.get(index).openFile(file, content);
	}*/

	public void onClickProjectGroup(String path) {
		//if (drawerHandler == null) drawerHandler = activity.drawerHandler;
		drawerHandler.closeRight();
		fileHandler.openProjectPath(path);
		drawerHandler.openLeft();
	}

	public void onClickTabTitle(EditorTab tab) {
		tab.group.showTab(tab);
		//if (drawerHandler == null) drawerHandler = activity.drawerHandler;
		drawerHandler.closeRight();
	}
	public void onClickTabOption(EditorTab tab, View anchor) {
		popupManager.showTabOptions(anchor);
		openedOptionsForTab = tab;
	}





	void setOnClick(View v, int i, Runnable r){
		v.findViewById(i).setOnClickListener(c->r.run());
	}



	public void setFontSize(int size){
		fontSize = size;
		if(activeTab != null) activeTab.editor.setTextSize(size);
    }

	public boolean isEdited(){
		for (TabGroup group : projects){
			if (group.isModified()) return true;
		}
		return false;
	}

	public void setReadOnly(boolean b){
		activeTab.editor.setEditable(!b);
	}

	public void toggleReadOnly() {
		if (activeTab != null) activeTab.toggleReadOnly();
	}

	public String getContent(){
		return activeTab.getContent();
	}

	public String getActiveProjectPath(){
		if (activeTab == null) return "";
		return activeTab.group.projectPath;
	}
	public File getActiveFile(){
		if (activeTab == null) return null;
		return activeTab.file;
	}
	public String getActiveFilePath(){
		if (activeTab == null) return "";
		return activeTab.file.getPath();
	}
	public String getActiveFileName(){
		if (activeTab == null) return "";
		return activeTab.file.getName();
	}




	public void saveActiveFile(){
		if (activeTab == null) return;
		boolean isSuccess = fileHandler.save(activeTab);
		if (isSuccess) activity.toast("File Saved!");
		else activity.showSaveAsDialogue();

	}

	public void saveAsActiveTab(String name){
		if (activeTab == null) return;
		fileHandler.saveAs(name, activeTab);
	}

	public void saveAll() {
		if (projectPaths.isEmpty() || activeTab == null) return;
		ArrayList<EditorTab> editorTabs = activeTab.group.editorTabs;
		for (EditorTab tab : editorTabs){
			if (tab.isModified) fileHandler.save(tab);
		}
	}

	public boolean isCloseable() {
		return !openedOptionsForTab.isModified;

	}
	public void closeTab() {
		closeTab(openedOptionsForTab);

	}


	public void onRename(File oldFile, File newFile){
		if (projectPaths.isEmpty()) return;
		TabGroup group = projects.get(fileHandler.currentProjectIndex);
		for (EditorTab tab : group.editorTabs) {
			if (oldFile.getPath().equals(tab.file.getPath())){
				tab.setFile(newFile);
				storeTabChanges(group);
				return;
			}
		}
	}

	public void onDelete(File file){
		if (projectPaths.isEmpty()) return;
		TabGroup group = projects.get(fileHandler.currentProjectIndex);
		for (EditorTab tab : group.editorTabs) {
			if (file.getPath().equals(tab.file.getPath())){
				closeTab(tab);
				return;
			}
		}
	}

	public void closeTab(EditorTab tab) {
		closeTab(tab, true);
	}

	public void closeTab(EditorTab tab, boolean shouldStoreChanges) {
		TabGroup group = tab.group;
		group.editorTabs.remove(tab);
		if (tab == activeTab){
			try {activeTab = group.showFirstTab();}
			catch (Exception e){activeTab = null;}
		}
		if (shouldStoreChanges) storeTabChanges(group);
		tab.destroy();
		refreshView();
	}


	public void storeTabChanges(TabGroup group) {
		StringBuilder paths = new StringBuilder();
		for (EditorTab tab : group.editorTabs) {
			paths.append(tab.file.getPath());
			paths.append("/ /");
		}
		fileHandler.storeTabChanges(group.projectPath, paths.toString());
	}

	public void refreshView(){
		editorContainer.invalidate();
		projectContainer.invalidate();
	}


	public void undo(){
		if (activeTab == null || !activeTab.editor.isEditable()) return;
		activeTab.editor.undo();

	}
	public void redo(){
		if (activeTab == null || !activeTab.editor.isEditable()) return;
		activeTab.editor.redo();

	}


	public void clearFocus(){
		hideIme();
		if (activeTab == null) return;
		activeTab.editor.clearFocus();
	}
	
	public void hideIme(){
		if (imm == null) return;
		imm.hideSoftInputFromWindow(editorContainer.getWindowToken(), 0);

	}

	void hide(View v){
		v.setVisibility(View.GONE);
	}

	void show(View v){
		v.setVisibility(View.VISIBLE);
	}


	public void toggleWrap(){
		if(isWrapped){
			//editor.setMinEms(300);
		}
		else{
			//editor.setWidth(LayoutParams.MATCH_PARENT);
		}
		isWrapped = !isWrapped;
		
	}
	
	
}















