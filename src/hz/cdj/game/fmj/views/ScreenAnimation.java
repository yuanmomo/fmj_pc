package hz.cdj.game.fmj.views;

import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResSrs;
import android.graphics.Canvas;

public class ScreenAnimation extends BaseScreen {
	private ResSrs mResSrs;
	private int mIndex;

	/**
	 * 
	 * @param index
	 *            247、248、249分别代表游戏开发组的商标、游戏的名称 以及游戏战斗失败后的过场动画
	 */
	public ScreenAnimation(int index) {
		if (index != 247 && index != 248 && index != 249) {
			throw new IllegalArgumentException("只能是247,248,249");
		}
		mIndex = index;
		mResSrs = (ResSrs) DatLib.getInstance().getRes(DatLib.RES_SRS, 1, index);
		mResSrs.setIteratorNum(4);
		mResSrs.startAni();
	}

	@Override
	public void update(long delta) {
		if (!mResSrs.update(delta)) {
			if (mIndex == 247) { // 转到游戏动画
				GameView.getInstance().changeScreen(Global.SCREEN_GAME_LOGO);
			} else if (mIndex == 248) { // 转到游戏菜单
				GameView.getInstance().changeScreen(Global.SCREEN_MENU);
			} else if (mIndex == 249) { // 
				GameView.getInstance().changeScreen(Global.SCREEN_MENU);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawColor(Global.COLOR_WHITE);
		mResSrs.draw(canvas, 0, 0);
	}

	@Override
	public void onKeyDown(int key) {
		if (key == Global.KEY_CANCEL && (mIndex == 247 || mIndex == 248)) {
			GameView.getInstance().changeScreen(Global.SCREEN_MENU);
		}
	}

	@Override
	public void onKeyUp(int key) {
	}

}
