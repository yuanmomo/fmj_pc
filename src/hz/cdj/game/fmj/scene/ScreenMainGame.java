package hz.cdj.game.fmj.scene;

import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.characters.Direction;
import hz.cdj.game.fmj.characters.NPC;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.characters.SceneObj;
import hz.cdj.game.fmj.combat.Combat;
import hz.cdj.game.fmj.gamemenu.ScreenGameMainMenu;
import hz.cdj.game.fmj.graphics.Util;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResMap;
import hz.cdj.game.fmj.script.ScriptExecutor;
import hz.cdj.game.fmj.script.ScriptProcess;
import hz.cdj.game.fmj.script.ScriptResources;
import hz.cdj.game.fmj.views.BaseScreen;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Point;

public class ScreenMainGame extends BaseScreen {
	
	public static ScreenMainGame instance;
	
	public static List<Player> sPlayerList = new LinkedList<Player>();
	public Player mPlayer;

	private ResMap mMap;
	private Point mMapScreenPos = new Point(); // 屏幕左上角对应地图的位置
	
	private ScriptProcess mScriptSys;
	private ScriptExecutor mScriptExecutor;
	
	public ScreenMainGame() {
		instance = this;

		mScriptSys = ScriptProcess.getInstance();
		mScriptSys.setScreenMainGame(this);
		
		if (SaveLoadGame.startNewGame) { // 开始新游戏
			Combat.FightDisable();
			ScriptResources.initGlobalVar();
			ScriptResources.initGlobalEvents();
			SaveLoadGame.NpcObjs = mNPCObj;
			sPlayerList.clear();
			Player.sGoodsList.clear();
			Player.sMoney = 0;
			startChapter(1, 1);
			ScriptExecutor.goonExecute = true;
			mRunScript = true;
		} else { // 再续前缘
			loadMap(SaveLoadGame.MapType, SaveLoadGame.MapIndex,
					SaveLoadGame.MapScreenX, SaveLoadGame.MapScreenY);
			mNPCObj = SaveLoadGame.NpcObjs;
			for (int i = 0; i < mNPCObj.length; i++) {
				if (mNPCObj[i] != null) {
					mNPCObj[i].setICanWalk(mCanWalk);
				}
			}
			if (sPlayerList.size() > 0) {
				mPlayer = sPlayerList.get(0);
			} else {
				createActor(1, 4, 3);
				//Log.e("error", "存档读取出错");
			}
			mScriptSys.loadScript(SaveLoadGame.ScriptType, SaveLoadGame.ScriptIndex);
			mScriptExecutor = mScriptSys.getScriptExecutor();
			ScriptExecutor.goonExecute = true;
			mRunScript = false;
		}
//		Player.sMoney = 999999;
	}
	
	private String mSceneName = "";
	
	public void setSceneName(String name) {
		mSceneName = name;
		SaveLoadGame.SceneName = name;
	}
	
	public String getSceneName() {
		return mSceneName;
	}
	
	/**
	 * 当前是否在执行脚本
	 */
	private boolean mRunScript = true;
	
	public void exitScript() {
		mRunScript = false;
		ScriptExecutor.goonExecute = false;
	}
	
	public void runScript() {
		mRunScript = true;
	}
	
	public void startChapter(int type, int index) {
		System.out.println("ScreenMainGame.startChapter " + type + " " + index);
		mScriptSys.loadScript(type, index);
		mScriptExecutor = mScriptSys.getScriptExecutor();
//		update(0);
		ScriptExecutor.goonExecute = false;
		for (int i = 1; i <= 40; i++) {
			mNPCObj[i] = null;
		}
		ScriptResources.initLocalVar();
		SaveLoadGame.ScriptType = type;
		SaveLoadGame.ScriptIndex = index;
	}

	@Override
	public void update(long delta) {
		if (mRunScript && mScriptExecutor != null) {
			mScriptExecutor.process();
			mScriptExecutor.update(delta);
		}else if (Combat.IsActive()) { // TODO fix this test
			Combat.Update(delta);
		} else {
			for (int i = 1; i <= 40; i++) {
				if (mNPCObj[i] == null) continue;
				mNPCObj[i].update(delta);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		if (mRunScript && mScriptExecutor != null) {
			if (Combat.IsActive()) {
				Combat.Draw(canvas);
			}
			mScriptExecutor.draw(canvas);
		} else if (Combat.IsActive()) {
			Combat.Draw(canvas);
			return;
		} else {
			drawScene(canvas);
		}
	}
	
	public void drawScene(Canvas canvas) {
		if (mMap != null) {
			mMap.drawMap(canvas, mMapScreenPos.x, mMapScreenPos.y);
		}
		
		int playY = 10000;
		boolean hasPlayerBeenDrawn = false;
		if (mPlayer != null) {
			playY = mPlayer.getPosInMap().y;
		}
		
		NPC[] npcs = getSortedNpcObjs();
		for (int i = npcs.length - 1; i >= 0; --i) {
			if (!hasPlayerBeenDrawn && playY < npcs[i].getPosInMap().y) {
				mPlayer.drawWalkingSprite(canvas, mMapScreenPos);
				hasPlayerBeenDrawn = true;
			}
			npcs[i].drawWalkingSprite(canvas, mMapScreenPos);
		}
		if (mPlayer != null && !hasPlayerBeenDrawn) {
			mPlayer.drawWalkingSprite(canvas, mMapScreenPos);
		}
		Util.drawSideFrame(canvas);
	}
	
	/**
	 * 按y值从大到小排序，确保正确的遮挡关系
	 * @return
	 */
	private NPC[] getSortedNpcObjs() {
		NPC[] arr = new NPC[40];
		int i = 0;
		for (int j = 1; j <= 40; j++) {
			if (mNPCObj[j] != null) {
				arr[i++] = mNPCObj[j];
			}
		}
		
		NPC[] arr2 = new NPC[i];
		System.arraycopy(arr, 0, arr2, 0, i);
		arr = arr2;
		
		// 选择排序
		for (int j = 0; j < i; j++) {
			int max = j;
			for (int k = j + 1; k < i; k++) {
				if (arr[k].getPosInMap().y > arr[max].getPosInMap().y) {
					max = k;
				}
			}
			NPC tmp = arr[j];
			arr[j] = arr[max];
			arr[max] = tmp;
		}
		return arr;
	}

	@Override
	public void onKeyDown(int key) {
		if (mRunScript && mScriptExecutor != null) {
			mScriptExecutor.keyDown(key);
		} else if (Combat.IsActive()) {
			Combat.KeyDown(key);
			return;
		} else if (mPlayer != null) {
			switch (key) {
			case Global.KEY_LEFT:
				walkLeft();
				break;
			case Global.KEY_RIGHT:
				walkRight();
				break;
			case Global.KEY_UP:
				walkUp();
				break;
			case Global.KEY_DOWN:
				walkDown();
				break;
			case Global.KEY_ENTER:
				triggerSceneObjEvent();
				break;
			}
		}
	}

	@Override
	public void onKeyUp(int key) {
		if (mRunScript && mScriptExecutor != null) {
			mScriptExecutor.keyUp(key);
		} else if (Combat.IsActive()) {
			Combat.KeyUp(key);
			return;
		} else if (key == Global.KEY_CANCEL) {
			GameView.getInstance().pushScreen(new ScreenGameMainMenu());
		}
	}
	
	public void gotoAddress(int address) {
		mScriptExecutor.gotoAddress(address);
		mRunScript = true;
	}
	
	public void triggerEvent(int eventId) {
		if (mScriptExecutor != null) {
			mRunScript = mScriptExecutor.triggerEvent(eventId);
		}
	}
	
	/**
	 * 按enter键后，检测并触发场景对象里的事件，如NPC对话，开宝箱等
	 */
	public void triggerSceneObjEvent() {
		Player p = getPlayer();
		int x = p.getPosInMap().x;
		int y = p.getPosInMap().y;
		switch (p.getDirection()) {
		case East:	++x;	break;
		case North:	--y;	break;
		case South: ++y;	break;
		case West:	--x;	break;
		}
		
		// NPC事件
		int npcId = getNpcIdFromPosInMap(x, y);
		if (npcId != 0) {
			mRunScript = mScriptExecutor.triggerEvent(npcId);
			return;
		} else if (triggerMapEvent(x, y)) {// 地图切换
		}
	}
	
	/**
	 * 场景切换
	 * 如果地图(x,y)有地图事件，就触发该事件
	 * @param x
	 * @param y
	 */
	public boolean triggerMapEvent(int x, int y) {
		if (mMap != null && mScriptExecutor != null) {
			int id = mMap.getEventNum(x, y);
			if (id != 0) {
				mScriptExecutor.triggerEvent(id + 40);
				mRunScript = true;
				return true;
			}
		}
		// 未触发地图事件，随机战斗
		Combat.StartNewRandomCombat();
		return false;
	}
	
	/**
	 * 地图的(x,y)处，是否可行走，是否有NPC
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean canPlayerWalk(int x, int y) {
		if (mMap == null) return false;
		return mMap.canPlayerWalk(x, y) && getNpcFromPosInMap(x, y) == null;
	}
	
	public void walkLeft() {
		Point p = mPlayer.getPosInMap();
		triggerMapEvent(p.x - 1, p.y);
		if (canPlayerWalk(p.x - 1, p.y)) {
			mPlayer.walk(Direction.West);
			--mMapScreenPos.x;
			SaveLoadGame.MapScreenX = mMapScreenPos.x;
		} else {
			mPlayer.walkStay(Direction.West);
		}
	}
	
	public void walkUp() {
		Point p = mPlayer.getPosInMap();
		triggerMapEvent(p.x, p.y - 1);
		if (canPlayerWalk(p.x, p.y - 1)) {
			mPlayer.walk(Direction.North);
			--mMapScreenPos.y;
			SaveLoadGame.MapScreenY = mMapScreenPos.y;
		} else {
			mPlayer.walkStay(Direction.North);
		}
	}
	
	public void walkRight() {
		Point p = mPlayer.getPosInMap();
		triggerMapEvent(p.x + 1, p.y);
		if (canPlayerWalk(p.x + 1, p.y)) {
			++mMapScreenPos.x;
			SaveLoadGame.MapScreenX = mMapScreenPos.x;
			mPlayer.walk(Direction.East);
		} else {
			mPlayer.walkStay(Direction.East);
		}
	}
	
	public void walkDown() {
		Point p = mPlayer.getPosInMap();
		triggerMapEvent(p.x, p.y + 1);
		if (canPlayerWalk(p.x, p.y + 1)) {
			++mMapScreenPos.y;
			SaveLoadGame.MapScreenY = mMapScreenPos.y;
			mPlayer.walk(Direction.South);
		} else {
			mPlayer.walkStay(Direction.South);
		}
	}
	
	/**
	 * 载入号码n,类型m的地图，初始位置（x，y）
	 */
	public void loadMap(int type, int index, int x, int y) {
		Point tmpP = null;
		if (mPlayer != null && mMap != null) {
			tmpP = mPlayer.getPosOnScreen(mMapScreenPos);
		}
		
		mMap = (ResMap)DatLib.getInstance().getRes(DatLib.RES_MAP, type, index);
		mMapScreenPos.set(x, y);
		if (tmpP != null) {
			mPlayer.setPosOnScreen(tmpP.x, tmpP.y, mMapScreenPos);
		}
		
		SaveLoadGame.MapType = type;
		SaveLoadGame.MapIndex = index;
		SaveLoadGame.MapScreenX = x;
		SaveLoadGame.MapScreenY = y;
	}
	
	public ResMap getCurrentMap() {
		return mMap;
	}
	
	public void setMapScreenPos(int x, int y) {
		mMapScreenPos.set(x, y);
	}
	
	/**
	 * 创建主角号码actor，位置为（x，y）
	 * @param actorId
	 * @param x
	 * @param y
	 */
	public void createActor(int actorId, int x, int y) {
		mPlayer = (Player)DatLib.GetRes(DatLib.RES_ARS, 1, actorId);
		mPlayer.setPosOnScreen(x, y, mMapScreenPos);
		sPlayerList.add(mPlayer);
		mPlayer = sPlayerList.get(0);
	}
	
	public void deleteActor(int actorId) {
		for (int i = 0; i < sPlayerList.size(); i++) {
			if (sPlayerList.get(i).getIndex() == actorId) {
				sPlayerList.remove(i);
				break;
			}
		}
		
		if (sPlayerList.size() > 0) {
			mPlayer = sPlayerList.get(0);
		} else {
			mPlayer = null;
		}
	}
	
	public Player getPlayer() {
		return mPlayer;
	}
	
	public Player getPlayer(int actorId) {
		for (int i = 0; i < sPlayerList.size(); i++) {
			Player p = sPlayerList.get(i);
			if (p.getIndex() == actorId) {
				return p;
			}
		}
		return null;
	}
	
	public List<Player> getPlayerList() {
		return sPlayerList;
	}
	
	/**
	 * id--NPC或场景对象 (1-40)
	 */
	private NPC[] mNPCObj = new NPC[41];
	
	private NPC.ICanWalk mCanWalk = new NPC.ICanWalk() {
		
		@Override
		public boolean canWalk(int x, int y) {
			return mMap.canWalk(x, y) &&
					getNpcFromPosInMap(x, y) == null &&
					!mPlayer.getPosInMap().equals(x, y);
		}
	};
	
	/**
	 * 创建配角号码npc，位置为（x，y），id为操作号
	 * @param id
	 * @param npc
	 * @param x
	 * @param y
	 */
	public void createNpc(int id, int npc, int x, int y) {
		NPC npcobj = (NPC)DatLib.GetRes(DatLib.RES_ARS, 2, npc);
		npcobj.setPosInMap(x, y);
		npcobj.setICanWalk(mCanWalk);
		mNPCObj[id] = npcobj;
	}
	
	public void deleteNpc(int id) {
		mNPCObj[id] = null;
	}
	
	public void deleteAllNpc() {
		for (int i = 0; i <= 40; i++) {
			mNPCObj[i] = null;
		}
	}
	
	public NPC getNPC(int id) {
		return mNPCObj[id];
	}
	
	public boolean isNpcVisible(NPC npc) {
		Point p = npc.getPosOnScreen(mMapScreenPos);
		return p.x >= 0 && p.x < ResMap.WIDTH &&
				p.y >= 0 && p.y <= ResMap.HEIGHT;
	}
	
	public boolean isNpcVisible(int id) {
		return isNpcVisible(getNPC(id));
	}
	
	/**
	 * 得到地图的(x,y)处的NPC，没有就返回null
	 * @param x
	 * @param y
	 * @return
	 */
	public NPC getNpcFromPosInMap(int x, int y) {
		return mNPCObj[getNpcIdFromPosInMap(x, y)];
	}
	
	public int getNpcIdFromPosInMap(int x, int y) {
		for (int i = 1; i <= 40; i++) {
			if (mNPCObj[i] != null && mNPCObj[i].getPosInMap().equals(x, y)) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * 建一个宝箱，宝箱号码boxindex(角色图片，type为4)，
	 * 位置为（x，y），id为操作号（与NPC共用)
	 */
	public void createBox(int id, int boxIndex, int x, int y) {
		SceneObj box = (SceneObj)DatLib.GetRes(DatLib.RES_ARS, 4, boxIndex);
		box.setPosInMap(x, y);
		mNPCObj[id] = box;
	}
	
	public void deleteBox(int id) {
		mNPCObj[id] = null;
	}
}
