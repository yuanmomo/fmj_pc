package hz.cdj.game.fmj.combat.actions;

import hz.cdj.game.fmj.characters.FightingCharacter;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.combat.anim.RaiseAnimation;
import hz.cdj.game.fmj.goods.GoodsHiddenWeapon;
import hz.cdj.game.fmj.lib.ResSrs;

import java.util.List;

import android.graphics.Canvas;

public class ActionThrowItemAll extends ActionMultiTarget {

	private static final int STATE_PRE = 1; // 起手动画
	private static final int STATE_ANI = 2; // 魔法动画
	private static final int STATE_AFT = 3; // 伤害动画
	
	private int mState = 1;
	
	private ResSrs mAni;
	
	private int ox, oy;

	GoodsHiddenWeapon hiddenWeapon;

	public ActionThrowItemAll(FightingCharacter attacker,
			List<? extends FightingCharacter> targets, GoodsHiddenWeapon goods) {
		super(attacker, targets);
		hiddenWeapon = goods;
	}

	@Override
	public void preproccess() {
		// TODO 记下伤害值、异常状态
		ox = mAttacker.getCombatX();
		oy = mAttacker.getCombatY();
		mAni = hiddenWeapon.getAni();
		mAni.startAni();
		mAni.setIteratorNum(2);
		// TODO effect it
		mRaiseAnis.add(new RaiseAnimation(10, 20, 10, 0));
		mRaiseAnis.add(new RaiseAnimation(30, 10, 10, 0));
	}

	@Override
	public boolean update(long delta) {
		super.update(delta);
		switch (mState) {
		case STATE_PRE:
			if (mCurrentFrame < 10) {
				if (mAttacker instanceof Player) {
					mAttacker.getFightingSprite().setCurrentFrame(
						mCurrentFrame * 3 / 10 + 6);
				} else {
					mAttacker.setCombatPos(ox + 2, oy + 2);
				}
			} else {
				mState = STATE_ANI;
			}
			break;

		case STATE_ANI:
			if (!mAni.update(delta)) {
				mState = STATE_AFT;
				if (mAttacker instanceof Player) {
					((Player)mAttacker).setFrameByState();
				} else {
					mAttacker.getFightingSprite().move(-2, -2);
				}
				if (!targetIsMonster()) {
					for (FightingCharacter fc : mTargets) {
						fc.getFightingSprite().setCurrentFrame(10);
					}
				} else {
					for (FightingCharacter fc : mTargets) {
						fc.getFightingSprite().move(2, 2);
					}
				}
			}
			break;

		case STATE_AFT:
			if (!updateRaiseAnimation(delta)) {
				if (targetIsMonster()) {
					for (FightingCharacter fc : mTargets) {
						fc.getFightingSprite().move(-2, -2);
					}
				} else {
					for (FightingCharacter fc : mTargets) {
						((Player)fc).setFrameByState();
					}
				}
				return false;
			}
			break;
		}
		return true;
	}

	@Override
	public void draw(Canvas canvas) {
		if (mState == STATE_ANI) {
			mAni.draw(canvas, 0, 0);
		} else if (mState == STATE_AFT) {
			drawRaiseAnimation(canvas);
		}
	}

}
