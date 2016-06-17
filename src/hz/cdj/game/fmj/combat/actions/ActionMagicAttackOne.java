package hz.cdj.game.fmj.combat.actions;

import hz.cdj.game.fmj.characters.FightingCharacter;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.combat.anim.RaiseAnimation;
import hz.cdj.game.fmj.lib.ResSrs;
import hz.cdj.game.fmj.magic.BaseMagic;
import hz.cdj.game.fmj.magic.MagicAttack;
import android.graphics.Canvas;

public class ActionMagicAttackOne extends ActionSingleTarget {
	
	private static final int STATE_PRE = 1; // 起手动画
	private static final int STATE_ANI = 2; // 魔法动画
	private static final int STATE_AFT = 3; // 伤害动画
	
	private int mState = 1;
	
	private BaseMagic magic; // MagicAttack MagicSpecial
	
	private ResSrs mAni;
	
	private int mAniX, mAniY;
	
	private int ox, oy;

	public ActionMagicAttackOne(FightingCharacter attacker, FightingCharacter target, BaseMagic magic) {
		super(attacker, target);
		this.magic = magic;
	}

	@Override
	public void preproccess() {
		// TODO 记下伤害值、异常状态
		ox = mAttacker.getCombatX();
		oy = mAttacker.getCombatY();
		mAni = magic.getMagicAni();
		mAni.startAni();
		mAni.setIteratorNum(2);
		int ohp = mTarget.getHP();
		if (magic instanceof MagicAttack) {
			((MagicAttack)magic).use(mAttacker, mTarget);
		}
		mAniX = mTarget.getCombatX();
		mAniY = mTarget.getCombatY() - mTarget.getFightingSprite().getHeight() / 2;
		mRaiseAni = new RaiseAnimation(mTarget.getCombatX(), mTarget.getCombatY(), mTarget.getHP() - ohp, 0/*FightingCharacter.BUFF_MASK_DU*/);
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
				if (mTarget instanceof Player) {
					mTarget.getFightingSprite().setCurrentFrame(10);
				} else {
					mTarget.getFightingSprite().move(2, 2);
				}
			}
			break;

		case STATE_AFT:
			if (!mRaiseAni.update(delta)) {
				if (mTarget instanceof Player) {
					((Player)mTarget).setFrameByState();
				} else {
					mTarget.getFightingSprite().move(-2, -2);
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
			mAni.drawAbsolutely(canvas, mAniX, mAniY);
		} else if (mState == STATE_AFT) {
			mRaiseAni.draw(canvas);
		}
	}

}
