package hz.cdj.game.fmj.script;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Canvas;

public class ScriptExecutor {
	public static boolean goonExecute = true;
	
	private ArrayList<Operate> mOperateList;
	
	/**
	 * 当前正在执行的操作在mOperateList中的位置
	 */
	private int mCurExeOperateIndex;
	
	/**
	 * 当前是否正在执行 update() draw()
	 */
	private boolean mIsExeUpdateDraw;

	/**
	 * mEventIndex[i]等于触发事件i+1时，要执行的Operate在list中的序号，
	 * -1表示不存在
	 */
	private int[] mEventIndex;
	
	/**
	 * address offset --- operate's index of mOperateList
	 */
	private HashMap<Integer, Integer>  mMapAddrOffsetIndex;
	
	/**
	 * code数据前的长度
	 */
	private int mHeaderCnt;
	
	/**
	 * 
	 * @param list 一个脚本文件对应的操作表
	 * @param eventIndex eventIndex[i]等于触发事件i+1时，要执行的Operate在list中的序号
	 * @param map 地址偏移-序号
	 */
	public ScriptExecutor(ArrayList<Operate> list, int[] eventIndex, HashMap<Integer, Integer> map, int headerCnt) {
		mOperateList = list;
		mEventIndex = eventIndex;
		mCurExeOperateIndex = 0;
		mIsExeUpdateDraw = false;
		mMapAddrOffsetIndex = map;
		mHeaderCnt = headerCnt;
	}

	/**
	 * 触发地图事件,场景切换，NPC对话，开宝箱等
	 * @param eventId 是否成功触发
	 */
	public boolean triggerEvent(int eventId) {
		if (eventId > mEventIndex.length) {
			return false;
		}
		
		int index = mEventIndex[eventId-1];
		if (index != -1) {
			mCurExeOperateIndex = index;
			mIsExeUpdateDraw = false;
			return true;
		}
		return false;
	}
	
	public void gotoAddress(int address) {
		mCurExeOperateIndex = mMapAddrOffsetIndex.get(address - mHeaderCnt);
		if (mIsExeUpdateDraw) { // 不在Operate.process()中调用的gotoAddress
			mIsExeUpdateDraw = false;
			--mCurExeOperateIndex;
		} else { // 在Operate.process()中调用的gotoAddress
			goonExecute = false; // mark 下次调用process再执行
		}
	}
	
	public void process() {
		if (!mIsExeUpdateDraw) {
			for (; mCurExeOperateIndex < mOperateList.size() && goonExecute; ) {
				Operate oper = mOperateList.get(mCurExeOperateIndex);
				if (oper != null && oper.process()) { // 执行 update draw
					mIsExeUpdateDraw = true;
					return;
				}
				if (!goonExecute) {
					goonExecute = true;
					return;
				}
				++mCurExeOperateIndex;
			}
			// 正常情况不回执行到这里，脚本最后一句是callback
		}
	}
	
	public void update(long delta) {
		if (mIsExeUpdateDraw) {
			if (!mOperateList.get(mCurExeOperateIndex).update(delta)) { // 退出当前操作
				mIsExeUpdateDraw = false;
				++mCurExeOperateIndex;
			}
		}
	}
	
	public void draw(Canvas canvas) {
		if (mIsExeUpdateDraw) {
			mOperateList.get(mCurExeOperateIndex).draw(canvas);
		} else {
//			mOperateList.get(mLastIndex).draw(canvas);
		}
	}
	
	public void keyDown(int key) {
		if (mIsExeUpdateDraw) {
			mOperateList.get(mCurExeOperateIndex).onKeyDown(key);
		}
	}
	
	public void keyUp(int key) {
		if (mIsExeUpdateDraw) {
			mOperateList.get(mCurExeOperateIndex).onKeyUp(key);
		}
	}
	
}
