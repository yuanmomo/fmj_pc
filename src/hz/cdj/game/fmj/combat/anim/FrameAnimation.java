package hz.cdj.game.fmj.combat.anim;

import hz.cdj.game.fmj.lib.ResImage;
import android.graphics.Canvas;

/** 帧动画*/
public class FrameAnimation {
	
	private int DELTA = 1000 / 5;
	
	private ResImage mImage;
	
	private int mStartFrame;
	
	private int mEndFrame;
	
	private int mCurFrame;
	
	private long mTimeCnt = 0;
	
	public FrameAnimation(ResImage img) {
		this(img, 1, img.getNumber());
	}
	
	public FrameAnimation(ResImage img, int startFrame, int endFrame) {
		mImage = img;
		mStartFrame = startFrame;
		mEndFrame = endFrame;
		mCurFrame = startFrame;
	}
	
	public void setFPS(int fps) {
		DELTA = 1000 / fps;
	}

	public void update(long delta) {
		mTimeCnt += delta;
		if (mTimeCnt >= DELTA) {
			mTimeCnt = 0;
			
			if (++mCurFrame > mEndFrame) {
				mCurFrame = mStartFrame;
			}
		}
	}
	
	public void draw(Canvas canvas, int x, int y) {
		mImage.draw(canvas, mCurFrame, x, y);
	}
}
