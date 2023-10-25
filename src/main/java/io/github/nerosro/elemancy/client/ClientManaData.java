package io.github.nerosro.elemancy.client;

/**
 * Created by Nerosro on 18/07/2023.
 */
public class ClientManaData {
    private static int playerMana;
    private static double playerRegen;
    private static int currentMana;

    public static void set(int mana, double regen){
        playerMana = mana;
        playerRegen = regen;
    }
    public static void setCurrentMana(int mana){
        currentMana = mana;
    }

    public static int getPlayerMana(){
       return playerMana;
    }

    public static double getPlayerRegen() {
        return playerRegen;
    }
    public static int getCurrentMana(){
        return currentMana;
    }
}
