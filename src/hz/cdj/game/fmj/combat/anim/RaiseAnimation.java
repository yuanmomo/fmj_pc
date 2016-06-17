package hz.cdj.game.fmj.combat.anim;

import hz.cdj.game.fmj.characters.FightingCharacter;
import hz.cdj.game.fmj.graphics.Util;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResSrs;

import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class RaiseAnimation {
	
	private int x, y;
	
	private int dy = 0, dt = 0;
	
	private Bitmap raiseNum;
	
	private LinkedList<ResSrs> srsList;
	
	private boolean bShowNum;

	public RaiseAnimation(int x, int y, int hitpoint, int buff) {
		this.x = x;
		this.y = y;
		raiseNum = Util.getSmallSignedNumBitmap(hitpoint);
		this.bShowNum = hitpoint != 0;
		srsList = new LinkedList<ResSrs>();
		if ((buff & FightingCharacter.BUFF_MASK_DU) == FightingCharacter.BUFF_MASK_DU) {
			srsList.add((ResSrs)DatLib.GetRes(DatLib.RES_SRS, 1, 243));
			srsList.getLast().startAni();
		}
		if ((buff & FightingCharacter.BUFF_MASK_LUAN) == FightingCharacter.BUFF_MASK_LUAN) {
			srsList.add((ResSrs)DatLib.GetRes(DatLib.RES_SRS, 1, 244));
			srsList.getLast().startAni();
		}
		if ((buff & FightingCharacter.BUFF_MASK_FENG) == FightingCharacter.BUFF_MASK_FENG) {
			srsList.add((ResSrs)DatLib.GetRes(DatLib.RES_SRS, 1, 245));
			srsList.getLast().startAni();
		}
		if ((buff & FightingCharacter.BUFF_MASK_MIAN) == FightingCharacter.BUFF_MASK_MIAN) {
			srsList.add((ResSrs)DatLib.GetRes(DatLib.RES_SRS, 1, 246));
			srsList.getLast().startAni();
		}
		if ((buff & FightingCharacter.BUFF_MASK_GONG) == FightingCharacter.BUFF_MASK_GONG) {
			srsList.add((ResSrs)DatLib.GetRes(DatLib.RES_SRS, 1, 240));
			srsList.getLast().startAni();
		}
		if ((buff & FightingCharacter.BUFF_MASK_FANG) == FightingCharacter.BUFF_MASK_FANG) {
			srsList.add((ResSrs)DatLib.GetRes(DatLib.RES_SRS, 1, 241));
			srsList.getLast().startAni();
		}
		if ((buff & FightingCharacter.BUFF_MASK_SU) == FightingCharacter.BUFF_MASK_SU) {
			srsList.add((ResSrs)DatLib.GetRes(DatLib.RES_SRS, 1, 242));
			srsList.getLast().startAni();
		}
	}
	
	private long cnt = 0;
	
	public boolean update(long delta) {
		if (bShowNum) {
			cnt += delta;
			if (cnt > 50) {
				cnt = 0;
				++dt;
				dy -= dt;
				if (dt > 4) {
					bShowNum = false;
				}
			}
		} else {
			if (srsList.isEmpty()) {
				return false;
			} else {
				if (!srsList.getFirst().update(delta)) {
					srsList.removeFirst();
					return !srsList.isEmpty();
				}
			}
		}
		return true;
	}
	
	public void draw(Canvas canvas) {
		if (bShowNum) {
			canvas.drawBitmap(raiseNum, x, y + dy, null);
		} else {
			if (srsList.size() > 0) {
				srsList.getFirst().drawAbsolutely(canvas, x, y);
			}
		}
	}

}
