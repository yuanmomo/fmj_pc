package hz.cdj.game.fmj.combat.actions;

public class CalcDamage {

	public static int calcBaseDamage(int attack, int defense) {
		int damage;
		
		if (attack > defense) {
			damage = (int)(attack * 2 - defense * 1.6 + 0.5);
		} else if (attack > defense * 0.6) {
			damage = (int)(attack - defense * 0.6 + 0.5);
		} else {
			damage = 0;
		}
		
		return damage;
	}
	
//	public static int calcMagicDamage(int )

}
