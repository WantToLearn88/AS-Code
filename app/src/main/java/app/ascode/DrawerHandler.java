package app.ascode;

import android.view.*;
import android.widget.*;


public class DrawerHandler {

	EditorActivity activity;
	LinearLayout leftDrawer;
	LinearLayout projectDrawer;

	EditorHandler editorHandler;
	View leftSwiper, rightSwiper;
	int rightTopHalf = 500;
	boolean isLeftOpen = true;
	boolean isRightOpen = false;



	DrawerHandler(EditorActivity activity){
		this.activity = activity;

	}
	
	void initDrawerHandler(){
		editorHandler = activity.editorHandler;
		leftDrawer = activity.findViewById(R.id.left_drawer);

		projectDrawer = activity.findViewById(R.id.project_drawer);

		leftSwiper = activity.findViewById(R.id.left_swiper);
		rightSwiper = activity.findViewById(R.id.right_swiper);
		//rightTopHalf = rightSwiper.getLayoutParams().height / 2;
		View leftCloser = activity.findViewById(R.id.left_closer);
		View rightCloser = activity.findViewById(R.id.project_drawer_closer);

		leftCloser.setOnTouchListener((v, e) -> {
			closeLeft();
			return true;
		});

		rightCloser.setOnTouchListener((v, e) -> {
			closeRight();
			return true;
		});
	}

	void enableSwipers(boolean enableLeft, boolean enableRight){
		if (enableLeft){
			leftSwiper.setOnTouchListener((v, e) -> {
				if ((e.getAction() == MotionEvent.ACTION_UP) && (e.getX() > 31)) {
					openLeft();
				}
				return true;
			});
		}
		else leftSwiper.setOnTouchListener(null);
			
		if (enableRight){
			rightSwiper.setOnTouchListener((v, e) -> {
				if ((e.getAction() == MotionEvent.ACTION_UP) && (e.getX() < -3)) {
					if(e.getY() < rightTopHalf) activity.onSwipeRightTop();
					else openRight();
				}
				return true;
			});
		}
		else rightSwiper.setOnTouchListener(null);
		
	}
	public void toggleLeftDrawer() {
		if (isLeftOpen) closeLeft();
		else {
			openLeft();
			if (isRightOpen) closeRight();
		}
	}
	public void toggleRightDrawer() {
		if (isRightOpen) closeRight();
		else {
			openRight();
			if (isLeftOpen) closeLeft();
		}
	}

	void onBackPressed(){
		if (isLeftOpen) closeLeft();
		else if (isRightOpen) closeRight();
		else openRight();

	}

	public void openLeft(){
		editorHandler.clearFocus(); LeftDrawer(View.VISIBLE); isLeftOpen = true; }
	public void closeLeft(){LeftDrawer(View.GONE); isLeftOpen = false;}
	public void openRight(){
		editorHandler.clearFocus(); RightDrawer(View.VISIBLE); isRightOpen = true;  }
	public void closeRight(){RightDrawer(View.GONE); isRightOpen = false; }

	void RightDrawer(int visibility){
		projectDrawer.setVisibility(visibility);
	}

	void LeftDrawer(int visibility){ leftDrawer.setVisibility(visibility); }
	
}










