package com.badlogic.androidgames.mrnom;

import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.AndroidGame;


public class MrNomGame extends AndroidGame {

    public Screen getStartScreen() {
        return new LoadingScreen(this);
    }

}
