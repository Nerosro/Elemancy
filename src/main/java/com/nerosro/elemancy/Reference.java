package com.nerosro.elemancy;

/**
 * Created by Nerosro on 23/01/2018.
 */
public class Reference {

    public static final String MODID = "elemancy";
    public static final String NAME = "Elemancy";
    public static final String VERSION = "0.1.2";
    public static final String ACCEPTED_VERSIONS = "[1.12]";

    public static final String CLIENT_PROXY = "com.nerosro.elemancy.proxy.ClientProxy";
    public static final String SERVER_PROXY = "com.nerosro.elemancy.proxy.CommonProxy";

    public static final String WAND_EMPTY ="You doubt your wand holds enough energy to convert this many stacks at once";
    //public static final String LEFTOVER_STACK ="The unleashed magic eats away at the leftover items and destroys them";
    public static final String WRONG_TOME ="The tome doesn't recognize you and refuses to open";
    public static String WELCOME_TEXT ="[General intro text]";
    //public static final String  ="";

    public enum ElemItems {
        //variable name(lower case language thing, class name)
        WAND("wand", "ItemWand"),
        TOME("tome", "ItemTome"),
        INFINGOT("inf_ingot","ItemInfIngot"),
        INFMETAL("inf_metal","ItemInfMetal"),
        ICECREAM("icecream", "ItemIceCream");

        private String unlocName;
        private String regName;

        ElemItems(String unlocName, String regName){
            this.unlocName=unlocName;
            this.regName=regName;
        }

        public String getRegName() {
            return regName;
        }

        public String getUnlocName() {
            return unlocName;
        }
    }

    public enum ElemBlocks{
        INFWOOL("inf_wool","BlockInfWool"),;

        private String unlocName;
        private String regName;

        ElemBlocks(String unlocName, String regName){
            this.unlocName = unlocName;
            this.regName = regName;
        }

        public String getRegName() {
            return regName;
        }

        public String getUnlocName() {
            return unlocName;
        }

    }
































}
