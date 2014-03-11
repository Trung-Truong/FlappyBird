import java.util.Random;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

//import com.FlappyBird.BirdJump;
import com.FlappyBird.MyFlappyBird;


public class MainFlappyBird extends MIDlet implements CommandListener {
	private MyFlappyBird myFlappyBird;
	private Display mDisplay;
	private Command cmdExit;
	
		
	public MainFlappyBird() {
		cmdExit = new Command("",Command.BACK,0);
		myFlappyBird = new MyFlappyBird(true);
		myFlappyBird.addCommand(cmdExit);
		myFlappyBird.setCommandListener(this);
		mDisplay = Display.getDisplay(this);		
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		mDisplay.setCurrent(myFlappyBird);
		myFlappyBird.startGame();
	}

	public void commandAction(Command c, Displayable d) {
		if (c==cmdExit){
			notifyDestroyed();
		}
		
	}

}
