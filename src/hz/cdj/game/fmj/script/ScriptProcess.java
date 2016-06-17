package hz.cdj.game.fmj.script;

import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.characters.Character;
import hz.cdj.game.fmj.characters.Direction;
import hz.cdj.game.fmj.characters.NPC;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.combat.Combat;
import hz.cdj.game.fmj.goods.BaseGoods;
import hz.cdj.game.fmj.graphics.TextRender;
import hz.cdj.game.fmj.graphics.Util;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResBase;
import hz.cdj.game.fmj.lib.ResGut;
import hz.cdj.game.fmj.lib.ResImage;
import hz.cdj.game.fmj.lib.ResSrs;
import hz.cdj.game.fmj.scene.ScreenMainGame;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

public class ScriptProcess {

	private static ScriptProcess instance;

	private ResGut mScript;
	
	private Command[] mCmds;
	
	private ScreenMainGame mScreenMainGame;

	private ScriptProcess() {
		mCmds = new Command[] {
				cmd_music,
				cmd_loadmap,
				cmd_createactor,
				cmd_deletenpc,
				null,
				null,
				cmd_move,
				null,
				null,
				cmd_callback,
				cmd_goto,
				cmd_if,
				cmd_set,
				cmd_say,
				cmd_startchapter,
				null,
				cmd_screens,
				null,
				null,
				null,
				cmd_gameover,
				cmd_ifcmp,
				cmd_add,
				cmd_sub,
				cmd_setcontrolid,
				null,
				cmd_setevent,
				cmd_clrevent,
				cmd_buy,
				cmd_facetoface,
				cmd_movie,
				cmd_choice,
				cmd_createbox,
				cmd_deletebox,
				cmd_gaingoods,
				cmd_initfight,
				cmd_fightenable,
				cmd_fightdisenable,
				cmd_createnpc,
				cmd_enterfight,
				cmd_deleteactor,
				cmd_gainmoney,
				cmd_usemoney,
				cmd_setmoney,
				cmd_learnmagic,
				cmd_sale,
				cmd_npcmovemod,
				cmd_message,
				cmd_deletegoods,
				cmd_resumeactorhp,
				cmd_actorlayerup,
				cmd_boxopen,
				cmd_delallnpc,
				cmd_npcstep,
				cmd_setscenename,
				cmd_showscenename,
				cmd_showscreen,
				cmd_usegoods,
				cmd_attribtest,
				cmd_attribset,
				cmd_attribadd,
				cmd_showgut,
				cmd_usegoodsnum,
				cmd_randrade,
				cmd_menu,
				cmd_testmoney,
				cmd_callchapter,
				cmd_discmp,
				cmd_return,
				cmd_timemsg,
				cmd_disablesave,
				cmd_enablesave,
				cmd_gamesave,
				cmd_seteventtimer,
				cmd_enableshowpos,
				cmd_disableshowpos,
				cmd_setto,
				cmd_testgoodsnum,
		};
	}

	public static void init() {
		if (instance == null) {
			instance = new ScriptProcess();
		}
	}

	public static ScriptProcess getInstance() {
		return instance;
	}
	
	public static int get2ByteInt(byte[] data, int start) {
		return ((int)data[start] & 0xFF) | ((int)data[start + 1] << 8 & 0xFF00);
	}
	
	public static int get4BytesInt(byte[] data, int start) {
		return ((int)data[start] & 0xFF) | ((int)data[start + 1] << 8 & 0xFF00)
				| ((int)data[start + 2] << 16 & 0xFF0000) | ((int)data[start + 3] << 24);
	}
	
	public static byte[] getStringBytes(byte[] data, int start) {
		int i = 0;
		while (data[start + i] != 0)
			++i;

		byte[] rlt = new byte[++i];
		System.arraycopy(data, start, rlt, 0, i);
		
		return rlt;
	}
	
	public void setScreenMainGame(ScreenMainGame screenMainGame) {
		mScreenMainGame = screenMainGame;
	}
	
	public void loadScript(ResGut resGut) {
		mScript = resGut;
	}

	public boolean loadScript(int type, int index) {
		mScript = (ResGut)DatLib.getInstance().getRes(DatLib.RES_GUT, type, index);
		return mScript != null;
	}

	public ScriptExecutor getScriptExecutor() {
		if (mScript == null) return null;
		
		byte[] code = mScript.getScriptData();
		int pointer = 0;
		
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>(128); // offsetAddr----index of operate
		int iOfOper = 0;
		
		ArrayList<Operate> operateList = new ArrayList<Operate>();
		
		while (pointer < code.length) {
			map.put(pointer, iOfOper);
			++iOfOper;
			Command cmd = mCmds[code[pointer]];
			operateList.add(cmd.getOperate(code, pointer + 1));
			pointer = cmd.getNextPos(code, pointer + 1);
		}
		
		int[] events = mScript.getSceneEvent();
		int[] eventIndex = new int[events.length];
		for (int i = 0; i < events.length; i++) {
			if (events[i] == 0) {
				eventIndex[i] = -1; // 未使用的事件，存在于前40个中
			} else {
				eventIndex[i] = map.get(events[i] - events.length * 2 - 3);
			}
		}
		
		return new ScriptExecutor(operateList, eventIndex, map, events.length * 2 + 3);
	}

	private interface Command {

		/**
		 * 得到下一条指令的位置
		 * 
		 * @param code
		 *            指令缓冲区
		 * @param start
		 *            要执行的指令的数据开始位置
		 * @return 小于0结束，大于0为下一条指令的位置
		 */
		public int getNextPos(byte[] code, int start);
		
		public Operate getOperate(final byte[] code, final int start);
	}

	private Command cmd_music = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_music");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return null;
		}
	};

	private Command cmd_loadmap = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_loadmap");
			return start + 8;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateDrawOnce() {
				int type, index, x, y;
				{
					type = ((int)code[start] & 0xFF) | ((int)code[start + 1] << 8 & 0xFF00);
					index = ((int)code[start + 2] & 0xFF) | ((int)code[start + 3] << 8 & 0xFF00);
					x = ((int)code[start + 4] & 0xFF) | ((int)code[start + 5] << 8 & 0xFF00);
					y = ((int)code[start + 6] & 0xFF) | ((int)code[start + 7] << 8 & 0xFF00);
				}
				
				@Override
				public boolean process() {
					mScreenMainGame.loadMap(type, index, x, y);
					return true;
				}

				@Override
				public void drawOnce(Canvas canvas) {
					mScreenMainGame.drawScene(canvas);
				}
			};
		}
	};

	private Command cmd_createactor = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_createactor");
			return start + 6;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateDrawOnce() {
				
				@Override
				public boolean process() {
					mScreenMainGame.createActor(get2ByteInt(code, start),
							get2ByteInt(code, start + 2),
							get2ByteInt(code, start + 4));
					return true;
				}
				
				@Override
				public void drawOnce(Canvas canvas) {
					mScreenMainGame.drawScene(canvas);
				}
			};
		}
	};

	private Command cmd_deletenpc = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_deletenpc");
			return start + 2;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					mScreenMainGame.deleteNpc(get2ByteInt(code, start));
					return false;
				}
			};
		}
	};

	private Command cmd_move = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_move");
			return start + 6;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new Operate() {
				long time = 400;
				NPC npc;
				int dstX = get2ByteInt(code, start + 2);
				int dstY = get2ByteInt(code, start + 4);
				
				@Override
				public boolean update(long delta) {
					time += delta;
					if (time > 100) {
						Point p = npc.getPosInMap();
						if (dstX < p.x) {
							npc.walk(Direction.West);
						} else if (dstX > p.x) {
							npc.walk(Direction.East);
						} else if (dstY < p.y) {
							npc.walk(Direction.North);
						} else if (dstY > p.y) {
							npc.walk(Direction.South);
						} else {
							return false;
						}
						time = 0;
					}
					return true;
				}
				
				@Override
				public boolean process() {
					npc = mScreenMainGame.getNPC(get2ByteInt(code, start));
					return true;
				}
				
				@Override
				public void onKeyUp(int key) {
				}
				
				@Override
				public void onKeyDown(int key) {
				}
				
				@Override
				public void draw(Canvas canvas) {
					mScreenMainGame.drawScene(canvas);
				}
			};
		}
	};

	private Command cmd_callback = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_callback");
			return start;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					mScreenMainGame.exitScript();
					return false;
				}
			};
		}
	};

	private Command cmd_goto = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_goto");
			return start + 2;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					mScreenMainGame.gotoAddress(get2ByteInt(code, start));
					return false;
				}
			};
		}
	};

	private Command cmd_if = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_if");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					if (ScriptResources.globalEvents[get2ByteInt(code, start)]) {
						mScreenMainGame.gotoAddress(get2ByteInt(code, start + 2));
					}
					return false;
				}
			};
		}
	};

	private Command cmd_set = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_set");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					ScriptResources.variables[get2ByteInt(code, start)] =
							get2ByteInt(code, start + 2);
					return false;
				}
			};
		}
	};

	private Command cmd_say = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_say");
			int i = 2;
			while (code[start + i] != 0) ++i;
			return start + i + 1;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new Operate() {
				int picNum = get2ByteInt(code, start);
				ResImage headImg;
				byte[] text = getStringBytes(code, start + 2);
				int iOfText = 0;
				int iOfNext = 0;
				boolean isAnyKeyDown = false;
				RectF rWithPic = new RectF(9, 50, 151, 96 - 0.5f); // 有图边框
				Rect rWithTextT = new Rect(44, 58, 145, 75); // 上
				Rect rWithTextB = new Rect(14, 76, 145, 93); // 下
				RectF rWithoutPic = new RectF(9, 55, 151, 96 - 0.5f); // 无图边框
				Rect rWithoutTextT = new Rect(14, 58, 145, 75); // 上
				Rect rWithoutTextB = new Rect(14, 76, 145, 93); // 下
				Paint paint = new Paint();
				{
					if (picNum != 0) {
						headImg = (ResImage)DatLib.getInstance().getRes(DatLib.RES_PIC, 1, picNum);
					}
					paint.setColor(Global.COLOR_BLACK);
					paint.setStyle(Paint.Style.FILL_AND_STROKE);
				}

				@Override
				public boolean update(long delta) {
					if (isAnyKeyDown) {
						if (iOfNext >= text.length - 1) { // 最后一位是0
							return false;
						} else {
							iOfText = iOfNext;
							isAnyKeyDown = false;
						}
					}
					return true;
				}
				
				@Override
				public boolean process() {
					iOfText = 0;
					iOfNext = 0;
					return true;
				}
				
				@Override
				public void onKeyUp(int key) {
				}
				
				@Override
				public void onKeyDown(int key) {
					isAnyKeyDown = true;
				}
				
				@Override
				public void draw(Canvas canvas) {
					if (!Combat.IsActive()) {
						mScreenMainGame.drawScene(canvas);
					}
					if (picNum == 0) { // 没头像
						// 画矩形
						paint.setColor(Global.COLOR_WHITE);
						paint.setStyle(Paint.Style.FILL);
						canvas.drawRect(rWithoutPic, paint);
						// 画边框
						paint.setColor(Global.COLOR_BLACK);
						paint.setStyle(Paint.Style.STROKE);
						paint.setStrokeWidth(1);
						canvas.drawRect(rWithoutPic, paint);
						iOfNext = TextRender.drawText(canvas, text, iOfText, rWithoutTextT);
						iOfNext = TextRender.drawText(canvas, text, iOfNext, rWithoutTextB);
					} else { // 有头像
						// 画矩形
						paint.setColor(Global.COLOR_WHITE);
						paint.setStyle(Paint.Style.FILL);
						canvas.drawRect(rWithPic, paint);
						// 画边框
						paint.setColor(Global.COLOR_BLACK);
						paint.setStyle(Paint.Style.STROKE);
						paint.setStrokeWidth(1);
						canvas.drawRect(rWithPic, paint);
						canvas.drawLine(38, 50, 44, 56, paint);
						canvas.drawLine(43.5f, 56, 151, 56, paint);
						headImg.draw(canvas, 1, 13, 46);
						iOfNext = TextRender.drawText(canvas, text, iOfText, rWithTextT);
						iOfNext = TextRender.drawText(canvas, text, iOfNext, rWithTextB);
					}
				}
			};
		}
	};

	private Command cmd_startchapter = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_startchapter");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				int type, index;
				{
					type = ((int)code[start] & 0xFF) | ((int)code[start + 1] << 8 & 0xFF);
					index = ((int)code[start + 2] & 0xFF) | ((int)code[start + 3] << 8 & 0xFF);
				}
				@Override
				public boolean process() {
					mScreenMainGame.startChapter(type, index);
					return false;
				}
			};
		}
	};

	private Command cmd_screens = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_screens");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					mScreenMainGame.setMapScreenPos(get2ByteInt(code, start), get2ByteInt(code, start + 2));
					return false;
				}
			};
		}
	};

	private Command cmd_gameover = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_gameover");
			return start;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					GameView.getInstance().changeScreen(Global.SCREEN_MENU);
					return false;
				}
			};
		}
	};

	private Command cmd_ifcmp = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_ifcmp");
			return start + 6;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					if (ScriptResources.variables[get2ByteInt(code, start)]
							== get2ByteInt(code, start + 2)) {
						mScreenMainGame.gotoAddress(get2ByteInt(code, start + 4));
					}
					return false;
				}
			};
		}
	};

	private Command cmd_add = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_add");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					ScriptResources.variables[get2ByteInt(code, start)] +=
							get2ByteInt(code, start + 2);
					return false;
				}
			};
		}
	};

	private Command cmd_sub = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_sub");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					ScriptResources.variables[get2ByteInt(code, start)] -=
							get2ByteInt(code, start + 2);
					return false;
				}
			};
		}
	};

	// 伏魔记未用到
	private Command cmd_setcontrolid = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_setcontrolid");
			return start + 2;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return null;
		}
	};

	private Command cmd_setevent = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_setevent");
			return start + 2;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					ScriptResources.setEvent(get2ByteInt(code, start));
					return false;
				}
			};
		}
	};

	private Command cmd_clrevent = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_clrevent");
			return start + 2;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					ScriptResources.clearEvent(get2ByteInt(code, start));
					return false;
				}
			};
		}
	};

	/**
	 * 序号 种类
	 */
	private Command cmd_buy = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_buy");
			int i = 0;
			while (code[start + i] != 0) ++i;
			return start + i + 1;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateBuy(code, start);
		}
	};

	private Command cmd_facetoface = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_facetoface");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateDrawOnce() {
				
				private Character getCharacter(int id) {
					if (id == 0) {
						return mScreenMainGame.getPlayer();
					}
					return mScreenMainGame.getNPC(id);
				}
				
				@Override
				public boolean process() {
					Character c1 = getCharacter(get2ByteInt(code, start));
					Character c2 = getCharacter(get2ByteInt(code, start + 2));
					Point p1 = c1.getPosInMap();
					Point p2 = c2.getPosInMap();
					if (p1.x > p2.x) {
						c2.setDirection(Direction.East);
					} else if (p1.x < p2.x) {
						c2.setDirection(Direction.West);
					} else {
						if (p1.y > p2.y) {
							c2.setDirection(Direction.South);
						} else if (p1.y < p2.y) {
							c2.setDirection(Direction.North);
						}
					}
					return true;
				}
				
				@Override
				public void drawOnce(Canvas canvas) {
					mScreenMainGame.drawScene(canvas);
				}
			};
		}
	};

	private Command cmd_movie = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_movie");
			return start + 10;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new Operate() {
				int type, index, x, y, ctl;
				int downKey = 0;
				boolean isAnyKeyPressed = false;
				ResSrs movie;
				{
					type = get2ByteInt(code, start);
					index = get2ByteInt(code, start + 2);
					x = get2ByteInt(code, start + 4);
					y = get2ByteInt(code, start + 6);
					ctl = get2ByteInt(code, start + 8);
				}
				
				@Override
				public boolean update(long delta) {
					if ((ctl == 1 || ctl == 3) && isAnyKeyPressed) {
						return false;
					}
					return movie.update(delta);
				}
				
				@Override
				public boolean process() {
					movie = (ResSrs)DatLib.getInstance().getRes(DatLib.RES_SRS, type, index);
					movie.setIteratorNum(5);
					movie.startAni();
					return true;
				}
				
				@Override
				public void onKeyUp(int key) {
					if (key == downKey) {
						isAnyKeyPressed = true;
					}
				}
				
				@Override
				public void onKeyDown(int key) {
					downKey = key;
				}
				
				@Override
				public void draw(Canvas canvas) {
					if (ctl == 2 || ctl == 3) {
						mScreenMainGame.drawScene(canvas);
					}
					movie.draw(canvas, x, y);
				}
			};
		}
	};

	private Command cmd_choice = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_choice");
			int i = 0;
			while (code[start + i] != 0) ++i;
			++i;
			while (code[start + i] != 0) ++i;
			return start + i + 3;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new Operate() {
				byte[] choice1 = getStringBytes(code, start);
				byte[] choice2 = getStringBytes(code, start + choice1.length);
				Bitmap bg;
				int bgx, bgy;
				int curChoice;
				int addrOffset = choice1.length + choice2.length;
				boolean hasSelect;
				
				{
					int w = 0;
					byte[] tmp = null;
					if (choice1.length > choice2.length) {
						w = choice1.length * 8 - 8 + 6;
						tmp = new byte[choice1.length];
						System.arraycopy(choice2, 0, tmp, 0, choice2.length);
						for (int i = choice2.length - 1; i < tmp.length; i++) {
							tmp[i] = ' ';
						}
						tmp[tmp.length - 1] = 0;
						choice2 = tmp;
					} else {
						w = choice2.length * 8 - 8 + 6;
						tmp = new byte[choice2.length];
						System.arraycopy(choice1, 0, tmp, 0, choice1.length);
						for (int i = choice1.length - 1; i < tmp.length; i++) {
							tmp[i] = ' ';
						}
						tmp[tmp.length - 1] = 0;
						choice1 = tmp;
					}
					bg = Util.getFrameBitmap(w, 16 * 2 + 6);
					bgx = (160 - bg.getWidth()) / 2;
					bgy = (96 - bg.getHeight()) / 2;
				}

				@Override
				public boolean process() {
					curChoice = 0;
					hasSelect = false;
					return true;
				}
				
				@Override
				public boolean update(long delta) {
					if (hasSelect) {
						if (curChoice == 1) {
							mScreenMainGame.gotoAddress(get2ByteInt(code, start + addrOffset));
						}
						return false;
					}
					return true;
				}
				
				@Override
				public void onKeyUp(int key) {
					if (key == Global.KEY_ENTER && mLastDownKey == key) {
						hasSelect = true;
					}
				}
				
				private int mLastDownKey = -1;
				
				@Override
				public void onKeyDown(int key) {
					if (key == Global.KEY_DOWN || key == Global.KEY_UP ||
							key == Global.KEY_LEFT || key == Global.KEY_RIGHT) {
						curChoice = 1 - curChoice;
					}
					mLastDownKey = key;
				}
				
				@Override
				public void draw(Canvas canvas) {
					mScreenMainGame.drawScene(canvas);
					canvas.drawBitmap(bg, bgx, bgy, null);
					if (curChoice == 0) {
						TextRender.drawSelText(canvas, choice1, bgx + 3, bgy + 3);
						TextRender.drawText(canvas, choice2, bgx + 3, bgy + 3 + 16);
					} else {
						TextRender.drawText(canvas, choice1, bgx + 3, bgy + 3);
						TextRender.drawSelText(canvas, choice2, bgx + 3, bgy + 3 + 16);
					}
				}
			};
		}
	};

	private Command cmd_createbox = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_createbox");
			return start + 8;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					mScreenMainGame.createBox(get2ByteInt(code, start),
							get2ByteInt(code, start + 2),
							get2ByteInt(code, start + 4),
							get2ByteInt(code, start + 6));
					return false;
				}
			};
		}
	};

	private Command cmd_deletebox = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_deletebox");
			return start + 2;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					mScreenMainGame.deleteBox(get2ByteInt(code, start));
					return false;
				}
			};
		}
	};

	private Command cmd_gaingoods = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_gaingoods");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new Operate() {
				BaseGoods goods = (BaseGoods)DatLib.GetRes(DatLib.RES_GRS,
						get2ByteInt(code, start), get2ByteInt(code, start + 2));
				String msg = "获得:" + goods.getName();
				long time;
				boolean isAnyKeyPressed;
				int downKey;

				@Override
				public boolean process() {
					goods.setGoodsNum(1);
					Player.sGoodsList.addGoods(goods.getType(), goods.getIndex());
					time = 0;
					isAnyKeyPressed = false;
					downKey = -1;
					return true;
				}
				
				@Override
				public boolean update(long delta) {
					time += delta;
					if (time > 1000 || isAnyKeyPressed) {
						return false;
					}
					
					return true;
				}
				
				@Override
				public void onKeyUp(int key) {
					if (key == downKey) {
						isAnyKeyPressed = true;
					}
				}
				
				@Override
				public void onKeyDown(int key) {
					downKey = key;
				}
				
				@Override
				public void draw(Canvas canvas) {
					Util.showMessage(canvas, msg);
				}
			};
		}
	};

	private Command cmd_initfight = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_initfight");
			return start + 22;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					int[] arr = new int[8];
					for (int i = 0; i < 8; i++) {
						arr[i] = get2ByteInt(code, start + i * 2);
					}
					Combat.InitFight(arr, get2ByteInt(code, start + 16),
							get2ByteInt(code, start + 18), get2ByteInt(code, start + 20));
					return false;
				}
			};
		}
	};

	private Command cmd_fightenable = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_fightenable");
			return start;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					Combat.FightEnable();
					return false;
				}
			};
		}
	};

	private Command cmd_fightdisenable = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_fightdisenable");
			return start;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					Combat.FightDisable();
					return false;
				}
			};
		}
	};

	private Command cmd_createnpc = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_createnpc");
			return start + 8;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					mScreenMainGame.createNpc(get2ByteInt(code, start),
							get2ByteInt(code, start + 2), 
							get2ByteInt(code, start + 4),
							get2ByteInt(code, start + 6));
					return false;
				}
			};
		}
	};

	private Command cmd_enterfight = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_enterfight");
			return start + 30;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				// TODO EnterFight
				@Override
				public boolean process() {
//					mScreenMainGame.gotoAddress(get2ByteInt(code, start + 28)); // win the fight
					int[] monstersType = {get2ByteInt(code, start + 2),
							get2ByteInt(code, start + 4),
							get2ByteInt(code, start + 6)};
					int[] scr = {get2ByteInt(code, start + 8),
							get2ByteInt(code, start + 10),
							get2ByteInt(code, start + 12)};
					int[] evtRnds = {get2ByteInt(code, start + 14),
							get2ByteInt(code, start + 16),
							get2ByteInt(code, start + 18)};
					int[] evts = {get2ByteInt(code, start + 20),
							get2ByteInt(code, start + 22),
							get2ByteInt(code, start + 24)};
					int lossto = get2ByteInt(code, start + 26);
					int winto = get2ByteInt(code, start + 28);
					Combat.EnterFight(get2ByteInt(code, start), monstersType, scr, evtRnds, evts, lossto, winto);
					mScreenMainGame.exitScript();
					return false;
				}
			};
		}
	};

	private Command cmd_deleteactor = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_deleteactor");
			return start + 2;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					mScreenMainGame.deleteActor(get2ByteInt(code, start));
					return false;
				}
			};
		}
	};

	private Command cmd_gainmoney = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_gainmoney");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					Player.sMoney += get4BytesInt(code, start);
					return false;
				}
			};
		}
	};

	private Command cmd_usemoney = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_usemoney");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					Player.sMoney -= get4BytesInt(code, start);
					return false;
				}
			};
		}
	};

	private Command cmd_setmoney = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_setmoney");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					Player.sMoney = get4BytesInt(code, start);
					return false;
				}
			};
		}
	};

	private Command cmd_learnmagic = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_learnmagic");
			return start + 6;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new Operate() {
				
				boolean isAnyKeyDown;
				long timeCnt;
				
				@Override
				public boolean update(long delta) {
					timeCnt += delta;
					return timeCnt < 1000 && !isAnyKeyDown;
				}
				
				@Override
				public boolean process() {
					isAnyKeyDown = false;
					timeCnt = 0;
					return true;
				}
				
				@Override
				public void onKeyUp(int key) {
				}
				
				@Override
				public void onKeyDown(int key) {
				}
				
				@Override
				public void draw(Canvas canvas) { // TODO fix the test
					TextRender.drawText(canvas, "学会了魔法:", 0, 0);
					TextRender.drawText(canvas, "actorId:" + get2ByteInt(code, start)
							+ "t" + get2ByteInt(code, start + 2)
							+ "i" + get2ByteInt(code, start + 4), 0, 16);
				}
			};
		}
	};

	private Command cmd_sale = new Command() {
		
		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_sale");
			return start;
		}
		
		@Override
		public Operate getOperate(byte[] code, int start) {
			return new OperateSale();
		}
	};
	
	private Command cmd_npcmovemod = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_npcmovemod");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					mScreenMainGame.getNPC(get2ByteInt(code, start))
					.setCharacterState(get2ByteInt(code, start + 2));
					return false;
				}
			};
		}
	};

	private Command cmd_message = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_message");
			int i = 0;
			while (code[start + i] != 0) ++i;
			return start + i + 1;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new Operate() {
				byte[] msg = getStringBytes(code, start);
				int downKey;
				boolean isAnyKeyDown;
				
				@Override
				public boolean process() {
					downKey = -1;
					isAnyKeyDown = false;
					return true;
				}
				
				@Override
				public boolean update(long delta) {
					return !isAnyKeyDown;
				}
				
				@Override
				public void onKeyUp(int key) {
					if (downKey == key) {
						isAnyKeyDown = true;
					}
				}
				
				@Override
				public void onKeyDown(int key) {
					downKey = key;
				}
				
				@Override
				public void draw(Canvas canvas) {
					Util.showMessage(canvas, msg);
				}
			};
		}
	};

	private Command cmd_deletegoods = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_deletegoods");
			return start + 6;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					boolean r = Player.sGoodsList.deleteGoods(get2ByteInt(code, start),
							get2ByteInt(code, start + 2));
					if (!r) {
						mScreenMainGame.gotoAddress(get2ByteInt(code, start + 2));
					}
					return false;
				}
			};
		}
	};

	private Command cmd_resumeactorhp = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_resumeactorhp");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					Player p = mScreenMainGame.getPlayer(get2ByteInt(code, start));
					if (p != null) {
						p.setHP(p.getMaxHP() * get2ByteInt(code, start + 2) / 100);
					}
					return false;
				}
			};
		}
	};

	private Command cmd_actorlayerup = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_actorlayerup");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new Operate() { // TODO
				
				boolean exit = false;
				
				@Override
				public boolean update(long delta) {
					return !exit;
				}
				
				@Override
				public boolean process() {
					return true;
				}
				
				@Override
				public void onKeyUp(int key) {
					if (key == Global.KEY_CANCEL) {
						exit = true;
					}
				}
				
				@Override
				public void onKeyDown(int key) {
				}
				
				@Override
				public void draw(Canvas canvas) {
					TextRender.drawText(canvas, "cmd_actorlayerup", 10, 20);
					TextRender.drawText(canvas, "press cancel to continue", 0, 40);
				}
			};
		}
	};

	private Command cmd_boxopen = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_boxopen");
			return start + 2;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					NPC box = mScreenMainGame.getNPC(get2ByteInt(code, start));
					if (box != null) {
						box.setStep(1);
					}
					return false;
				}
			};
		}
	};

	private Command cmd_delallnpc = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_delallnpc");
			return start;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					mScreenMainGame.deleteAllNpc();
					return false;
				}
			};
		}
	};

	private Command cmd_npcstep = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_npcstep");
			return start + 6;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new Operate() {
				long time = 0;
				long interval = 0;
				int id = get2ByteInt(code, start); // 0为主角
				int faceto = get2ByteInt(code, start + 2);
				int step = get2ByteInt(code, start + 4);
				
				@Override
				public boolean update(long delta) {
					time += delta;
					return time < interval;
				}
				
				@Override
				public boolean process() {
					time = 0;
					Direction d = Direction.South;
					switch (faceto) { // 与资源文件里的不一样
					case 0: d = Direction.North; break;
					case 1: d = Direction.East; break;
					case 2: d = Direction.South; break;
					case 3: d = Direction.West; break;
					}
					if (id == 0) {
						Player p = mScreenMainGame.getPlayer();
						p.setDirection(d);
						p.setStep(step);
						interval = 300;
					} else {
						// TODO npc's step
						NPC npc = mScreenMainGame.getNPC(id);
						npc.setDirection(d);
						npc.setStep(step);
						if (mScreenMainGame.isNpcVisible(npc)) {
							interval = 300;
						} else {
							interval = 0;
						}
					}
					return true;
				}
				
				@Override
				public void onKeyUp(int key) {
				}
				
				@Override
				public void onKeyDown(int key) {
				}
				
				@Override
				public void draw(Canvas canvas) {
					mScreenMainGame.drawScene(canvas);
				}
			};
		}
	};

	private Command cmd_setscenename = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_setscenename");
			int i = 0;
			while (code[start + i] != 0) ++i;
			return start + i + 1;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					mScreenMainGame.setSceneName(ResBase.getString(code, start));
					return false;
				}
			};
		}
	};

	private Command cmd_showscenename = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_showscenename");
			return start;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new Operate() {
				long time = 0;
				String text;
				boolean isAnyKeyDown = false;
				
				@Override
				public boolean update(long delta) {
					time += delta;
					if (time > 100 && isAnyKeyDown) {
						isAnyKeyDown = false;
						return false;
					}
					return time < 1000;
				}
				
				@Override
				public boolean process() {
					text = mScreenMainGame.getSceneName();
					return true;
				}
				
				@Override
				public void onKeyUp(int key) {
				}
				
				@Override
				public void onKeyDown(int key) {
					isAnyKeyDown = true;
				}
				
				@Override
				public void draw(Canvas canvas) {
					mScreenMainGame.drawScene(canvas);
					Util.showInformation(canvas, text);
				}
			};
		}
	};

	private Command cmd_showscreen = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_showscreen");
			return start;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateDrawOnce() {
				
				@Override
				public boolean process() {
					return true;
				}
				
				@Override
				public void drawOnce(Canvas canvas) {
					mScreenMainGame.drawScene(canvas);
				}
			};
		}
	};

	private Command cmd_usegoods = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_usegoods");
			return start + 6;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					boolean b = Player.sGoodsList.deleteGoods(get2ByteInt(code, start),
							get2ByteInt(code, start + 2));
					if (!b) {
						mScreenMainGame.gotoAddress(get2ByteInt(code, start + 4));
					}
					return false;
				}
			};
		}
	};

	// 伏魔记未用到
	private Command cmd_attribtest = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_attribtest");
			return start + 10;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return null;
		}
	};

	// 伏魔记未用到
	private Command cmd_attribset = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_attribset");
			return start + 6;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return null;
		}
	};

	// 伏魔记未用到
	private Command cmd_attribadd = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_attribadd");
			return start + 6;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return null;
		}
	};

	private Command cmd_showgut = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_showgut");
			int i = 4;
			while (code[start + i] != 0) ++i;
			return start + i + 1;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new Operate() {
				ResImage imgTop, imgBottom;
				String text; // TODO change to bytes
				boolean goon = true;
				long interval = 50;
				long timeCnt = 0;
				int step = 1;
				int curY;
				Rect rect;
				
				{
					int top = ((int)code[start] & 0xFF) | ((int)code[start + 1] << 8 & 0xFF00);
					int btm = ((int)code[start + 2] & 0xFF) | ((int)code[start + 3] << 8 & 0xFF00);
					imgTop = (ResImage)DatLib.getInstance().getRes(DatLib.RES_PIC, 5, top);
					imgBottom = (ResImage)DatLib.getInstance().getRes(DatLib.RES_PIC, 5, btm);
					text = ResBase.getString(code, start + 4);
					curY = imgBottom != null ? 96 - imgBottom.getHeight() : 96;
					rect = new Rect(0,
							imgTop != null ? imgTop.getHeight() : 0,
							160, curY);
				}
				
				@Override
				public boolean process() {
					goon = true;
					interval = 50;
					timeCnt = 0;
					step = 1;
					curY = imgBottom != null ? 96 - imgBottom.getHeight() : 96;
					return true;
				}
				
				@Override
				public boolean update(long delta) {
					if (!goon) return false;
					timeCnt += delta;
					if (timeCnt >= interval) {
						timeCnt = 0;
						curY -= step;
					}
					return true;
				}
				
				@Override
				public void onKeyUp(int key) {
					if (key == Global.KEY_CANCEL) {
						goon = false;
					}
					step = 1;
					interval = 50;
				}
				
				@Override
				public void onKeyDown(int key) {
					step = 3;
					interval = 20;
				}
				
				@Override
				public void draw(Canvas canvas) {
					canvas.drawColor(Global.COLOR_WHITE);
					int e = TextRender.drawText(canvas, text, rect, curY);
					if (e != 1 && e != 2) {
						goon = false;
					}
					if (imgTop != null) {
						imgTop.draw(canvas, 1, 0, 0);
					}
					if (imgTop != null) {
						imgBottom.draw(canvas, 1, 0, 96 - imgBottom.getHeight());
					}
				}
			};
		}
	};

	private Command cmd_usegoodsnum = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_usegoodsnum");
			return start + 8;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					boolean b = Player.sGoodsList.useGoodsNum(get2ByteInt(code, start),
							get2ByteInt(code, start + 2), get2ByteInt(code, start + 4));
					if (!b) {
						mScreenMainGame.gotoAddress(get2ByteInt(code, start + 6));
					}
					return false;
				}
			};
		}
	};

	private Command cmd_randrade = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_randrade");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					if ((int)(Math.random() * 1000) <= get2ByteInt(code, start)) {
						mScreenMainGame.gotoAddress(get2ByteInt(code, start + 2));
					}
					return false;
				}
			};
		}
	};

	// 0-6中用到
	private Command cmd_menu = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_menu");
			int i = 2;
			while (code[start + i] != 0) ++i;
			return start + i + 1;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return null;
		}
	};

	private Command cmd_testmoney = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_testmoney");
			return start + 6;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					if (Player.sMoney < get4BytesInt(code, start)) {
						mScreenMainGame.gotoAddress(get2ByteInt(code, start + 4));
					}
					return false;
				}
			};
		}
	};

	// 伏魔记未用到
	private Command cmd_callchapter = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_callchapter");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return null;
		}
	};

	private Command cmd_discmp = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_discmp");
			return start + 8;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					int var = ScriptResources.variables[get2ByteInt(code, start)];
					int num = get2ByteInt(code, start + 2);
					if (var < num) {
						mScreenMainGame.gotoAddress(get2ByteInt(code, start + 4));
					} else if (var > num) {
						mScreenMainGame.gotoAddress(get2ByteInt(code, start + 6));
					}
					return false;
				}
			};
		}
	};

	private Command cmd_return = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_return");
			return start;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return null;
		}
	};

	// 伏魔记未用到
	private Command cmd_timemsg = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_timemsg");
			int i = 2;
			while (code[start + i] != 0) ++i;
			return start + i + 1;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return null;
		}
	};

	// 0-6
	private Command cmd_disablesave = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_disablesave");
			return start;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return null;
		}
	};

	// 0-6
	private Command cmd_enablesave = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_enablesave");
			return start;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return null;
		}
	};

	// 伏魔记未用到
	private Command cmd_gamesave = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_gamesave");
			return start;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return null;
		}
	};
	
	// 伏魔记未用到
	private Command cmd_seteventtimer = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_seteventtimer");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return null;
		}
	};
	
	private Command cmd_enableshowpos = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_enableshowpos");
			return start;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return null; //TODO
		}
	};
	
	private Command cmd_disableshowpos = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_disableshowpos");
			return start;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return null;//TODO
		}
	};
	
	private Command cmd_setto = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_setto");
			return start + 4;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					ScriptResources.variables[get2ByteInt(code, start + 2)] =
							ScriptResources.variables[get2ByteInt(code, start)];
					return false;
				}
			};
		}
	};
	
	private Command cmd_testgoodsnum = new Command() {

		@Override
		public int getNextPos(byte[] code, int start) {
//			System.out.println("cmd_testgoodsum");
			return start + 10;
		}

		@Override
		public Operate getOperate(final byte[] code, final int start) {
			return new OperateAdapter() {
				
				@Override
				public boolean process() {
					int goodsnum = Player.sGoodsList.getGoodsNum(get2ByteInt(code, start),
							get2ByteInt(code, start + 2));
					int num = get2ByteInt(code, start + 4);
					if (goodsnum == num) {
						mScreenMainGame.gotoAddress(get2ByteInt(code, start + 6));
					} else if (goodsnum > num) {
						mScreenMainGame.gotoAddress(get2ByteInt(code, start + 8));
					}
					return false;
				}
			};
		}
	};
}
