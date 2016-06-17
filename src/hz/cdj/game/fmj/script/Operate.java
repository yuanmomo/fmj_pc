package hz.cdj.game.fmj.script;

import android.graphics.Canvas;

public abstract class Operate {

	/**
	 * 处理一条指令
	 * 
	 * @return <code>true</code>继续执行 {@link #update(long)} {@link #draw(Canvas)}
	 *         <p>
	 *         <code>false</code>指令执行完毕
	 */
	public abstract boolean process();

	/**
	 * 
	 * @param delta
	 * @return <code>false</code>退出当前操作
	 */
	public abstract boolean update(long delta);

	public abstract void draw(Canvas canvas);

	public abstract void onKeyDown(int key);

	public abstract void onKeyUp(int key);

	/**
	 * 
	 * @return 是否全屏
	 */
	public boolean isPopup() {
		return false;
	}
}
