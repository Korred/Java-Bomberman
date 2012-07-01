package map;

import game.Drawable;
import game.GameData;

public abstract class Field extends Drawable {
    private static final long serialVersionUID = GameData.version;

    private int strength;

    public Field(int x, int y, int strength) {
        super(x, y, true);
        this.strength = strength;
    }

    /**
     * @return strength of the field
     */
    public int getStrength() {
        return strength;
    }

    /**
     * @param newStrength
     *            - the new strength
     */
    public void setStrength(int newStrength) {
        this.strength = newStrength;
    }

    public Field getItem() {
		int itemnumber = 3; //increased, if more than 3 items
		int random = (int)(Math.floor(Math.random()*100));
		if (random < 20){ //80% field remains empty
			return new Item(this.x, this.y, (random % itemnumber) + 1);
		}
		else{
			return new EmptyField(this.x, this.y);
		}
    }
}