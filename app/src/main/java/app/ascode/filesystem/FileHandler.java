package app.ascode.filesystem;

import java.util.*;
import java.io.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.view.*;
import android.widget.*;


import android.*;
import android.content.pm.*;
import app.ascode.*;
import app.ascode.R;
import arman.common.infocodes.InfoCode;
import arman.common.io.ContentUriConverter;

public class FileHandler{

	public final String CODE_PROJECTS = "CODE_PROJECTS";
	public final String ANDROID_PROJECTS = "ANDROID_PROJECTS";

	TextView dirUp;
	ListView listView;
	View selectedItemView;

	SharedPreferences sp;

	EditorActivity activity;
	EditorHandler editorHandler;
	DrawerHandler drawerHandler;

	public ArrayList<File> files = new ArrayList<>();
	public LinkedList<File> linkedList = new LinkedList<>();
	public ArrayList<String> projectPaths;

	public File selectedFile, clickedFile;

	public String sdCard, root, defaultRoot = "", defaultAndroidRoot = "/Working/Android-Projects/MainProjects", currentProjectDir = "", defaultPath = defaultRoot, projectQuery = CODE_PROJECTS;

	public int currentProjectIndex = 0, selectedItemPosition;

	boolean isIDE = false;



	public FileHandler(EditorActivity activity){
		this.activity = activity;

	}
	
	
	
	public void initFileHandler(){
		editorHandler = activity.editorHandler;
		drawerHandler = activity.drawerHandler;
		sp = activity.sp;

		projectPaths = editorHandler.projectPaths;

		dirUp = activity.findViewById(R.id.dir_up);
		
		dirUp.setOnClickListener(v -> {
			if(linkedList.size() == 1){return;}
			linkedList.removeLast();
			refreshList();
		});

		sdCard = Environment.getExternalStorageDirectory().getPath();
		validateRootDir(defaultRoot, true);

		listView = activity.findViewById(R.id.file_list_view);
		listView.setAdapter(new FileArrayAdapter(activity, files));
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				tryOpeningFile(files.get(position));
			}
		});
		activity.registerForContextMenu(listView);

	}

	public boolean validateRootDir(String rootPath, boolean forceCreate){
		File rootDir = new File(sdCard + rootPath);
		boolean result = rootDir.exists();
		if (!result && forceCreate){
			result = rootDir.mkdirs();
			if (!result) toast("File Access Error!");
		}
		return result;
	}
    
    public void restoreProjects(){
		if (activity.isIDE) {
			projectQuery = ANDROID_PROJECTS;
			defaultPath = defaultAndroidRoot;
		}

		String[] projectArr = getStringFromSP(projectQuery);
		for (String projectPath : projectArr) {
			if (projectPath.equals("")) continue;
			boolean isValidDir = validateRootDir(projectPath, false);
			if (isValidDir) {
				int projectIndex = projectPaths.size();
				editorHandler.createProject(projectPath);
				String[] tabArr = getStringFromSP(projectPath);
				for (String tabPath : tabArr) {
					if (tabPath.equals("")) continue;
					File file = new File(tabPath);
					if (file.exists()) {
						editorHandler.createShadowTab(projectIndex, file);
					}
				}
			}
		}
		populateProjectFiles();
	}

	String[] getStringFromSP(String query){
		String paths = activity.sp.getString(query, "");
		return paths.split("/ /");
	}


	public void handleIntent(Intent i){
		String category = i.getCategories().toString();
		if (!category.contains("category.DEFAULT")) return;
		Uri uri = i.getData();
		//if (uri == null) return;
		String path = uri.getPath();
		String filePath = "";

		filePath = new ContentUriConverter().getAbsoluteFilePath(uri);
		if (filePath.equals("")){
			activity.toast("You have to use InputStream to read this contentUri");
            return;
		}
		int nd = filePath.lastIndexOf("/");
		String dirPath = filePath.substring(0, nd);
		String fileName = filePath.substring(nd);

		int index = projectPaths.indexOf(dirPath);
		if(currentProjectIndex == index) return;
		if (index < 0){
			index = projectPaths.size();
			editorHandler.createProject(dirPath);
            storeOpenedProjects();
		}
        currentProjectIndex = index;
        populateProjectFiles();
		tryOpeningFilePath("/sdcard" + filePath);
	}

	public void populateProjectFiles() {
		String path = defaultPath;
		if (!projectPaths.isEmpty()) {
			//toast("" + projects.size());
			path = projectPaths.get(currentProjectIndex);
			//toast(path);
		}
		/*if (isIDE) createNewProjectRoot(path);
		else createSubLinkedList("", path);*/
		createNewProjectRoot(path);
		refreshList();
	}


	public void createNewProjectRoot(String dirPath){
		linkedList.clear();
		root = sdCard + dirPath;
		File projectRoot = new File(root);
		linkedList.add(projectRoot);
		currentProjectDir = dirPath;
		activity.onProjectChange(projectRoot.getName());

	}

	public void createSubLinkedList(String rootDir, String subDir){
		linkedList.clear();
		root = sdCard + rootDir;
		linkedList.add(new File(root));
		if (subDir.equals("")) return;
		String[] list = subDir.substring(1).split("/");
		int len = list.length;
		if (len > 0) {
			list = Arrays.copyOf(list, len);
			for (String dir : list) {
				root += "/" + dir;
				linkedList.add(new File(root));
			}
		}
	}


	public ListView getListView(){
		return listView;
	}

	public void getNewFiles(File file){
		File[] fileArray = file.listFiles();
		new FileSorter().sort(fileArray);
		for(File f : fileArray){
			files.add(f);
		}
		//files = (ArrayList<File>) Arrays.asList(file.listFiles());

	}

	public void onClickFile(){
		if(clickedFile.isDirectory()){
			linkedList.add(clickedFile);
			refreshList();
		}
		else{
			actionOpenFile();
		}
	}

	private void tryOpeningFilePath(String filePath){
		tryOpeningFile(new File(filePath));
	}

	private void tryOpeningFile(File file){
		if (!file.exists()) {
			toast(file.getPath());
			return;
		}
		clickedFile = file;
		onClickFile();
	}

	private void actionOpenFile(){
		if(isEditable(clickedFile)){
			openClickedFile();
		}
	}

	public void refreshList(){
		files.clear();
		getNewFiles(linkedList.getLast());
		listView.invalidateViews();
	}

	public String getSelectedFileName(ContextMenu.ContextMenuInfo info){
		selectedFile = files.get(((AdapterView.AdapterContextMenuInfo) info).position);
		return selectedFile.getName();
	}


	public void openProjectPath(String path) {
		int index = projectPaths.indexOf(path);
		if (index == currentProjectIndex) return;
		currentProjectIndex = index;
		createNewProjectRoot(path);
		refreshList();
	}

	public void chooseNewProject() {
		createSubLinkedList("", defaultPath);
		refreshList();
	}
	public void cancelProjectChoice() {
		populateProjectFiles();
	}

	public boolean openAsProject() {
		File dir = linkedList.getLast();
		String path = dir.getPath().replace(sdCard, "");
		if (isIDE){
			List<String> items = Arrays.asList(dir.list());
			if (!items.contains("settings.gradle")) {
				populateProjectFiles();
				return false;
			}
		}

		currentProjectIndex = projectPaths.indexOf(path);
		if (currentProjectIndex < 0) {
			currentProjectIndex = projectPaths.size();
			editorHandler.createProject(path);
		}
		storeOpenedProjects();
		populateProjectFiles();
		return true;
	}


	public void closeProject() {
		if (projectPaths.isEmpty()) return;
		editorHandler.closeProject(currentProjectIndex);
		storeOpenedProjects();
		int size = projectPaths.size();
		if (currentProjectIndex == size) currentProjectIndex--;
		populateProjectFiles();

	}

	public void storeOpenedProjects() {
		SharedPreferences.Editor editor = sp.edit();
		String paths = /*projects.isEmpty() ? "" :*/ String.join("/ /", projectPaths);
		//toast(currentRoot);
		editor.putString(projectQuery, paths);
		editor.apply();

	}


	public void storeTabChanges(String projectPath, String tabPaths) {
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(projectPath, tabPaths);
		editor.apply();
	}

	public void removeStoredProject(String projectPath) {
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(projectPath);
		editor.apply();

	}


	public void gotoHomeDir() {
		if (projectPaths.isEmpty()) return;
		createNewProjectRoot(currentProjectDir);
		refreshList();
	}
	public void gotoSrcDir() {
		if (projectPaths.isEmpty()) return;
		createSubLinkedList(currentProjectDir, "/app/src/main");
		refreshList();
	}
	public void gotoJavaDir() {
		if (projectPaths.isEmpty()) return;
		createSubLinkedList(currentProjectDir, "/app/src/main/java");
		refreshList();
	}
	public void gotoResDir() {
		if (projectPaths.isEmpty()) return;
		createSubLinkedList(currentProjectDir, "/app/src/main/res");
		refreshList();
	}
	public void gotoEditingDir() {
		if (projectPaths.isEmpty()) return;
		File file = editorHandler.getActiveFile();
		if (file == null) return;
		String filePath = file.getPath().replace(sdCard, "");
		String matchedProject = "";
		for (String project : projectPaths) {
			if (filePath.startsWith(project)){
				if (matchedProject.length() < project.length()){
					matchedProject = project;
				}
			}
		}
		if (matchedProject.equals("")) return;
		currentProjectIndex = projectPaths.indexOf(matchedProject);
		createNewProjectRoot(matchedProject);
		filePath = filePath.replace(matchedProject, "");
		filePath = filePath.substring(0, filePath.lastIndexOf("/"));
		createSubLinkedList(matchedProject, filePath);
		refreshList();
	}



	public void renameSelectedFile(String rename){
		String dir = selectedFile.getPath();
		dir = dir.substring(0, dir.lastIndexOf(File.separator));
		//toast(dir);
		File newFile = new File(dir, rename);
		if (selectedFile.renameTo(newFile)){
			toast("renamed");
			refreshList();
			editorHandler.onRename(selectedFile, newFile);
		}
		else toast("failed");
	}

	public void deleteSelectedFile(){
		if (selectedFile.delete()) {
			toast("deleted");
			refreshList();
			editorHandler.onDelete(selectedFile);
		}
		else toast("failed");
	}

	public void newDoc(){
		drawerHandler.closeRight();
		editorHandler.createUntitledTab();
	}
	
	
	boolean isEditable(File file){
		String ext = getExt(file.getName());
		long size = file.length();
		//editor.setText(ext + " file. size: "+ size);
		if(size < 2048*1024/* && Arrays.asList(supportedFileType).contains(ext)*/){
			return true;	
		}
		return false;
	}
	
	String getExt(String name){
		return name.substring(name.lastIndexOf(".")+1);
	}


	public boolean save(EditorTab tab){
		File file = tab.file;
		if (file == null || !file.exists()) return false;
		BufferedWriter bw;
		try{
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(tab.getContent());
			bw.close();
			tab.onSaved();
			return true;
		}
		catch (IOException e){toast("Error");}
		return false;
	}

	public void saveAs(String name, EditorTab activeTab) {
		if (name == null || name.equals("")) {
			activity.showSaveAsDialogue();
			toast("Give it a Name!");
			return;
		}
		String ext = ".txt";
		if (name.lastIndexOf(".") > 0) {
			ext = name.substring(name.lastIndexOf("."));
			name = name.substring(0, name.lastIndexOf("."));
		}
		String parent = activeTab.file.getParent();
		File tampFile = getNewFile(parent, name, ext);
		EditorTab newTab = editorHandler.createShadowTab(activeTab.group, tampFile);

		boolean isSuccess = saveAsNewTab(newTab, activeTab);
		if (isSuccess) toast("File Saved!");
		refreshList();
	}
	public boolean saveAsNewTab(EditorTab newTab, EditorTab activeTab){
		File file = newTab.file;
		if (file == null) return false;
		BufferedWriter bw;
		try{
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(activeTab.getContent());
			bw.close();
			editorHandler.storeTabChanges(newTab.group);
			return true;
		}
		catch (IOException e){
			InfoCode.log(e.getLocalizedMessage());
			toast("Error");}
		return false;
	}
	
	File getNewFile(String name, String ext){
		String currentRoot = linkedList.getLast().toString();
		return getNewFile(currentRoot, name, ext);
	}

	File getNewFile(String root, String name, String ext){
		int i =1;
		File file = new File( root + "/" + name + ext);

		while(true){
			if(file.exists()){
				file = new File(getNewName(name, ext, i++));
			}
			else{
				return file;
			}
		}
	}
	
	String getNewName(String name, String ext, int i){
		String currentRoot = linkedList.getLast().toString();
		return currentRoot + "/" + name + i + ext;
	}

    
	/*public void openFile(String filePath){
		openFile(new File(filePath));
	}*/

	public void openClickedFile(){
		openFile(clickedFile);
	}
	
	
	private void openFile(File file){
		drawerHandler.closeLeft();
		editorHandler.openFile(file);
	}

	public String readFile(File file){
		StringBuffer buffer = new StringBuffer();
		String line = "";
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

			while ((line = reader.readLine()) != null) {
				buffer.append(line + "\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return buffer.toString();
	}
	
	
	
	private boolean checkPermission(){
        int result = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else
            return false;
    }

    private void requestPermission(){
        if(activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(activity,"Storage permission is requires,please allow from settings",Toast.LENGTH_SHORT).show();
        }else
			activity.requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},111);
    }


	void toast(String s){ Toast.makeText(activity, s, Toast.LENGTH_SHORT).show(); }
	
	

}
