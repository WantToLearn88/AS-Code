package app.ascode.filesystem;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import arman.common.infocodes.InfoCode;


public class PluginResultsService extends IntentService {

    public static final String EXTRA_EXECUTION_ID = "execution_id";

    private static int EXECUTION_ID = 1000;

    public static final String PLUGIN_SERVICE_LABEL = "PluginResultsService";

    private static final String LOG_TAG = "PluginResultsService";

    public PluginResultsService(){
        super(PLUGIN_SERVICE_LABEL);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle resultBundle = intent.getBundleExtra("result");
        if (resultBundle == null) {
            InfoCode.log("bundle empty");
            return;
        }

        String stdout = intent.getStringExtra("stdout");
        String stderr = intent.getStringExtra("stderr");

        InfoCode.log(stdout);
        InfoCode.log(stderr);


    }

    public static synchronized int getNextExecutionId() {
        return EXECUTION_ID++;
    }

}
















