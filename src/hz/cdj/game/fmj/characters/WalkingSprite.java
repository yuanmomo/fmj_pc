package hz.cdj.game.fmj.characters;

import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResImage;
import android.graphics.Canvas;

public class WalkingSprite {
	
	private static final int[] OFFSET = {0,1,2,1};
	
	private ResImage mResImage;
	
	private int mOffset = 1; // 面向
	private int mI = 0; // 脚步

	public WalkingSprite(int type, int id) {
		mResImage = (ResImage)DatLib.getInstance().getRes(DatLib.RES_ACP,
				type, id);
	}
	
	public int getId() {
		return mResImage.getIndex();
	}
	
	public void setDirection(Direction d) {
		switch (d) {
		case North: mOffset = 1; break;
		case East: mOffset = 4; break;
		case South: mOffset = 7; break;
		case West: mOffset = 10; break;
		}
	}
	
	public void walk(Direction d) {
		setDirection(d);
		walk();
	}
	
	public void walk() {
		++mI;
		mI %= 4;
	}
	
	public void setStep(int step) {
		mI = step % 4;
	}
	
	public int getStep() {
		return mI;
	}
	
	public void draw(Canvas canvas, int x, int y) {
		y = y + 16 - mResImage.getHeight();
		if (x + mResImage.getWidth() > 0 && x < 160 - 16 &&
				y + mResImage.getHeight() > 0 && y < 96) {
			mResImage.draw(canvas, mOffset + OFFSET[mI], x + Global.MAP_LEFT_OFFSET, y);
		}
	}
}
