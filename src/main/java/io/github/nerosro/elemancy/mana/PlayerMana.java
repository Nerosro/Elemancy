package io.github.nerosro.elemancy.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Nerosro on 18/07/2023.
 */
@AutoRegisterCapability
public class PlayerMana {
    private float currentMana;
    private double regen;
    private int maxMana;
    private final int MIN_MANA=0;
    /*
    private final int LOWEST_MANA = 95;
    private final int HIGHEST_MANA = 105;
    private final Random rand;
    */
    public PlayerMana() {
        Random rand = new SecureRandom();
        this.maxMana = rand.nextInt(95, 106);
        this.currentMana = this.maxMana;

        var randRegen = rand.nextDouble(0.10, 0.201);
        var calcRegen = randRegen + ((double) (105 - maxMana) /100);
        this.regen = Math.round(calcRegen * 100d) / 100d;  //FROM 0.1 - 0.3
    }

    public int getCurrentMana() {
        return (int) currentMana;
    }

    public int getMaxMana(){
        return maxMana;
    }

    public double getRegenAmount(){
        return regen;
    }

    public void regenMana(double boost){
        //Use regen value
        if(currentMana < maxMana) {
            this.currentMana += (regen * boost);
            //System.out.println("regening Mana: " + getRegenAmount());
        } else {
            this.currentMana = maxMana;
        }
    }
    public int useMana(int amount){
        currentMana = Math.max(currentMana - amount, MIN_MANA);
        return (int) currentMana;
    }

    public void increaseMana(int amount){
        currentMana += amount;
    }

    public void copyFrom(PlayerMana source){ //Select what NBT-Data to copy to new clone in here!!!
        this.maxMana = source.maxMana;
        this.regen = source.regen;

    }

    public void lowerManaOnDeath(){
        this.currentMana = this.maxMana/2; //Can do some logic to respawn with not all mana fully charged?
    }

    public void saveNBTData(CompoundTag nbt){
        nbt.putInt("maxMana", maxMana);
        nbt.putDouble("regen", regen);
        //nbt.putInt("currentMana", (int) currentMana);
    }

    public void loadNBTData(CompoundTag nbt){
        maxMana = nbt.getInt("maxMana");
        regen = nbt.getDouble("regen");
        //currentMana = nbt.getInt("currentMana");
    }

    @Override
    public String toString() {
        return "PlayerMana{" +
                //"currentMana=" + currentMana +
                "maxMana:" + maxMana +
                ", regen:" + regen +
                '}';
    }
}
