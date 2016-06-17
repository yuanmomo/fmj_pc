package hz.cdj.game.fmj.combat.actions;

import hz.cdj.game.fmj.characters.Player;
import android.graphics.Canvas;

public class ActionFlee extends Action {
	
	private int FRAME_CNT = 5;

	private Player player;
	
	private boolean fleeSucceed;
	
	private Runnable runAfterFlee;
	
	private int ox, oy, dy;

	/**
	 * 
	 * @param p 逃跑者
	 * @param fleeSuc 是否逃跑成功
	 * @param runAft 逃跑动作完毕后，执行之
	 */
	public ActionFlee(Player p, boolean fleeSuc, Runnable runAft) {
		player = p;
		fleeSucceed = fleeSuc;
		runAfterFlee = runAft;
	}

	@Override
	public void preproccess() {
		// TODO calc the pos
		ox = player.getCombatX();
		oy = player.getCombatY();
		dy = (96 - oy) / FRAME_CNT;
		player.getFightingSprite().setCurrentFrame(1);
	}

	@Override
	public boolean update(long delta) {
		super.update(delta);
		if (mCurrentFrame < FRAME_CNT) {
			player.setCombatPos(ox, oy + dy * mCurrentFrame);
			return true;
		} else if (!fleeSucceed && mCurrentFrame < FRAME_CNT + 2) {
			player.setCombatPos(ox, oy);
			player.getFightingSprite().setCurrentFrame(11);
		} else if (!fleeSucceed) {
			player.setFrameByState();
		}
		return false;
	}

	@Override
	public void postExecute() {
		// TODO Auto-generated method stub
		if (fleeSucceed && runAfterFlee != null) {
			runAfterFlee.run();
		} else {
			player.setCombatPos(ox, oy);
		}
	}

	@Override
	protected boolean updateRaiseAnimation(long delta) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void drawRaiseAnimation(Canvas canvas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPriority() {
		return player.getSpeed() * 100;
	}

	@Override
	public boolean isAttackerAlive() {
		return true;
	}

	@Override
	public boolean isTargetAlive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTargetsMoreThanOne() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean targetIsMonster() {
		// TODO Auto-generated method stub
		return false;
	}

}
