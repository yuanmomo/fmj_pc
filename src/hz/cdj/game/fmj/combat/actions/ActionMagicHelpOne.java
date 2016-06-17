package hz.cdj.game.fmj.combat.actions;

import hz.cdj.game.fmj.characters.FightingCharacter;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.combat.anim.RaiseAnimation;
import hz.cdj.game.fmj.graphics.TextRender;
import hz.cdj.game.fmj.lib.ResSrs;
import hz.cdj.game.fmj.magic.BaseMagic;
import android.graphics.Canvas;

public class ActionMagicHelpOne extends ActionSingleTarget {

	private static final int STATE_PRE = 1; // 起手动画
	private static final int STATE_ANI = 2; // 魔法动画
	private static final int STATE_AFT = 3; // 伤害动画
	
	private int mState = 1;
	
	BaseMagic magic;
	
	ResSrs mAni;
	
	int mAnix, mAniy;
	
	int ox, oy;

	public ActionMagicHelpOne(FightingCharacter attacker,
			FightingCharacter target, BaseMagic magic) {
		super(attacker, target);
		this.magic = magic;
	}

	@Override
	public void preproccess() {
		// TODO 记下伤害值、异常状态
		mAni = magic.getMagicAni();
		mAni.startAni();
		mAni.setIteratorNum(2);
		mAnix = mTarget.getCombatX();
		mAniy = mTarget.getCombatY();
		mRaiseAni = new RaiseAnimation(mTarget.getCombatX(), mTarget.getCombatTop(), 10, 0);
	}

	@Override
	public boolean update(long delta) {
		// TODO Auto-generated method stub
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
			if (!mAni.update(delta)) { // 魔法动画完成
				mState = STATE_AFT;
				if (mAttacker instanceof Player) {
					((Player)mAttacker).setFrameByState();
				} else {
					mAttacker.getFightingSprite().move(-2, -2);
				}
			}
			break;
			
		case STATE_AFT:
			return mRaiseAni.update(delta);
//			break;
		}
		return true;
	}

	@Override
	public void draw(Canvas canvas) {
		if (mState == STATE_ANI) {
			mAni.drawAbsolutely(canvas, mAnix, mAniy);
		} else if (mState == STATE_AFT) {
			mRaiseAni.draw(canvas);
		}
	}

}
