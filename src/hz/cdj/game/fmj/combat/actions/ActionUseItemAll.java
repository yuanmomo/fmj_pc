package hz.cdj.game.fmj.combat.actions;

import hz.cdj.game.fmj.characters.FightingCharacter;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.combat.anim.RaiseAnimation;
import hz.cdj.game.fmj.goods.BaseGoods;
import hz.cdj.game.fmj.lib.ResSrs;
import hz.cdj.game.fmj.magic.BaseMagic;

import java.util.List;

import android.graphics.Canvas;

public class ActionUseItemAll extends ActionMultiTarget {

	private static final int STATE_PRE = 1; // 起手动画
	private static final int STATE_ANI = 2; // 魔法动画
	private static final int STATE_AFT = 3; // 伤害动画
	
	private int mState = 1;

	BaseGoods goods;
	
	ResSrs mAni;
	
	int ox, oy;

	public ActionUseItemAll(FightingCharacter attacker,
			List<? extends FightingCharacter> targets, BaseGoods goods) {
		super(attacker, targets);
		this.goods = goods;
	}

	@Override
	public void preproccess() {
		ox = mAttacker.getCombatX();
		oy = mAttacker.getCombatY();
//		mAni = magic.getMagicAni(); TODO null fix
		mAni.startAni();
		mAni.setIteratorNum(2);
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
			return updateRaiseAnimation(delta);
//			break;
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

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return super.getPriority();
	}

}
