package hz.cdj.game.fmj.goods;

/**
 * 12兴奋剂
 * @author Chen
 *
 */
public class GoodsStimulant extends BaseGoods {

	@Override
	protected void setOtherData(byte[] buf, int offset) {
		mdfPercent = (int)buf[offset + 0x18] & 0xff;
		matPercent = (int)buf[offset + 0x19] & 0xff;
		mSpeedPercent = (int)buf[offset + 0x1b] & 0xff;
		mForAll = ((int)buf[offset + 0x1c] & 0x10) != 0;
	}

	private int mdfPercent;
	private int matPercent;
	private int mSpeedPercent;
	private boolean mForAll;
	
	
	@Override
	public boolean effectAll() {
		return mForAll;
	}
	
	
}
