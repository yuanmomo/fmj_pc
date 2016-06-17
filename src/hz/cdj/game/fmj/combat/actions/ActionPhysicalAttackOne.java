package hz.cdj.game.fmj.combat.actions;

import hz.cdj.game.fmj.characters.FightingCharacter;
import hz.cdj.game.fmj.characters.FightingSprite;
import hz.cdj.game.fmj.characters.Monster;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.combat.anim.RaiseAnimation;
import android.graphics.Canvas;

public class ActionPhysicalAttackOne extends ActionSingleTarget {
	
	private int TOTAL_FRAME = 5;
	private float dx, dy;
	private int ox, oy;
	
	private int buffRound;
	
	public ActionPhysicalAttackOne(FightingCharacter attacker,
			FightingCharacter target) {
		super(attacker, target);
	}

	@Override
	public void preproccess() {
		// TODO 记下伤害值、异常状态
		int damage;
		ox = mAttacker.getCombatX();
		oy = mAttacker.getCombatY();
		dx = (float)(mTarget.getCombatX() - mAttacker.getCombatX()) / TOTAL_FRAME;
		dy = (float)(mTarget.getCombatY() - mAttacker.getCombatY()) / TOTAL_FRAME;
		damage = mAttacker.getAttack() - mTarget.getDefend();
		if (damage <= 0) {
			damage = 1;
		}
		if (mAttacker instanceof Player) {
			damage *= 10;
		}
		damage += (int)(Math.random() * 10);
		mTarget.setHP(mTarget.getHP() - damage);
		mRaiseAni = new RaiseAnimation(mTarget.getCombatLeft(), mTarget.getCombatTop(), -damage, 0);
	}

	private boolean mTotalMark = true;
	
	@Override
	public boolean update(long delta) {
		super.update(delta);
		if (mCurrentFrame < TOTAL_FRAME) { // 发起动作
			mAttacker.setCombatPos((int)(ox + dx * mCurrentFrame), (int)(oy + dy * mCurrentFrame));
			if (mAttacker instanceof Monster) {
				FightingSprite fs = mAttacker.getFightingSprite();
				fs.setCurrentFrame(fs.getFrameCnt() * mCurrentFrame / TOTAL_FRAME + 1);
			} else if (mAttacker instanceof Player) {
				FightingSprite fs = mAttacker.getFightingSprite();
				fs.setCurrentFrame(5 * mCurrentFrame / TOTAL_FRAME + 1);
			}
		} else if (mCurrentFrame > TOTAL_FRAME) { // 扣血、异常状态的动画
			if (!updateRaiseAnimation(delta)) {
				if (mTarget instanceof Player) {
					((Player)mTarget).setFrameByState();
				} else {
					mTarget.getFightingSprite().move(-2, -2);
				}
				return false;
			}
		} else if (mTotalMark){
			mTotalMark = false;
			mAttacker.setCombatPos(ox, oy);
			if (mAttacker instanceof Monster) {
				FightingSprite fs = mAttacker.getFightingSprite();
				fs.setCurrentFrame(1);
			} else if (mAttacker instanceof Player) {
				((Player)mAttacker).setFrameByState();
			}
			if (mTarget instanceof Player) {
				mTarget.getFightingSprite().setCurrentFrame(10);
			} else {
				mTarget.getFightingSprite().move(2, 2);
			}
		}
		return true;
	}

	@Override
	public void draw(Canvas canvas) {
		if (mCurrentFrame >= TOTAL_FRAME) {
			drawRaiseAnimation(canvas);
		}
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return super.getPriority();
	}

}
