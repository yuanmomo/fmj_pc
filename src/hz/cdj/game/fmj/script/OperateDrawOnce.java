package hz.cdj.game.fmj.script;

import android.graphics.Canvas;

public abstract class OperateDrawOnce extends Operate {
	int drawCnt = 0;

	public abstract void drawOnce(Canvas canvas);

	@Override
	public boolean update(long delta) {
		if (drawCnt >= 3) {
			drawCnt = 0;
			return false;
		}
		return true;
	}

	@Override
	public void draw(Canvas canvas) {
		drawOnce(canvas);
		++drawCnt;
	}

	@Override
	public void onKeyDown(int key) {
	}

	@Override
	public void onKeyUp(int key) {
	}

}
