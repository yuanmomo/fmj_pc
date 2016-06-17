package hz.cdj.game.fmj.lib;

import java.io.UnsupportedEncodingException;

/**
 * 每次new一个对象后，必须调用{@link ResBase#setData(byte[], int)}方法
 * 
 * @author Chen
 * 
 */
public abstract class ResBase {
	protected int mType;
	protected int mIndex;

	/**
	 * 每次new一个对象后，必须调用该方法填充各个字段
	 * 
	 * @param buf
	 *            资源的数据缓冲区
	 * @param offset
	 *            该资源在数组{@code buf}中的偏移位置，{@code buf[offset]} 为该资源的首字节
	 */
	public abstract void setData(byte[] buf, int offset);
	
	public int getType() {
		return mType;
	}
	
	public int getIndex() {
		return mIndex;
	}

	/**
	 * 获得GBK编码的字符串
	 * 
	 * @param buf
	 * @param start
	 *            字符串的第一个字节
	 * @return
	 */
	public static String getString(byte[] buf, int start) {
		int i = 0;
		while (buf[start + i] != 0)
			++i;
		try {
			return new String(buf, start, i, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 两字节无符号整型
	 * @param buf
	 * @param start
	 * @return
	 */
	public static int get2BytesInt(byte[] buf, int start) {
		return ((int)buf[start] & 0xFF) | ((int)buf[start + 1] << 8 & 0xFF00);
	}
	
	/**
	 * 两字节有符号整型
	 * @param buf
	 * @param start
	 * @return
	 */
	public static int get2BytesSInt(byte[] buf, int start) {
		int i = ((int)buf[start] & 0xFF) | ((int)buf[start + 1] << 8 & 0x7F00);
		if (((int)buf[start + 1] & 0x80) != 0) {
			return -i;
		}
		return i;
	}
	
	public static int get1ByteSInt(byte[] buf, int start) {
		int i = (int)buf[start] & 0x7f;
		if (((int)buf[start] & 0x80) != 0) {
			return -i;
		}
		return i;
	}
}
