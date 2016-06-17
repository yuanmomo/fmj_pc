package hz.cdj.game.fmj.lib;

import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.graphics.TextRender;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;

public class ResImage extends ResBase {
	/**
	 * 切片宽
	 */
	protected int mWidth;

	/**
	 * 切片高
	 */
	protected int mHeight;

	/**
	 * 切片数量
	 */
	protected int mNumber;

	/**
	 * 是否透明
	 */
	protected boolean mTransparent;

	/**
	 * 图像数据 不透明：一位一像素，0白，1黑。 
	 * 透明：两位一像素，高位（0不透明，1透明），低位（0白，1黑）。 
	 * 注意：有冗余数据。
	 */
	protected byte[] mData;

	/**
	 * 图片数组
	 */
	protected Bitmap[] mBitmaps;

	@Override
	public void setData(byte[] buf, int offset) {
		// TODO Auto-generated method stub
		mType = buf[offset];
		mIndex = (int)buf[offset + 1] & 0xFF;
		mWidth = (int)buf[offset + 2] & 0xFF;
		mHeight = (int)buf[offset + 3] & 0xFF;
		mNumber = (int)buf[offset + 4] & 0xFF;
		mTransparent = buf[offset + 5] == 2;

		int len = mNumber * (mWidth / 8 + (mWidth % 8 != 0 ? 1 : 0))
				* mHeight * buf[offset + 5];
		mData = new byte[len];
		System.arraycopy(buf, offset + 6, mData, 0, len);

		createBitmaps();
	}

	/**
	 * 根据{@link #mData}创建位图数组
	 */
	private void createBitmaps() {
		mBitmaps = new Bitmap[mNumber];

		int[] tmp = new int[mWidth * mHeight];
		int iOfData = 0;

		if (mTransparent) {
			for (int i = 0; i < mNumber; i++) {
				int cnt = 0, iOfTmp = 0;
				for (int y = 0; y < mHeight; y++) {
					for (int x = 0; x < mWidth; x++) {
						if (((mData[iOfData] << cnt) & 0x80) != 0) {
							tmp[iOfTmp] = Global.COLOR_TRANSP;
						} else {
							tmp[iOfTmp] = ((mData[iOfData] << cnt << 1) & 0x80) != 0 ?
									Global.COLOR_BLACK : Global.COLOR_WHITE;
						}
						++iOfTmp;
						cnt += 2;
						if (cnt >= 8) {
							cnt = 0;
							++iOfData;
						}
					}
					
					if (cnt > 0 && cnt <= 7) {
						cnt = 0;
						++iOfData;
					}
					if (iOfData % 2 != 0) ++iOfData;
				}
				mBitmaps[i] = Bitmap.createBitmap(tmp, mWidth, mHeight,
						Bitmap.Config.ARGB_8888);
			} // for mNumber
		} else { // 不透明
			for (int i = 0; i < mNumber; i++) {
				int cnt = 0, iOfTmp = 0;
				for (int y = 0; y < mHeight; y++) {
					for (int x = 0; x < mWidth; x++) {
						tmp[iOfTmp++] = ((mData[iOfData] << cnt) & 0x80) != 0 ? Global.COLOR_BLACK
								: Global.COLOR_WHITE;
						if (++cnt >= 8) {
							cnt = 0;
							++iOfData;
						}
					}
					if (cnt != 0) { // 不足一字节的舍去
						cnt = 0;
						++iOfData;
					}
				} // end for (int y = ...
				mBitmaps[i] = Bitmap.createBitmap(tmp, mWidth, mHeight,
						Bitmap.Config.ARGB_8888);
			} // end for (int i = ...*/
		} // end if
	}

	/**
	 * 
	 * @param canvas
	 *            目标画布
	 * @param num
	 *            要画的切片编号,>0
	 * @param left
	 *            画到画布的最左端位置
	 * @param top
	 *            画到画布的最上端位置
	 */
	public void draw(Canvas canvas, int num, int left, int top) {
		if (num <= mNumber) {
			canvas.drawBitmap(mBitmaps[num - 1], left, top, null);
		} else {
			if (mNumber > 0) { // 要改？
				canvas.drawBitmap(mBitmaps[0], left, top, null);
			} else {
				TextRender.drawText(canvas, "烫", left, top);
			}
		}
	}
	
	public Bitmap getBitmap(int index) {
		if (index >= mNumber) {
			return null;
		}
		return mBitmaps[index].copy(Config.ARGB_8888, true);
	}

	/**
	 * 
	 * @return 切片数量
	 */
	public int getNumber() {
		return mNumber;
	}

	public int getWidth() {
		return mWidth;
	}
	
	public int getHeight() {
		return mHeight;
	}
	
	/**
	 * 得到资源的大小
	 * @return
	 */
	public int getBytesCount() {
		return mData.length + 6;
	}
}
