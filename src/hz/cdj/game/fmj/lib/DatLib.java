package hz.cdj.game.fmj.lib;

import hz.cdj.game.fmj.characters.Monster;
import hz.cdj.game.fmj.characters.NPC;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.characters.ResLevelupChain;
import hz.cdj.game.fmj.characters.SceneObj;
import hz.cdj.game.fmj.goods.BaseGoods;
import hz.cdj.game.fmj.goods.GoodsDecorations;
import hz.cdj.game.fmj.goods.GoodsDrama;
import hz.cdj.game.fmj.goods.GoodsEquipment;
import hz.cdj.game.fmj.goods.GoodsHiddenWeapon;
import hz.cdj.game.fmj.goods.GoodsMedicine;
import hz.cdj.game.fmj.goods.GoodsMedicineChg4Ever;
import hz.cdj.game.fmj.goods.GoodsMedicineLife;
import hz.cdj.game.fmj.goods.GoodsStimulant;
import hz.cdj.game.fmj.goods.GoodsTudun;
import hz.cdj.game.fmj.goods.GoodsWeapon;
import hz.cdj.game.fmj.magic.MagicAttack;
import hz.cdj.game.fmj.magic.MagicAuxiliary;
import hz.cdj.game.fmj.magic.MagicEnhance;
import hz.cdj.game.fmj.magic.MagicRestore;
import hz.cdj.game.fmj.magic.MagicSpecial;
import hz.cdj.game.fmj.magic.ResMagicChain;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;



public final class DatLib {
	private static final String FILE_NAME = "DAT.LIB";//"tst.LIB";

	public static final int RES_GUT = 1; // 剧情脚本
	public static final int RES_MAP = 2; // 地图资源
	public static final int RES_ARS = 3; // 角色资源
	public static final int RES_MRS = 4; // 魔法资源
	public static final int RES_SRS = 5; // 特效资源
	public static final int RES_GRS = 6; // 道具资源
	public static final int RES_TIL = 7; // tile资源
	public static final int RES_ACP = 8; // 角色图片
	public static final int RES_GDP = 9; // 道具图片
	public static final int RES_GGJ = 10; // 特效图片
	public static final int RES_PIC = 11; // 杂类图片
	public static final int RES_MLR = 12; // 链资源

	private static DatLib instance;

	/**
	 * DAT.LIB文件的所有内容
	 */
	private byte[] mBuffer;

	/**
	 * 保存资源数据相对文件首字节的偏移量
	 */
	private HashMap<Integer, Integer> mDataOffset = new HashMap<Integer, Integer>(2048);

	private DatLib() throws IOException {
		InputStream in = null;
		try {
			in = new FileInputStream(new File("./assets/"+FILE_NAME));
			mBuffer = new byte[in.available()];
			in.read(mBuffer);
			getAllResOffset();
		} catch (IOException e) {
			throw e;
		} finally {
			if(in != null){
				in.close();
			}
		}

	}

	private void getAllResOffset() {
		int i = 0x10, j = 0x2000;

		while (mBuffer[i] != -1) {
			int key = getKey(mBuffer[i++], mBuffer[i++],
					((int) mBuffer[i++]) & 0xFF);
			int block = ((int) mBuffer[j++]) & 0xFF;
			int low = ((int) mBuffer[j++]) & 0xFF;
			int high = ((int) mBuffer[j++]) & 0xFF;
			int value = block * 0x4000 | (high << 8 | low);
			mDataOffset.put(key, value);
		}
	}

	public static void init() throws IOException {
		if (instance == null || !(instance instanceof DatLib)) {
			instance = new DatLib();
		}
	}

	public static DatLib getInstance() {
		return instance;
	}
	
	public static ResBase GetRes(int resType, int type, int index) {
		return instance.getRes(resType, type, index);
	}

	/**
	 * 
	 * @param resType
	 *            资源文件类型号1-12
	 * @param type
	 *            资源类型
	 * @param index
	 *            资源索引号
	 * @return 资源对象，不存在则返回<code>null</code>
	 */
	public ResBase getRes(int resType, int type, int index) {
		ResBase rtn = null;
		int offset = getDataOffset(resType, type, index);

		if (offset != -1) {
			switch (resType) {
			case RES_GUT:
				rtn = new ResGut();
				break;

			case RES_MAP:
				rtn = new ResMap();
				break;
				
			case RES_ARS:
				switch (type) {
				case 1: // 玩家角色
					rtn = new Player();
					break;

				case 2: // NPC角色
					rtn = new NPC();
					break;
					
				case 3: // 敌人角色
					rtn = new Monster();
					break;
					
				case 4: // 场景对象
					rtn = new SceneObj();
					break;
					
				default:
					rtn = null;
					break;
				}
				break;
				
			case RES_MRS:
				rtn = getMagic(type, index);
				break;
				
			case RES_SRS:
				rtn = new ResSrs();
				break;
				
			case RES_GRS:
				rtn = getGoods(type, index);
				break;

			case RES_TIL:
			case RES_ACP:
			case RES_GDP:
			case RES_GGJ:
			case RES_PIC:
				rtn = new ResImage();
				break;
				
			case RES_MLR:
				if (type == 1) {
					rtn = new ResMagicChain();
				} else if (type == 2) {
					rtn = new ResLevelupChain();
				}
				break;
			}
			rtn.setData(mBuffer, offset);
		} else { // 资源不存在
			//Log.e("DatLib.getRes", "resType:" + resType + " type:" + type + " index:" + index + " not found.");
		}

		return rtn;
	}
	
	private BaseGoods getGoods(int type, int index) {
		if (type >= 1 && type <= 5) {
			return new GoodsEquipment();
		}
		BaseGoods rtn = null;
		switch (type) {
		case 6:
			rtn = new GoodsDecorations();
			break;
			
		case 7:
			rtn = new GoodsWeapon();
			break;
			
		case 8:
			rtn = new GoodsHiddenWeapon();
			break;
			
		case 9:
			rtn = new GoodsMedicine();
			break;
			
		case 10:
			rtn = new GoodsMedicineLife();
			break;
			
		case 11:
			rtn = new GoodsMedicineChg4Ever();
			break;
			
		case 12:
			rtn = new GoodsStimulant();
			break;
			
		case 13:
			rtn = new GoodsTudun();
			break;
			
		case 14:
			rtn = new GoodsDrama();
			break;
		}
		return rtn;
	}

	private ResBase getMagic(int type, int index) {
		switch (type) {
		case 1:		return new MagicAttack();
		case 2:		return new MagicEnhance();
		case 3:		return new MagicRestore();
		case 4:		return new MagicAuxiliary();
		case 5:		return new MagicSpecial();
		}
		return null;
	}
	
	/**
	 * 
	 * @param resType
	 *            资源文件类型号1-12
	 * @param type
	 *            资源类型
	 * @param index
	 *            资源索引号
	 * @return 资源所在位置, 返回-1表示不存在
	 */
	private int getDataOffset(int resType, int type, int index) {
		Integer i= mDataOffset.get(getKey(resType, type, index));
		if( i==null){
			return -1;
		}
		return i;
		
		//return mDataOffset.get(getKey(resType, type, index), -1);
	}

	/**
	 * 
	 * @param resType
	 *            资源文件类型号1-12
	 * @param type
	 *            资源类型
	 * @param index
	 *            资源索引号
	 * @return 每个资源唯一的编号，用于哈希表键
	 */
	private int getKey(int resType, int type, int index) {
		return (resType << 16) | (type << 8) | index;
	}
}
