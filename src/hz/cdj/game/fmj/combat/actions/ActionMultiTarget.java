package hz.cdj.game.fmj.combat.actions;

import hz.cdj.game.fmj.characters.FightingCharacter;
import hz.cdj.game.fmj.characters.Monster;
import hz.cdj.game.fmj.combat.anim.RaiseAnimation;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;

public class ActionMultiTarget extends Action {

	protected List<FightingCharacter> mTargets;
	
	protected List<RaiseAnimation> mRaiseAnis;
	
	public ActionMultiTarget(FightingCharacter attacker,
			List<? extends FightingCharacter> targets) {
		mAttacker = attacker;
		mTargets = new LinkedList<FightingCharacter>();
		mTargets.addAll(targets);
		mRaiseAnis = new LinkedList<RaiseAnimation>();
	}

	public void postExecute() {
		if (mTargets != null) {
			for (FightingCharacter fc : mTargets) {
				fc.setVisiable(fc.isAlive());
			}
		}
	}

	@Override
	protected boolean updateRaiseAnimation(long delta) {
		if (mRaiseAnis != null) { // 全体
			if (mRaiseAnis.size() == 0) {
				return false;
			} else {
				for (int i = 0; i < mRaiseAnis.size(); i++) {
					if (!mRaiseAnis.get(i).update(delta)) {
						mRaiseAnis.remove(i);
						if (mRaiseAnis.isEmpty()) return false;
					}
				}
				return true;
			}
		}

		return false;
	}

	@Override
	protected void drawRaiseAnimation(Canvas canvas) {
		if (mRaiseAnis != null) {
			for (RaiseAnimation ani : mRaiseAnis) {
				ani.draw(canvas);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isTargetAlive() {
		for (FightingCharacter fc : mTargets) {
			if (fc.isAlive()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isTargetsMoreThanOne() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean targetIsMonster() {
		return mTargets.get(0) instanceof Monster;
	}

}
