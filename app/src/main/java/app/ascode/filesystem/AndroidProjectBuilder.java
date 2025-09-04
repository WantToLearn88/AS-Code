package app.ascode.filesystem;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import arman.termux.TermuxExecutor;

import app.ascode.AndroidIDEActivity;

public class AndroidProjectBuilder {

    AndroidIDEActivity activity;
    BroadcastReceiver receiver;
	TermuxExecutor executor;

    String receiverAction = "AndroidProjectBuildResult";

    String RESULT_BUNDLE = "result";
    String STDOUT = "stdout";
    String STDOUT_LENGTH = "stdout_original_length";
    String STDERR = "stderr";
    String STDERR_LENGTH = "stderr_original_length";
    String EXIT_CODE = "exitCode";
    String ERR = "err";
    String ERRMSG = "errmsg";


    public AndroidProjectBuilder(AndroidIDEActivity ideActivity){
        activity = ideActivity;
        init();
    }

    private void init(){
		executor = new TermuxExecutor(activity);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //InfoCode.log("Received");
                String output = intent.getStringExtra("output");
                String buildResult = intent.getStringExtra("result");
                activity.onReceiveBuildOutput(output, buildResult.toLowerCase());
                //InfoCode.log(output);
            }
        };

        // Register receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(receiverAction);
        activity.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);

    }
    public void unregister(){
        activity.unregisterReceiver(receiver);
    }


    public void runGradleBuild(String task) {
        String projectPath = activity.editorHandler.getActiveProjectPath();
        if (projectPath.equals("")) {
            activity.toast("Open a tab First!");
            return;
        }
        String apkBuilderPath = activity.fileHandler.sdCard + projectPath + "/apk.builder";
        File file = new File(apkBuilderPath);
        if (!file.exists()){ createPackageInfo(file); }

		launchGradleBuilder(apkBuilderPath, task);
    }
	private void launchGradleBuilder(String apkBuilderPath, String task) {
        executor.runGradleBuild(apkBuilderPath, task/*, getPendingIntent()*/);
		
    }
    private void createPackageInfo(File tampFile) {
        BufferedWriter bw;
        try{
            bw = new BufferedWriter(new FileWriter(tampFile));
            String packageInfo = getLauncherActivity(tampFile);
            if(packageInfo.equals("")){
                activity.toast("Unable to Create PackageInfo");
                return;
            }
            bw.write(packageInfo);
            bw.close();
            activity.toast("apk.builder Created");
        }
        catch (IOException e){
            activity.toast("Error");}
    }

    private String getLauncherActivity(File tampFile) {
        String projectDir = tampFile.getParent();
        String manifest = projectDir + "/app/src/main/AndroidManifest.xml";
        File file = new File(manifest);
        StringBuffer buffer = new StringBuffer();
        String line = "";
        boolean isPackageName = false;
        boolean isInsideActivity = false;
        boolean isActivityFound = false;
        String activityName = "";
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            while ((line = reader.readLine()) != null) {
                if(!isPackageName){
                    int st = line.indexOf("package=\"") + 9;
                    if(st > 8){
                        isPackageName = true;
                        int nd = line.indexOf("\"", st);
                        line = line.substring(st, nd);
                        buffer.append(line + "/");
                        continue;
                    }
                }
                if(!isInsideActivity){
                    isInsideActivity = line.contains("<activity");
                }
                if(isInsideActivity){
                    if(!isActivityFound){
                        int st = line.indexOf("android:name=\"") + 14;
                        if(st > 13){
                            isActivityFound = true;
                            int nd = line.indexOf("\"", st);
                            activityName = line.substring(st, nd);
                        }
                    }
                    else if(line.contains("intent.category")){
                        if(line.contains("intent.category.LAUNCHER")){
                            buffer.append(activityName);
                            break;
                        }
                        isInsideActivity = false;
                        isActivityFound = false;
                    }
                }
            }
            reader.close();
            line = buffer.toString();
        }
        catch (IOException e) {
            line = "";
            activity.toast("Failed to Read Manifest");
        }
        return line;
    }

    Intent gradleLauncher(){
        Intent i = new Intent(Intent.ACTION_VIEW);
        String pkg = "app.termux.launcher";
        String cls = "app.termux.launcher.GradleBuildLauncher";
        ComponentName cn = new ComponentName(pkg, cls);
        i.setComponent(cn);
        return i;
    }

    PendingIntent getPendingIntent(){
        Intent pluginResultsServiceIntent = new Intent(activity, PluginResultsService.class);
        int executionId = PluginResultsService.getNextExecutionId();
        pluginResultsServiceIntent.putExtra(PluginResultsService.EXTRA_EXECUTION_ID, executionId);
        PendingIntent pendingIntent = PendingIntent.getService(activity, executionId, pluginResultsServiceIntent, PendingIntent.FLAG_ONE_SHOT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0));


        /*PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ideActivity, 1000,
                new Intent(receiverAction),
                PendingIntent.FLAG_ONE_SHOT |
                        PendingIntent.FLAG_IMMUTABLE);
*/
        return pendingIntent;
    }



}




















