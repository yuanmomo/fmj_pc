package hz.cdj.game.fmj.lib;

public class ResGut extends ResBase {
	/**
	 * 脚本说明
	 */
	private String mDescription;

	/**
	 * 脚本长度，字节总数
	 */
	private int mLength;

	/**
	 * 场景事件个数
	 */
	private int mNumSceneEvent;

	/**
	 * 场景事件
	 */
	private int[] mSceneEvent;

	/**
	 * 脚本，格式为 指令号+数据
	 */
	private byte[] mScriptData;

	@Override
	public void setData(byte[] buf, int offset) {
		mType = buf[offset];
		mIndex = buf[offset + 1];
		mDescription = getString(buf, offset + 2);
		mLength = (((int) buf[offset + 0x19] & 0xFF) << 8)
				| ((int) buf[offset + 0x18] & 0xFF);
		mNumSceneEvent = (int) buf[offset + 0x1a] & 0xFF;
		mSceneEvent = new int[mNumSceneEvent];
		for (int i = 0; i < mNumSceneEvent; i++) {
			mSceneEvent[i] = ((int) buf[offset + (i << 1) + 0x1c] & 0xFF) << 8
					| ((int) buf[offset + (i << 1) + 0x1b] & 0xFF);
		}
		int len = mLength - mNumSceneEvent * 2 - 3;
		mScriptData = new byte[len];
		System.arraycopy(buf, offset + 0x1b + (mNumSceneEvent * 2),
				mScriptData, 0, len);
	}

	/**
	 * 
	 * @return 脚本说明
	 */
	public String getDescription() {
		return mDescription;
	}

	/**
	 * 场景事件，255个(1-255)。分为NPC事件、地图事件和其他事件。 NPC事件由1到40，与其资源操作号对应；地图事件由41到255，
	 * 即地图编辑器中设置的事件为1，在场景中的事件为1+40=41； 其他事件可用1到255。
	 * 
	 * @return 场景事件
	 */
	public int[] getSceneEvent() {
		return mSceneEvent;
	}

	/**
	 * 
	 * @return 脚本，格式为 指令号+数据
	 */
	public byte[] getScriptData() {
		return mScriptData;
	}

}
