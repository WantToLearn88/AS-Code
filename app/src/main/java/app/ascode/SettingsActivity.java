package app.ascode;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;


public class SettingsActivity extends Activity {


    SharedPreferences sp;
    EditText fontSizeET;
    Switch swiperLeftSW, swiperRightSW, topbarSW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        initialize();
    }

	private void initialize(){
		sp = getSharedPreferences("settings", 0);

        fontSizeET = findViewById(R.id.font_size_et);
        swiperLeftSW = findViewById(R.id.left_swiper_switch);
		swiperRightSW = findViewById(R.id.right_swiper_switch);
        topbarSW = findViewById(R.id.topbar_switch);

		applySettings();
	}
	private void applySettings(){
        fontSizeET.setText(""+sp.getInt("fontSize", 20));
		swiperLeftSW.setChecked(sp.getBoolean("enableLeftSwiper", true));
		swiperRightSW.setChecked(sp.getBoolean("enableRightSwiper", true));
		topbarSW.setChecked(sp.getBoolean("enableTopbar", true));

	}

    @Override
    public void onBackPressed() {
        String sFontSize = fontSizeET.getText().toString();
        int fontSize = -1;
        if (sFontSize != null || sFontSize != ""){
            fontSize = Integer.parseInt(sFontSize);
            if (fontSize >= 1){
                SharedPreferences.Editor editor = sp.edit();

                editor.putInt("fontSize", fontSize);
                editor.putBoolean("enableLeftSwiper", swiperLeftSW.isChecked());
				editor.putBoolean("enableRightSwiper", swiperRightSW.isChecked());
                editor.putBoolean("enableTopbar", topbarSW.isChecked());
                editor.apply();

                setResult(ASCodeActivity.SETTINGS_UPDATE);
            }

        }
        super.onBackPressed();
    }
}