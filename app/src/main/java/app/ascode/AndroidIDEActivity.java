package app.ascode;

import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import app.ascode.filesystem.AndroidProjectBuilder;

public class AndroidIDEActivity extends EditorActivity {

    AndroidProjectBuilder projectBuilder;
    TextView buildOutputLogTv;


    @Override
    public void setActivityLayout(int layout) {
        super.setActivityLayout(R.layout.activity_ide);

    }

    @Override
    public boolean isIde(){
        return true;

    }
    public String getActivityLabel() {
        return "AS-IDE";
    }

 
    public void initViews(){
        buildOutputLogTv = findViewById(R.id.build_output_log_tv);
    }

    @Override
    protected void onDestroy() {
        if (projectBuilder != null) projectBuilder.unregister();
        super.onDestroy();
    }

    public void runProject(View v) {
        saveAll();
        runGradleBuild("run");
    }
    public void installProject(View v) {
        saveAll();
        runGradleBuild("install");
    }
    public void buildProject(View v) {
        saveAll();
        runGradleBuild("build");
    }

    private void runGradleBuild(String task) {
        if (projectBuilder == null) projectBuilder = new AndroidProjectBuilder(this);
        updateBuildOutputLogs("Gradle Task: " + task);
        projectBuilder.runGradleBuild(task);
    }

    public void onReceiveBuildOutput(String outputLog, String buildResult) {
        updateBuildOutputLogs(outputLog);
        toast(buildResult);
        if (buildResult.equals("failed") && !isBottomMenuVisible) {
            isBottomMenuVisible = true;
            show(bottomMenu);
        }
    }


    public void updateBuildOutputLogs(String output) {
        buildOutputLogTv.setText(output);
    }



}


















