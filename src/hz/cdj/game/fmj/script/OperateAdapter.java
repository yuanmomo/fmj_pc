package hz.cdj.game.fmj.script;

import android.graphics.Canvas;

public abstract class OperateAdapter extends Operate{

	@Override
	public boolean update(long delta) {
		return false;
	}

	@Override
	public void draw(Canvas canvas) {
	}

	@Override
	public void onKeyDown(int key) {
	}

	@Override
	public void onKeyUp(int key) {
	}

}
