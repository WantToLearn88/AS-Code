package app.ascode;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;



public class Dialogue {

    View view;
    public Dialogue(Activity activity, ViewGroup parent){
        view = activity.getLayoutInflater().inflate(R.layout.dialogue, parent);

    }



}
