package hz.cdj.game.fmj.views;

import android.graphics.Canvas;

public abstract class BaseScreen {

	public abstract void update(long delta);
	
	public abstract void draw(Canvas canvas);
	
	public abstract void onKeyDown(int key);
	
	public abstract void onKeyUp(int key);
	
	public boolean isPopup() {
		return false;
	}
}
