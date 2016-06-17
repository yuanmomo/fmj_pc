package hz.cdj.game.fmj.characters;

import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResImage;
import android.graphics.Canvas;

public class FightingSprite {

	private ResImage mImage;
	
	private int mCurrentFrame = 1;

	/** 在战斗场景中的屏幕坐标*/
	private int mCombatX, mCombatY;
	
	public FightingSprite(int resType, int index) {
		if (resType == DatLib.RES_ACP) { // 怪物的
			mImage = (ResImage)DatLib.GetRes(DatLib.RES_ACP, 3, index);
		} else if (resType == DatLib.RES_PIC) { // 玩家角色的
			mImage = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 3, index);
		} else {
			throw new IllegalArgumentException("resType 有错.");
		}
	}
	
	public void draw(Canvas canvas) {
		mImage.draw(canvas, mCurrentFrame, mCombatX - mImage.getWidth() / 2,
				mCombatY - mImage.getHeight() / 2);
	}
	
	public void draw(Canvas canvas, int x, int y) {
		mImage.draw(canvas, mCurrentFrame, x, y);
	}

	public void setCombatPos(int x, int y) {
		mCombatX = x;
		mCombatY = y;
	}
	
	public void move(int dx, int dy) {
		mCombatX += dx;
		mCombatY += dy;
	}
	
	public int getCombatX() {
		return mCombatX;
	}
	
	public int getCombatY() {
		return mCombatY;
	}
	
	public int getWidth() {
		return mImage.getWidth();
	}
	
	public int getHeight() {
		return mImage.getHeight();
	}
	
	public int getCurrentFrame() {
		return mCurrentFrame;
	}
	
	/**
	 * 
	 * @param i > 0
	 */
	public void setCurrentFrame(int i) {
		mCurrentFrame = i;
	}
	
	public int getFrameCnt() {
		return mImage.getNumber();
	}
}
