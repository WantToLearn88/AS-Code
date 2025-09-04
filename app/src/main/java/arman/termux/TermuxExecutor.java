package arman.termux;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import arman.common.infocodes.InfoCode;

public class TermuxExecutor {
    String termuxPrivateDir = "/data/data/com.termux/files/";
    String launcherDir = termuxPrivateDir + "home/launcher/";
    String termuxBinDir = termuxPrivateDir + "usr/bin/";
    Context context;

    public TermuxExecutor(Context context){
        this.context = context;
    }

    public void launch(String launcher) {
        runCommandInBackground(new String[]{launcherDir + launcher});
    }

    public void openAndroidStudio(String filePath) {
        runCommandInBackground(new String[]{launcherDir + "android-studio-launcher", filePath});

    }

    public void runGradleBuild(String apkBuilderPath, String task) {
        runCommandInBackground(new String[]{launcherDir + "gradle-apk-builder", apkBuilderPath, task});

    }

    public void runGradleBuild(String apkBuilderPath, String task, PendingIntent pendingIntent) {
        runCommandInBackground(new String[]{launcherDir + "gradle-apk-builder", apkBuilderPath, task}, pendingIntent);

    }


    public void openNeoVim(String filePath) {
        runCommandInForeground(new String[]{launcherDir + "neovim-launcher", filePath});
    }


    public void runCommandInBackground(String[] command) {
        runCommand(command, null, true);

    }
    public void runCommandInForeground(String[] command) {
        runCommand(command, null, false);

    }
    public void runCommandInBackground(String[] command, PendingIntent resultIntent) {
        runCommand(command, resultIntent, true);

    }
    public void runCommand(String[] command, PendingIntent resultIntent, boolean isBackground) {
        Intent i = new Intent("com.termux.RUN_COMMAND").setClassName("com.termux", "com.termux.app.RunCommandService");

        String executable = command[0];

        i.putExtra("com.termux.RUN_COMMAND_PATH", executable);

        int argLen = command.length;
        if (argLen > 1) {
            ArrayList<String> arrayList = new ArrayList<>();
            for (int x = 1; x < argLen; x++) {
                String arg = command[x];
                if (arg != null) arrayList.add(arg);
            }
            Object[] objects = arrayList.toArray();
            String[] args = Arrays.copyOf(objects, objects.length, String[].class);
            i.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", args);
            InfoCode.log(Arrays.toString(args));
        }
        if (resultIntent != null) {
            //toast("pending isBroadcast: " + resultIntent.isBroadcast());
            i.putExtra("pendingIntent", resultIntent);
        }
        //else { toast("pending is null" ); }

        i.putExtra("com.termux.RUN_COMMAND_BACKGROUND", isBackground);
        context.startService(i);
    }



    public void openX11() {
        Intent i = new Intent();
        ComponentName cn = new ComponentName("com.termux.x11", "com.termux.x11.MainActivity");
        i.setComponent(cn);
        context.startActivity(i);
    }
    
    public void toast(String text){Toast.makeText(context, text, Toast.LENGTH_SHORT).show();}





}
