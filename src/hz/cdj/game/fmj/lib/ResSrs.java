package hz.cdj.game.fmj.lib;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import android.graphics.Canvas;

public class ResSrs extends ResBase {
	/**
	 * 帧数
	 */
	private int mFrameNum;
	
	/**
	 *  图片数
	 */
	private int mImageNum;
	
	private int mStartFrame;
	private int mEndFrame;
	
	/**
	 * <code>mFrameHeader = new int[mFrameNum][5];</code><p>
	 * x,y,Show,nShow,imgIndex
	 */
	private int[][] mFrameHeader;
	
	private ResImage[] mImage;

	@Override
	public void setData(byte[] buf, int offset) {
		mType = buf[offset];
		mIndex = (int)buf[offset + 1] & 0xFF;
		mFrameNum = (int)buf[offset + 2] & 0xFF;
		mImageNum = (int)buf[offset + 3] & 0xFF;
		mStartFrame = (int)buf[offset + 4] & 0xFF;
		mEndFrame = (int)buf[offset + 5] & 0xFF;
		
		int ptr = offset + 6;
		mFrameHeader = new int[mFrameNum][5];
		for (int i = 0; i < mFrameNum; i++) {
			mFrameHeader[i][0] = (int)buf[ptr++] & 0xFF; // x
			mFrameHeader[i][1] = (int)buf[ptr++] & 0xFF; // y
			mFrameHeader[i][2] = (int)buf[ptr++] & 0xFF; // Show
			mFrameHeader[i][3] = (int)buf[ptr++] & 0xFF; // nShow
			mFrameHeader[i][4] = (int)buf[ptr++] & 0xFF; // 图号
		}
		
		// 读入mImageNum个ResImage
		mImage = new ResImage[mImageNum];
		for (int i = 0; i < mImageNum; i++) {
			mImage[i] = new ResImage();
			mImage[i].setData(buf, ptr);
			ptr += mImage[i].getBytesCount();
		}
	}
	
	private int ITERATOR = 1; // update 迭代次数
	private List<Key> mShowList = new LinkedList<Key>();
	
	private class Key {
		int index;
		int show;
		int nshow;
		
		public Key(int index) {
			this.index = index;
			this.show = mFrameHeader[index][2];
			this.nshow = mFrameHeader[index][3];
		}
	}
	
	/**
	 * 开始特效动画
	 */
	public void startAni() {
		mShowList.clear();
		mShowList.add(new Key(0));
	}
	
	/**
	 * 
	 * @return 返回false动画播放完毕
	 */
	public boolean update(long delta) {
		for (int j = 0; j < ITERATOR; j++) {
			ListIterator<Key> iter = mShowList.listIterator();
			while (iter.hasNext()) {
				Key i = iter.next();
				--i.show;
				--i.nshow;
				if (i.nshow == 0 && i.index + 1 < mFrameNum) {
					iter.add(new Key(i.index + 1)); // 下一帧开始显示
				}
			}
			iter = mShowList.listIterator();
			while (iter.hasNext()) {
				Key i = iter.next();
				if (i.show <= 0) { // 该帧的图片显示完成
					iter.remove();
				}
			}
			if (mShowList.isEmpty()) return false;
		}
		return true;
	}
	
	public void draw(Canvas canvas, int dx, int dy) {
		for (Key i : mShowList) {
			mImage[mFrameHeader[i.index][4]].draw(canvas, 1,mFrameHeader[i.index][0] + dx, mFrameHeader[i.index][1] + dy);
		}
	}
	
	public void drawAbsolutely(Canvas canvas, int x, int y) {
		for (Key i : mShowList) {
			mImage[mFrameHeader[i.index][4]].draw(canvas, 1,
					mFrameHeader[i.index][0] - mFrameHeader[0][0] + x,
					mFrameHeader[i.index][1] - mFrameHeader[0][1] + y);
		}
	}
	
	public void setIteratorNum(int n) {
		ITERATOR = n;
		if (ITERATOR < 1) {
			ITERATOR = 1;
		}
	}

}
