package hz.cdj.game.fmj.scene;

import hz.cdj.game.fmj.characters.NPC;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.combat.Combat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class SaveLoadGame {

	/**
	 * 是否开始新游戏
	 */
	public static boolean startNewGame = true;
	
	/**
	 * 当前地图编号
	 */
	public static int MapType = 1, MapIndex = 1;
	
	/**
	 * 屏幕左上角在地图中的位置
	 */
	public static int MapScreenX = 1, MapScreenY = 1;
	
	/**
	 * 当前脚本编号
	 */
	public static int ScriptType = 1, ScriptIndex = 1;
	
	/**
	 *  场景名称
	 */
	public static String SceneName = "";
	
	public static NPC[] NpcObjs;
	
	public static void write(ObjectOutputStream out) throws IOException {
		out.writeObject(SceneName);
		int actorNum = ScreenMainGame.sPlayerList.size();
		out.writeInt(actorNum);
		for (int i = 0; i < actorNum; i++) {
			out.writeInt(ScreenMainGame.sPlayerList.get(i).getIndex());
		}
		out.writeInt(MapType);
		out.writeInt(MapIndex);
		out.writeInt(MapScreenX);
		out.writeInt(MapScreenY);
		out.writeInt(ScriptType);
		out.writeInt(ScriptIndex);

		out.writeInt(ScreenMainGame.sPlayerList.size());
		for (int i = 0; i < ScreenMainGame.sPlayerList.size(); i++) {
			out.writeObject(ScreenMainGame.sPlayerList.get(i));
		}
		out.writeInt(Player.sMoney);
		Player.sGoodsList.write(out);
		
		out.writeObject(NpcObjs);
		
		Combat.write(out);
	}
	
	public static void read(ObjectInputStream in) throws Exception {
		SceneName = (String)in.readObject();
		int actorNum = in.readInt();
		while (actorNum-- > 0) in.readInt();
		MapType = in.readInt();
		MapIndex = in.readInt();
		MapScreenX = in.readInt();
		MapScreenY = in.readInt();
		ScriptType = in.readInt();
		ScriptIndex = in.readInt();
		
		int size = in.readInt();
		ScreenMainGame.sPlayerList.clear();
		for (int i = 0; i < size; i++) {
			Player p = (Player)in.readObject();
			ScreenMainGame.sPlayerList.add(p);
		}
		Player.sMoney = in.readInt();
		Player.sGoodsList.read(in);
		
		NpcObjs = (NPC[])in.readObject();
		
		Combat.read(in);
	}
}
