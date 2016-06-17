package hz.cdj.game.fmj.combat.actions;

import hz.cdj.game.fmj.characters.FightingCharacter;
import hz.cdj.game.fmj.characters.FightingSprite;
import hz.cdj.game.fmj.characters.Monster;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.combat.anim.RaiseAnimation;

import java.util.List;

import android.graphics.Canvas;

public class ActionPhysicalAttackAll extends ActionMultiTarget {

	private int TOTAL_FRAME = 5;
	private float dx, dy;
	private int ox, oy;
	
	private int buffRound;
	
	public ActionPhysicalAttackAll(FightingCharacter attacker,
			List<? extends FightingCharacter> targets) {
		super(attacker, targets);
	}

	@Override
	public void preproccess() {
		// TODO 记下伤害值、异常状态
		int damage;
		ox = mAttacker.getCombatX();
		oy = mAttacker.getCombatY();
		dx = (44.0f - mAttacker.getCombatX()) / TOTAL_FRAME;
		dy = (14.0f - mAttacker.getCombatY()) / TOTAL_FRAME;
		for (int i = 0; i < mTargets.size(); i++) {
			FightingCharacter fc =  mTargets.get(i);
			damage = mAttacker.getAttack() - fc.getDefend();
			if (damage <= 0) {
				damage = 1;
			}
			damage += (int)(Math.random() * 3);
			fc.setHP(fc.getHP() - damage);
			mRaiseAnis.add(new RaiseAnimation(mTargets.get(i).getCombatX(), mTargets.get(i).getCombatY(), -damage, 0));
		}
	}

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
			return updateRaiseAnimation(delta);
		} else {
			mAttacker.setCombatPos(ox, oy);
			if (mAttacker instanceof Monster) {
				FightingSprite fs = mAttacker.getFightingSprite();
				fs.setCurrentFrame(1);
			} else if (mAttacker instanceof Player) {
				FightingSprite fs = mAttacker.getFightingSprite();
				fs.setCurrentFrame(1); // TODO the old state 眠
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
