package hz.cdj.game.fmj.gamemenu;

import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.graphics.TextRender;
import hz.cdj.game.fmj.graphics.Util;
import hz.cdj.game.fmj.views.BaseScreen;
import hz.cdj.game.fmj.views.ScreenSaveLoadGame;
import hz.cdj.game.fmj.views.ScreenSaveLoadGame.Operate;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class ScreenMenuSystem extends BaseScreen {
	
	private int first = 0;
	private int index = 0;
	private String[] str = {"读入进度", "存储进度", "游戏设置", "结束游戏"};
	private int strX = 42, strY = 32, selY = strY;
	
	private Bitmap bmpFrame = Util.getFrameBitmap(109 - 39 + 1, 91 - 29 + 1);
	private Bitmap bmpArrowUp = Bitmap.createBitmap(7, 4, Bitmap.Config.ARGB_8888);
	private Bitmap bmpArrowDown = Bitmap.createBitmap(7, 4, Bitmap.Config.ARGB_8888);
	private Bitmap[] bmpArr = new Bitmap[] {bmpArrowDown, bmpArrowUp};
	private int arrowX = 70, arrowY = 82, bmpi = 0;
	
	public ScreenMenuSystem() {
		Paint p = new Paint();
		p.setColor(Global.COLOR_BLACK);
		
		Canvas c = new Canvas(bmpArrowUp);
		c.drawColor(Global.COLOR_WHITE);
		c.drawLine(3, 0, 4, 0, p);
		c.drawLine(2, 1, 5, 1, p);
		c.drawLine(1, 2, 6, 2, p);
		c.drawLine(0, 3, 7, 3, p);
		
		c.setBitmap(bmpArrowDown);
		c.drawColor(Global.COLOR_WHITE);
		c.drawLine(0, 0, 7, 0, p);
		c.drawLine(1, 1, 6, 1, p);
		c.drawLine(2, 2, 5, 2, p);
		c.drawLine(3, 3, 4, 3, p);
	}

	@Override
	public void update(long delta) {
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bmpFrame, 39, 29, null);
		TextRender.drawText(canvas, str[first], strX, strY);
		TextRender.drawText(canvas, str[first + 1], strX, strY + 16);
		TextRender.drawText(canvas, str[first + 2], strX, strY + 32);
		TextRender.drawSelText(canvas, str[index], strX, selY);
		canvas.drawBitmap(bmpArr[bmpi], arrowX, arrowY, null);
	}

	@Override
	public void onKeyDown(int key) {
		if (key == Global.KEY_UP) {
			--index;
			selY -= 16;
		} else if (key == Global.KEY_DOWN) {
			++index;
			selY += 16;
		}
		
		if (index == 0 || index == 4) {
			index = 0;
			selY = 32;
			arrowY = 82;
			bmpi = 0;
			first = 0;
			strY = 32;
		} else if (index == 3 || index == -1) {
			index = 3;
			selY = 72;
			arrowY = 34;
			bmpi = 1;
			first = 1;
			strY = 40;
		}
	}

	@Override
	public void onKeyUp(int key) {
		if (key == Global.KEY_CANCEL) {
			GameView.getInstance().popScreen();
		} else if (key == Global.KEY_ENTER) {
			switch (index) {
			case 0:
				GameView.getInstance().pushScreen(new ScreenSaveLoadGame(Operate.LOAD));
				break;
				
			case 1:
				GameView.getInstance().pushScreen(new ScreenSaveLoadGame(Operate.SAVE));
				break;
				
			case 2:
				break;
				
			case 3:
				GameView.getInstance().changeScreen(Global.SCREEN_MENU);
				break;
			}
		}
	}

	@Override
	public boolean isPopup() {
		return true;
	}

}
