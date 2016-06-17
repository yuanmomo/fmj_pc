package hz.cdj.game.fmj.combat.actions;

import hz.cdj.game.fmj.characters.FightingCharacter;
import hz.cdj.game.fmj.characters.Monster;
import hz.cdj.game.fmj.combat.anim.RaiseAnimation;
import android.graphics.Canvas;

public abstract class ActionSingleTarget extends Action {
	
	protected FightingCharacter mTarget;
	
	protected RaiseAnimation mRaiseAni;

	public ActionSingleTarget(FightingCharacter attacker,
			FightingCharacter target) {
		mAttacker = attacker;
		mTarget = target;
	}

	@Override
	public void postExecute() {
		mTarget.setVisiable(mTarget.isAlive());
	}

	@Override
	protected boolean updateRaiseAnimation(long delta) {
		return mRaiseAni != null && mRaiseAni.update(delta);
	}

	@Override
	protected void drawRaiseAnimation(Canvas canvas) {
		if (mRaiseAni != null) {
			mRaiseAni.draw(canvas);
		}
	}

	@Override
	public boolean isTargetAlive() {
		return mTarget.isAlive();
	}
	
	@Override
	public boolean isTargetsMoreThanOne() {
		return false;
	}
	
	@Override
	public boolean targetIsMonster() {
		return mTarget instanceof Monster;
	}
	
	public void setTarget(FightingCharacter fc) {
		mTarget = fc;
	}
}
