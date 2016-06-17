package hz.cdj.game.fmj.magic;

/**
 * 04辅助型
 * @author Chen
 *
 */
public class MagicAuxiliary extends BaseMagic {

	@Override
	protected void setOtherData(byte[] buf, int offset) {
		mHp = get2BytesInt(buf, offset + 0x12);
	}

	private int mHp;//0~100，表示被施展者恢复生命的百分比（起死回生）
}
