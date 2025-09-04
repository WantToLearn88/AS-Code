package app.ascode;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PopupManager {

    EditorActivity activity;
    RelativeLayout popupWindow;
    View activeLayout, projectOptionsLayout, saveOptionsLayout, buildOptionsLayout, tabOptionsLayout, bottomMenu;

    int displayWidth = 1080;
    int displayHeight = 2460;

    int windowWidth = displayWidth;
    int windowHeight = displayHeight;

    int defaultPopupWidth, popupWidth;

    PopupManager(EditorActivity activity){
        this.activity = activity;

    }

    void initPopupManager(){
        defaultPopupWidth = getPxFromDp(80);
        popupWindow = (RelativeLayout) find(R.id.popup_window);
        projectOptionsLayout = find(R.id.project_options);
        saveOptionsLayout = find(R.id.save_options);
        buildOptionsLayout = find(R.id.build_options);
        tabOptionsLayout = find(R.id.tab_options);
        bottomMenu = find(R.id.bottom_menu);
    }
    public int getPxFromDp(float dp) {
        return (int) (dp * activity.getResources().getDisplayMetrics().density + 0.5f);
    }

    void onRotate(boolean landscape){
        if (landscape) {
            windowWidth = displayHeight;
            windowHeight = displayWidth;
        }
        else {
            windowWidth = displayWidth;
            windowHeight = displayHeight;
        }
        bottomMenu.invalidate();
    }


    View find(int i){ return activity.findViewById(i); }



    public void hidePopupWindow(){
        hide(popupWindow);
        if (activeLayout != null) hide(activeLayout);
    }
    void showProjectOptions(View anchor){
        show(projectOptionsLayout, anchor);

    }
    void showSaveOptions(View anchor){
        show(saveOptionsLayout, anchor);
    }
    void showBuildOptions(View anchor){
        show(buildOptionsLayout, anchor);
    }
    public void showTabOptions(View anchor){
        show(tabOptionsLayout, anchor);
    }


    void hide(View v){
        v.setVisibility(View.GONE);
    }

    void show(View v){
        v.setVisibility(View.VISIBLE);
    }

    void show(View popup, View anchor){
        show(popupWindow);
        movePopupLayout(popup, anchor);

        show(popup);

        activeLayout = popup;
    }

    void movePopupLayout(View popup, View anchor){
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        int anchorX = location[0];
        int anchorY = location[1];
        int anchorWidth = anchor.getWidth();
        int anchorHeight = anchor.getHeight();

        int popupX = anchorX;
        int popupY = anchorY + anchorHeight;


        int popupWidth = popup.getLayoutParams().width;
        if (popupWidth < 1) popupWidth = defaultPopupWidth;

        int popupHeight = popup.getLayoutParams().height;
        if (popupHeight < 1) popupHeight = defaultPopupWidth;


        if (popupX + popupWidth > windowWidth) popupX -= popupWidth;
        if (popupX < 0) popupX = 0;
        if (popupY + popupHeight > windowHeight) popupY -= (popupHeight + anchorHeight);
        if (popupY < 0) popupY = 0;

        popup.setX(popupX);
        popup.setY(popupY);

    }




    void toast(String text){
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }


}


















