package com.FlappyBird;

import java.io.IOException;
import java.util.Random;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

public class MyFlappyBird extends GameCanvas implements Runnable{
	private boolean playingGame = false;
	private boolean jump, pass;
	private int xBird, yBird, jumpMax;
	private int colStreet, xStreet;
	private final int yStreet = 280;
	private Sprite birdSprite;
	
	private LayerManager mLayerManager;
	private TiledLayer backgroundLayer, streetLayer, numberLayer, bloodLayer;
	private TiledLayer[] pipeUpLayer = new TiledLayer[3];
	private TiledLayer[] pipeDownLayer = new TiledLayer[3];
	private Sprite streetSprite;
	private Image imgBackground, imgPipeUp, imgPipeDown, imgBird, imgStreet, imgBlood, imgNumber;
	private int[] yPipe = new int[3];
	private int[] xPipe = new int[3];
	private final int gate = 60;
	private int distance = 80;
	private Random rd = new Random();
	private int currentPipe= 0;
	private int nextPipe=1;
	private int previousPipe=2;
	private boolean CRASH;
	public boolean DIE;
	private boolean touchToStart;
	private boolean playAgain;
	private int mark; 
	
	public MyFlappyBird(boolean arg0) {
		super(arg0);
		initImage()	;		
		createVariant();
		mLayerManager = new LayerManager();
		DIE = false;
	}
	
	private void createNumber() {
		numberLayer = new TiledLayer(3, 1, imgNumber, 33, 42);
		numberLayer.setCell(2, 0, 1);		
		numberLayer.setPosition(30, 30);
		mLayerManager.append(numberLayer);		
	}

	public void run() {
		Graphics g = getGraphics();
		while (playingGame){
			update();
			paintBird(g);
			flushGraphics();
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

	private void createVariant(){
		currentPipe= 0;
		nextPipe=1;
		previousPipe=0;		
		playAgain = false;
		xBird = getWidth()/4;
		yBird = getHeight()/2;		
		xPipe[currentPipe] = getWidth();
		yPipe[currentPipe] = rd.nextInt(5)*(-10);
		DIE = false;
		CRASH = false;
		touchToStart = false;
		mark = 0;		
	}
	
	public void startGame(){				
		createBlood();
		createBird();				
		createNumber();
		createStreet();		
		createPipe(xPipe[currentPipe], yPipe[currentPipe], 0);		
		createBackGround();
		playingGame = true;
		Thread t = new Thread(this);
		t.start();
	}
	
	private void initImage() {
		try {
			imgBird = Image.createImage("/bird.png");
			imgBackground = Image.createImage("/atlas.png");
			imgPipeUp = Image.createImage("/pipeup.png");
			imgPipeDown = Image.createImage("/pipedown.png");
			imgStreet = Image.createImage("/ground.png");
			imgBlood = Image.createImage("/blood.png");
			imgNumber = Image.createImage("/number.png");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private void createBird() {
		birdSprite = new Sprite(imgBird, 25, 22);
		birdSprite.setPosition(xBird,yBird);		
		mLayerManager.append(birdSprite);		
	}
	
	private void createStreet() {	
		colStreet = 0;
		streetLayer = new TiledLayer(100, 1, imgStreet, 332, 78);
		for (int i = 0 ; i<100; i++)
			streetLayer.setCell(i, 0, 1);
		streetLayer.setPosition(xStreet, yStreet);
		mLayerManager.append(streetLayer);		
	}
	
	private void createBlood(){
		bloodLayer = new TiledLayer(1, 1, imgBlood, 240, 240);
		bloodLayer.setCell(0, 0, 1);
		bloodLayer.setPosition(0, 70);		
		mLayerManager.append(bloodLayer);
		bloodLayer.setVisible(false);
	}
		
	private void createBackGround() {
		backgroundLayer = new TiledLayer(1, 1, imgBackground, 240, 320);
		backgroundLayer.setCell(0, 0, 1);
		backgroundLayer.setPosition(0, 0);
		mLayerManager.append(backgroundLayer);
	}
	
	private void createPipe(int xPipe, int yPipe, int numPipe){
		pipeDownLayer[numPipe] = new TiledLayer(1, 1, imgPipeDown, 35,212);
		pipeDownLayer[numPipe].setCell(0, 0, 1);
		pipeDownLayer[numPipe].setPosition(xPipe, yPipe);
		pipeUpLayer[numPipe] = new TiledLayer(1, 1, imgPipeUp, 35,212);
		pipeUpLayer[numPipe].setCell(0, 0, 1);
		pipeUpLayer[numPipe].setPosition(xPipe, 212-Math.abs(yPipe)+gate);		
		mLayerManager.insert(pipeUpLayer[numPipe],3);
		mLayerManager.insert(pipeDownLayer[numPipe], 3);
	}
	
	private void removePipe(int numPipe){
		mLayerManager.remove(pipeUpLayer[numPipe]);
		mLayerManager.remove(pipeDownLayer[numPipe]);
	}
	
	private void paintBird(Graphics g) {		
		if ((!DIE)&&(!CRASH)){	
			paintBird_Alive(g);
			showMark();
		}
		else if (DIE)
			paintBird_Die(g);			
		else if (CRASH)
			paintBird_Crash(g);
		mLayerManager.paint(g, 0, 0);
	}

	private void showMark() {		
		if(pass){			
			int a, b, c, xNumber;
			a = mark / 100;
			b = mark%100/10;
			c = mark %10;
			xNumber = 30;					
			numberLayer.setCell(2, 0, c+1);			
			if (mark>9){
				xNumber += 15;
				numberLayer.setCell(1, 0, b+1);				
				if (mark >99){
					xNumber += 15;
					numberLayer.setCell(0, 0, a+1);					
				}
			}
			numberLayer.setPosition(xNumber, 30);			
			mLayerManager.insert(numberLayer, 1);
			pass = false;
		}
	}

	private void paintBird_Crash(Graphics g) {
		birdSprite.setPosition(xBird, yBird+=20);	
	}

	private void paintBird_Die(Graphics g) {				
		try {
			bloodLayer.setVisible(true);
			playingGame = false;
			Thread.sleep(500);
			playAgain = true;
			CRASH = false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void paintBird_Alive(Graphics g) {
		birdSprite.setPosition(xBird, yBird);
		streetLayer.setPosition(xStreet-=2, yStreet);		
		if (touchToStart){
			try{
				for (int i = 0 ; i <3 ; i++){
					pipeDownLayer[i].setPosition(xPipe[i]--, yPipe[i]);
					pipeUpLayer[i].setPosition(xPipe[i]--, 212-Math.abs(yPipe[i])+gate);
				}
			} catch (NullPointerException e){
			}
		}	
	}

	private void update(){
		if ((!CRASH) || (!DIE))
			updateAlive();
		else
			updateDie();
	}
	
	private void updateDie() {
		if (birdSprite.collidesWith(streetLayer, true))
			DIE = true;
		}		

	private void updateAlive() {		
		updateAlive_Bird();
		updateAlive_Pipe();
		updateAlive_RemovePipe();
		updateAlive_Collision();
		updateAlive_Mark();
		updateAlive_Street();
	}
	
	private void updateAlive_Street() {
		if (xStreet == -30)
			streetLayer.setPosition(0, yStreet);
		
	}

	private void updateAlive_Mark() {
		if (xPipe[previousPipe] == xBird-10){
			mark++;
			pass = true;
		}
	}

	private void updateAlive_Collision() {
		try{
			if (birdSprite.collidesWith(streetLayer, true))
				DIE = true;		
			if ((birdSprite.collidesWith(pipeUpLayer[previousPipe], true))	||	(birdSprite.collidesWith(pipeDownLayer[previousPipe], true))){
				CRASH = true;
			}
		}catch (NullPointerException e){			
		}		
	}

	private void updateAlive_RemovePipe() {
		if (xPipe[previousPipe]<-10){
			try{
				removePipe(previousPipe);		
			}catch (NullPointerException e){				
			}
		}		
	}

	private void updateAlive_Pipe() {
		if (xPipe[currentPipe] < getWidth()-(35+distance)){		
			switch (rd.nextInt(2)) {
			case 0: distance = 80;	break;
			case 1:	distance = 70;	break;
			}			
//Create Next Pipe
			xPipe[nextPipe]=getWidth();
			yPipe[nextPipe] = rd.nextInt(5)*(-40);
			createPipe(xPipe[nextPipe],yPipe[currentPipe] , nextPipe);			
//Change Current Pipe
			if (currentPipe == 2)
				currentPipe = 0;
			else currentPipe++;
			switch (currentPipe) {
			case 0:
				nextPipe = 1;
				previousPipe = 2;
				break;
			case 1:
				nextPipe = 2;
				previousPipe = 0;
				break;
			case 2:
				nextPipe = 0;
				previousPipe = 1;
				break;
			}					
		}
		
	}

	private void updateAlive_Bird() {
		if (jump){			
			if (yBird < jumpMax)			
				jump =false;
			else 
				yBird -= 10;
		}
		else if (touchToStart)
			yBird+=2;
	}

	protected void pointerPressed(int x, int y){
		if (playingGame){
			touchToStart = true;
			jump = true;
			if(yBird>=17)
				jumpMax = yBird - 17;
			else
				jumpMax = 0;
		}
		if( playAgain){
			PlayAgain();
			
		}
	}	
	
	private void PlayAgain(){	
		for (int i = 0 ; i<3 ; i++)
			removePipe(i);
		mLayerManager = new LayerManager();
		CRASH = false;
		createVariant();
		startGame();
	}
}
