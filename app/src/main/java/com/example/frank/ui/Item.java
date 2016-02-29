package com.example.frank.ui;

/**
 * Created by frank on 2016/1/31.
 */
public class Item {

        public static final int ITEM = 0;
        public static final int SECTION = 1;

        private int type = ITEM;
        private String text;
        private String jumpActivityName;
        public int sectionPosition;
        public int listPosition;

        public Item(int type, String text, String name) {
            this.type = type;
            this.text = text;
            this.jumpActivityName = name;
        }

        public Item(int type, String text) {
             this(type,text,"");
        }
        @Override public String toString() {
            return text;
        }

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getJumpActivityName() {
        return jumpActivityName;
    }
}
