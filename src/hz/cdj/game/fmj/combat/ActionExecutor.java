package hz.cdj.game.fmj.combat;

import hz.cdj.game.fmj.characters.FightingCharacter;
import hz.cdj.game.fmj.combat.actions.Action;
import hz.cdj.game.fmj.combat.actions.ActionFlee;
import hz.cdj.game.fmj.combat.actions.ActionSingleTarget;

import java.util.Queue;

import android.graphics.Canvas;

public class ActionExecutor {
	
	/** 被执行的动作队列*/
	private Queue<Action> mActionQueue;
	
	/** 当前执行的动作*/
	private Action mCurrentAction;
	
	private boolean mIsNewAction = true;
	
	private Combat mCombat;

	public ActionExecutor(Queue<Action> actionQueue, Combat combat) {
		mActionQueue = actionQueue;
		mCombat = combat;
	}
	
	public void reset() {
		mCurrentAction = null;
		mIsNewAction = true;
	}
	
	/**
	 * 
	 * @param delta
	 * @return 执行完毕返回<code>false</code>，否则返回<code>true</code>
	 */
	public boolean update(long delta) {
		if (mCurrentAction == null) {
			mCurrentAction = mActionQueue.poll();
			if (mCurrentAction == null) {
				return false;
			}
			mCurrentAction.preproccess();
			mIsNewAction = false;
		}
		
		if (mIsNewAction) { // 跳过死亡角色
			if (!fixAction()) {
				return false;
			}
			mCurrentAction.preproccess();
			mIsNewAction = false;
		}
		
		if (!mCurrentAction.update(delta)) { // 当前动作执行完毕
			mCurrentAction.postExecute();
			mCurrentAction = mActionQueue.poll(); // 取下一个动作
			if (mCurrentAction == null) { // 所有动作执行完毕
				return false;
			}
			mIsNewAction = true;
		}
		
		return true;
	}

	/**
	 * 执行完毕返回<code>false</code>
	 */
	private boolean fixAction() {
		// attacker dead, goto next action
		while (!mCurrentAction.isAttackerAlive()) {
			mCurrentAction = mActionQueue.poll();
			if (mCurrentAction == null) {
				return false;
			}
		}
		
		// target dead, get an alive target
		if (!mCurrentAction.isTargetAlive()) {
			if (mCurrentAction.isTargetsMoreThanOne()) { // 敌人都死了
				return false;
			} else { // try to find an alive target
				FightingCharacter newTarget = null;
				if (mCurrentAction.targetIsMonster()) {
					newTarget = mCombat.getFirstAliveMonster();
				} else {
					newTarget = mCombat.getRandomAlivePlayer();
				}
				
				if (newTarget == null) {
					return false;
				} else if (!(mCurrentAction instanceof ActionFlee)){
					((ActionSingleTarget)mCurrentAction).setTarget(newTarget);
				}
			}
		}
		
		return true;
	}
	
	public void draw(Canvas canvas) {
		if (mCurrentAction != null) {
			mCurrentAction.draw(canvas);
		}
	}
}
