package hz.cdj.game.fmj.combat.actions;

import hz.cdj.game.fmj.characters.FightingCharacter;
import android.graphics.Canvas;

public abstract class Action {
	
	/** 动作的发起者*/
	protected FightingCharacter mAttacker;
	
	private static final int DELTA = 1000 / 20;
	private long mTimeCnt = 0;
	protected int mCurrentFrame = 0;
	
	/**
	 * 动作产生的影响，播放动作动画之前执行一次。
	 */
	public void preproccess() {
		
	}
	
	/**
	 * 隐藏死亡角色
	 */
	public abstract void postExecute();
	
	protected abstract boolean updateRaiseAnimation(long delta);
	
	protected abstract void drawRaiseAnimation(Canvas canvas);

	/**
	 * 
	 * @param delta
	 * @return 执行完毕返回<code>false</code>，否则返回<code>true</code>
	 */
	public boolean update(long delta) {
		mTimeCnt += delta;
		if (mTimeCnt >= DELTA) {
			++mCurrentFrame;
			mTimeCnt = 0;
		}
		return true;
	}
	
	public void draw(Canvas canvas) {}
	
	/**
	 * 
	 * @return 动作发起者的身法
	 */
	public int getPriority() {
		return mAttacker.getSpeed();
	}
	
	public boolean isAttackerAlive() {
		return mAttacker.isAlive();
	}
	
	public abstract boolean isTargetAlive();
	
	public abstract boolean isTargetsMoreThanOne();
	
	public abstract boolean targetIsMonster();
	
}
