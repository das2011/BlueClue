package com.example.dius.blueclue;

import junit.framework.TestCase;

/**
 * Created by user1 on 8/20/15.
 */
public class GameManagerTest extends TestCase {

    public void testGameStartUp(){
        GameManager gameManager = GameManager.getInstance();
        assertNotNull(gameManager);
    }
}
