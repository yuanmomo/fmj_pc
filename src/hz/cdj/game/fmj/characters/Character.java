package hz.cdj.game.fmj.characters;

import hz.cdj.game.fmj.lib.ResBase;
import android.graphics.Canvas;
import android.graphics.Point;

public abstract class Character extends ResBase {
	
	String mName = "";
	
	public void setName(String name) {
		mName = name;
	}
	
	public String getName() {
		return mName;
	}

	/**
	 * 角色的动作状态
	 */
	/**
	 * 停止状态，不作运动驱动
	 */
	public static final int STATE_STOP = 0;
	/**
	 * 强制移动状态，效果同2
	 */
	public static final int STATE_FORCE_MOVE = 1;
	/**
	 * 巡逻状态，自由行走
	 */
	public static final int STATE_WALKING = 2;
	/**
	 * 暂停状态，等到延时到了后转变为巡逻状态
	 */
	public static final int STATE_PAUSE = 3;
	/**
	 * 激活状态，只换图片，不改变位置（适合动态的场景对象，比如：伏魔灯）
	 */
	public static final int STATE_ACTIVE = 4;
	
	private int mState = STATE_STOP;
	
	public int getCharacterState() {
		return mState;
	}
	
	public void setCharacterState(int state) {
		mState = state;
	}
	
	/**
	 * 角色在地图中的位置
	 */
	private Point mPosInMap = new Point();
	
	public Point getPosInMap() {
		return mPosInMap;
	}
	
	public void setPosInMap(int x, int y) {
		mPosInMap.set(x, y);
	}
	
	public Point getPosOnScreen(Point posMapScreen) {
		return new Point(mPosInMap.x - posMapScreen.x,
				mPosInMap.y - posMapScreen.y);
	}
	
	public void setPosOnScreen(Point p, Point posMapScreen) {
		mPosInMap.set(p.x + posMapScreen.x, p.y + posMapScreen.y);
	}
	
	public void setPosOnScreen(int x, int y, Point posMapScreen) {
		mPosInMap.set(x + posMapScreen.x, y + posMapScreen.y);
	}
	
	/**
	 * 角色在地图中的面向
	 */
	private Direction mDirection = Direction.South;
	
	public Direction getDirection() {
		return mDirection;
	}
	
	public void setDirection(Direction d) {
		mDirection = d;
		mWalkingSprite.setDirection(d);
	}
	
	/**
	 * 角色行走图
	 */
	private WalkingSprite mWalkingSprite;
	
	public int getWalkingSpriteId() {
		return mWalkingSprite.getId();
	}
	
	public void setWalkingSprite(WalkingSprite sprite) {
		mWalkingSprite = sprite;
		mWalkingSprite.setDirection(getDirection());
	}
	
	public void walk() {
		mWalkingSprite.walk();
		updatePosInMap(mDirection);
	}
	
	public void walk(Direction d) {
		if (d == getDirection()) {
			mWalkingSprite.walk();
		} else {
			mWalkingSprite.walk(d);
			setDirection(d);
		}
		updatePosInMap(d);
	}
	
	private void updatePosInMap(Direction d) {
		switch (d) {
		case East: mPosInMap.x++; break;
		case West: mPosInMap.x--; break;
		case North: mPosInMap.y--; break;
		case South: mPosInMap.y++; break;
		}
	}
	
	/**
	 * 原地踏步
	 */
	public void walkStay(Direction d) {
		if (d == getDirection()) {
			mWalkingSprite.walk();
		} else {
			mWalkingSprite.walk(d);
			setDirection(d);
		}
	}

	/**
	 * 原地踏步，面向不变
	 */
	public void walkStay() {
		mWalkingSprite.walk();
	}
	
	/**
	 * 设置脚步
	 * @param step 0—迈左脚；1—立正；2—迈右脚
	 */
	public void setStep(int step) {
		mWalkingSprite.setStep(step);
	}
	
	public int getStep() {
		return mWalkingSprite.getStep();
	}
	
	public void drawWalkingSprite(Canvas canvas, Point posMapScreen) {
		Point p = getPosOnScreen(posMapScreen);
		mWalkingSprite.draw(canvas, p.x * 16, p.y * 16);
//		if (p.x >= 0 && p.x <= 9 && p.y >= 0 && p.y <= 5) {
//			mWalkingSprite.draw(canvas, p.x * 16, p.y * 16);
//		}
	}
}
