package hz.cdj.game.fmj.views;

import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResImage;
import hz.cdj.game.fmj.lib.ResSrs;
import hz.cdj.game.fmj.scene.SaveLoadGame;
import android.graphics.Canvas;

public class ScreenMenu extends BaseScreen {
	private ResImage mImgMenu;
	private int mLeft, mTop;
	private ResSrs[] mSrsSelector = new ResSrs[2];
	private int mCurSelect = 0;
	
	public ScreenMenu() {
		mImgMenu = (ResImage)DatLib.getInstance().getRes(DatLib.RES_PIC, 2, 14);
		mSrsSelector[0] = (ResSrs)DatLib.getInstance().getRes(DatLib.RES_SRS, 1, 250);
		mSrsSelector[1] = (ResSrs)DatLib.getInstance().getRes(DatLib.RES_SRS, 1, 251);
		mSrsSelector[0].startAni();
		mSrsSelector[1].startAni();
		mLeft = (160 - mImgMenu.getWidth()) / 2;
		mTop = (96 - mImgMenu.getHeight()) / 2;
	}

	@Override
	public void update(long delta) {
		if (!mSrsSelector[mCurSelect].update(delta)) {
			mSrsSelector[mCurSelect].startAni();
		}
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawColor(Global.COLOR_WHITE);
		mImgMenu.draw(canvas, 1, mLeft, mTop);
		mSrsSelector[mCurSelect].draw(canvas, 0, 0);
	}

	@Override
	public void onKeyDown(int key) {
		switch (key) {
		case Global.KEY_UP:
		case Global.KEY_DOWN:
			mCurSelect = 1 - mCurSelect;
			break;
		case Global.KEY_CANCEL:
			isCancelKeyDown = true;
			break;
		}
	}
	
	private boolean isCancelKeyDown = false;

	@Override
	public void onKeyUp(int key) {
		if (key == Global.KEY_ENTER) {
			if (mCurSelect == 0) { // 新游戏
				SaveLoadGame.startNewGame = true;
				GameView.getInstance().changeScreen(Global.SCREEN_MAIN_GAME);
			} else if (mCurSelect == 1) { // 读取进度
				GameView.getInstance().pushScreen(
						new ScreenSaveLoadGame(ScreenSaveLoadGame.Operate.LOAD));
			}
		} else if (key == Global.KEY_CANCEL && isCancelKeyDown) {
			System.out.println("退出游戏");//GameActivity.showExitDialog();
			System.exit(0);
		}
	}

}
