package hz.cdj.game.fmj.goods;

import hz.cdj.game.fmj.lib.DatLib;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 物品管理
 * @author Chen
 *
 */
public class GoodsManage {

	/**
	 * 保存只能用一次的物品，暗器、药品等
	 */
	LinkedList<BaseGoods> mGoodsList = new LinkedList<BaseGoods>();
	/**
	 * 装备链表
	 */
	LinkedList<BaseGoods> mEquipList = new LinkedList<BaseGoods>();
	
	public List<BaseGoods> getGoodsList() {
		return mGoodsList;
	}
	
	public List<BaseGoods> getEquipList() {
		return mEquipList;
	}
	
	/**
	 * 获取装备链表中的i号装备
	 * @param i
	 * @return 当<code>i < 0 || i >= list.size()</code>时，返回<code>null</code>
	 */
	public BaseGoods getEquip(int i) {
		if (i >= 0 && i < mEquipList.size()) {
			return mEquipList.get(i);
		}
		return null;
	}
	
	/**
	 * 返回装备种类数目
	 * @return
	 */
	public int getEquitTypeNum() {
		return mEquipList.size();
	}
	
	/**
	 * 获取一次性物品链表中i位置的物品
	 * @param i
	 * @return 当<code>i < 0 || i >= list.size()</code>时，返回<code>null</code>
	 */
	public BaseGoods getGoods(int i) {
		if (i >= 0 && i < mGoodsList.size()) {
			return mGoodsList.get(i);
		}
		return null;
	}
	
	/**
	 * 返回一次性物品种类数目
	 * @return
	 */
	public int getGoodsTypeNum() {
		return mGoodsList.size();
	}
	
	/**
	 * 获取链表中type index号物品
	 * @param type
	 * @param index
	 * @return 当该种物品不存在时返回<code>null</code>
	 */
	public BaseGoods getGoods(int type, int index) {
		if (type >= 1 && type <= 7) {
			return getGoods(mEquipList, type, index);
		} else if (type >= 8 && type <= 14) {
			return getGoods(mGoodsList, type, index);
		}
		return null;
	}
	
	private BaseGoods getGoods(LinkedList<BaseGoods> list, int type, int index) {
		Iterator<BaseGoods> iter = list.iterator();
		while (iter.hasNext()) {
			BaseGoods i = iter.next();
			if (i.getType() == type && i.getIndex() == index) {
				return i;
			}
		}
		return null;
	}
	
	/**
	 * 得到type index号物品的数量
	 * @param type
	 * @param index
	 * @return 物品数量
	 */
	public int getGoodsNum(int type, int index) {
		int num = 0;
		if (type >= 1 && type <= 7) {
			num = getGoodsNum(mEquipList, type, index);
		} else if (type >= 8 && type <= 14) {
			num = getGoodsNum(mGoodsList, type, index);
		}
		return num;
	}
	
	private int getGoodsNum(LinkedList<BaseGoods> list, int type, int index) {
		Iterator<BaseGoods> iter = list.iterator();
		while (iter.hasNext()) {
			BaseGoods i = iter.next();
			if (i.getType() == type && i.getIndex() == index) {
				return i.getGoodsNum();
			}
		}
		return 0;
	}
	
	/**
	 * 
	 * @param goods
	 */
	public void addGoods(int type, int index, int num) {
		if (type >= 1 && type <= 7) {
			addGoods(mEquipList, type, index, num);
		} else if (type >= 8 && type <= 14) {
			addGoods(mGoodsList, type, index, num);
		}
	}
	
	public void addGoods(int type, int index) {
		if (type >= 1 && type <= 7) { // 装备
			addGoods(mEquipList, type, index, 1);
		} else if (type >= 8 && type <= 14) { // 物品，使用一次
			addGoods(mGoodsList, type, index, 1);
		}
	}
	
	/**
	 * 将type index号物品添加到list链表中<p>
	 * 如果有该号物品，则计数加一<p>
	 * 没有则加入该物品
	 * @param list
	 * @param type
	 * @param index
	 */
	private void addGoods(LinkedList<BaseGoods> list, int type, int index, int num) {
		Iterator<BaseGoods> iter = list.iterator();
		while (iter.hasNext()) {
			BaseGoods i = iter.next();
			if (i.getType() == type && i.getIndex() == index) { // 数量+1
				i.addGoodsNum(num);
				return;
			}
		}
		// 加入链表
		BaseGoods item = (BaseGoods)DatLib.GetRes(DatLib.RES_GRS, type, index);
		item.setGoodsNum(num);
		list.add(item);
	}
	
	/**
	 * 物品数量减一，如果数量为0，则删除物品
	 * @param type
	 * @param index
	 * @return 删除成功返回<code>true</code> 不存在该物品返回<code>false</code>
	 */
	public boolean deleteGoods(int type, int index) {
		return useGoodsNum(type, index, 1);
	}

	/**
	 * 物品数量减num，如果数量不足则返回false
	 * @param type
	 * @param index
	 * @return
	 */
	public boolean useGoodsNum(int type, int index, int num) {
		if (type >= 1 && type <= 7) { // 装备
			return deleteGoods(mEquipList, type, index, num);
		} else if (type >= 8 && type <= 14) { // 物品，使用一次
			return deleteGoods(mGoodsList, type, index, num);
		}
		return false;
	}
	
	private boolean deleteGoods(LinkedList<BaseGoods> list, int type, int index, int num) {
		Iterator<BaseGoods> iter = list.iterator();
		while (iter.hasNext()) {
			BaseGoods i = iter.next();
			if (i.getType() == type && i.getIndex() == index) { // 数量-num
				if (i.getGoodsNum() < num) {
					return false;
				}
				i.addGoodsNum(-num);
				if (i.getGoodsNum() <= 0) {
					iter.remove();
				}
				return true;
			}
		}
		return false;
	}
	
	public void clear() {
		mEquipList.clear();
		mGoodsList.clear();
	}
	
	public void read(ObjectInputStream in) throws IOException {
		clear();
		int size = in.readInt();
		for (int i = 0; i < size; i++) {
			BaseGoods g = (BaseGoods)DatLib.GetRes(DatLib.RES_GRS, in.readInt(), in.readInt());
			g.setGoodsNum(in.readInt());
			mEquipList.add(g);
		}
		
		size = in.readInt();
		for (int i = 0; i < size; i++) {
			BaseGoods g = (BaseGoods)DatLib.GetRes(DatLib.RES_GRS, in.readInt(), in.readInt());
			g.setGoodsNum(in.readInt());
			mGoodsList.add(g);
		}
	}
	
	public void write(ObjectOutputStream out) throws IOException {
		int size = mEquipList.size();
		out.writeInt(size);
		for (int i = 0; i < size; i++) {
			BaseGoods g = mEquipList.get(i);
			out.writeInt(g.getType());
			out.writeInt(g.getIndex());
			out.writeInt(g.getGoodsNum());
		}
		
		size = mGoodsList.size();
		out.writeInt(size);
		for (int i = 0; i < size; i++) {
			BaseGoods g = mGoodsList.get(i);
			out.writeInt(g.getType());
			out.writeInt(g.getIndex());
			out.writeInt(g.getGoodsNum());
		}
	}
}
