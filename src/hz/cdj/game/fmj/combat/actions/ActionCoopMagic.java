package hz.cdj.game.fmj.combat.actions;

import hz.cdj.game.fmj.characters.FightingCharacter;
import hz.cdj.game.fmj.characters.Monster;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.combat.anim.RaiseAnimation;
import hz.cdj.game.fmj.goods.GoodsDecorations;
import hz.cdj.game.fmj.graphics.TextRender;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResSrs;
import hz.cdj.game.fmj.magic.MagicAttack;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;

public class ActionCoopMagic extends Action {

	private static final int STATE_MOV = 0; // 移位动画
	private static final int STATE_PRE = 1; // 起手动画
	private static final int STATE_ANI = 2; // 魔法动画
	private static final int STATE_AFT = 3; // 伤害动画
	
	private static final int MOV_FRAME = 5; // 移位帧数
	
	private int mState = STATE_MOV;
	
	List<Player> mActors;
	
	List<FightingCharacter> mMonsters;
	
	FightingCharacter mMonster;
	
	boolean onlyOneMonster;
	
	MagicAttack magic;
	
	ResSrs mAni;

	RaiseAnimation mRaiseAni;
	
	List<RaiseAnimation> mRaiseAnis;

	private float dxy[][];
	private int oxy[][];
	
	private int mAniX, mAniY;
	
	public ActionCoopMagic(List<Player> actors, FightingCharacter monster) {
		mActors = actors;
		mMonster = monster;
		onlyOneMonster = true;
		
		magic = getCoopMagic();
	}
	
	public ActionCoopMagic(List<Player> actors, List<FightingCharacter> monsters) {
		mActors = actors;
		mMonsters = new LinkedList<FightingCharacter>();
		mMonsters.addAll(monsters);
		onlyOneMonster = false;
		
		magic = getCoopMagic();
	}
	
	private MagicAttack getCoopMagic() {
		Player firstPlayer = (Player)mActors.get(0);
		GoodsDecorations dc = (GoodsDecorations)firstPlayer.getEquipmentsArray()[0];
		return dc == null ? null : dc.getCoopMagic();
	}

	@Override
	public void preproccess() {
		// TODO 记下伤害值、异常状态
		int midpos[][] = new int[][]{{92, 52}, {109, 63}, {126, 74}};
		dxy = new float[mActors.size()][2];
		oxy = new int[mActors.size()][2];
		for (int i = 0; i < mActors.size(); i++) {
			oxy[i][0] = mActors.get(i).getCombatX();
			oxy[i][1] = mActors.get(i).getCombatY();
		}
		for (int i = 0; i < dxy.length; i++) {
			dxy[i][0] = midpos[i][0] - oxy[i][0]; dxy[i][0] /= MOV_FRAME;
			dxy[i][1] = midpos[i][1] - oxy[i][1]; dxy[i][1] /= MOV_FRAME;
		}
		
		if (onlyOneMonster) {
			mAniX = mMonster.getCombatX();
			mAniY = mMonster.getCombatY() - mMonster.getFightingSprite().getHeight() / 2;
		} else {
			mAniX = mAniY = 0;
		}
		
		if (magic == null) {
			mAni = (ResSrs)DatLib.GetRes(DatLib.RES_SRS, 2, 240);
		} else {
			mAni = magic.getMagicAni();
		}
		mAni.startAni();
	}

	@Override
	public boolean update(long delta) {
		super.update(delta);
		// TODO Auto-generated method stub
		switch (mState) {
		case STATE_MOV:
			if (mCurrentFrame < MOV_FRAME) {
				for (int i = 0; i < mActors.size(); i++) {
					mActors.get(i).setCombatPos((int)(oxy[i][0] + dxy[i][0] * mCurrentFrame),
							(int)(oxy[i][1] + dxy[i][1] * mCurrentFrame));
				}
			} else {
				mState = STATE_PRE;
			}
			break;

		case STATE_PRE:
			if (mCurrentFrame < 10 + MOV_FRAME) {
				for (int i = 0; i < mActors.size(); i++) {
					mActors.get(i).getFightingSprite().setCurrentFrame(
							(mCurrentFrame - MOV_FRAME) * 3 / 10 + 6);
				}
			} else {
				mState = STATE_ANI;
			}
			break;
			
		case STATE_ANI:
			if (!mAni.update(delta)) {
				mState = STATE_AFT;
				for (int i = 0; i < mActors.size(); i++) {
					mActors.get(i).setFrameByState();
					mActors.get(i).setCombatPos(oxy[i][0], oxy[i][1]);
				}
			}
			break;
			
		case STATE_AFT:
			if (onlyOneMonster) {
//				return m
			}
			if (true) return false;
			break;
		}
		return true;
	}

	@Override
	public void draw(Canvas canvas) {
		if (mState == STATE_ANI) {
			mAni.drawAbsolutely(canvas, mAniX, mAniY);
		}
	}

	@Override
	public int getPriority() {
		return mActors.get(0).getSpeed();
	}

	@Override
	public boolean isAttackerAlive() {
		return mActors.get(0).isAlive();
	}

	@Override
	public void postExecute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean updateRaiseAnimation(long delta) {
		if (onlyOneMonster) {
			return mRaiseAni != null && mRaiseAni.update(delta);
		}

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
		if (onlyOneMonster) {
			if (mRaiseAni != null) {
				mRaiseAni.draw(canvas);
			}
		} else {
			if (mRaiseAnis != null) {
				for (RaiseAnimation ani : mRaiseAnis) {
					ani.draw(canvas);
				}
			}
		}
	}

	@Override
	public boolean isTargetAlive() {
		if (onlyOneMonster) {
			return mMonster.isAlive();
		} else {
			for (FightingCharacter m : mMonsters) {
				if (m.isAlive()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isTargetsMoreThanOne() {
		return onlyOneMonster;
	}

	@Override
	public boolean targetIsMonster() {
		return true;
	}


}
