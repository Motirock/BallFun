/*
This class is used to store Actions which handle what happens when keys are pressed.

How to add an action for a key:
	1. Create a boolean to keep track of if a certain key is pressed. I format it as [keyName]IsPressed. For this example, I'll being using the "up" key stroke (whether that is by pressing w or the up arrow).  
	
	2. Create two new classes with a relevant name, one for handling the key being pressed and the other for being released.
		Example:
		"
		public class UpPressedAction extends AbstractAction {}
		
		public class UpReleasedAction extends AbstractAction {}
		"
	
	3. Add the actionPerformed method.
		Example:
		"
		public class UpPressedAction extends AbstractAction {
			@Override public void actionPerformed(ActionEvent e) {
				upIsPressed = true;
			}
		}
		
		public class UpReleasedAction extends AbstractAction {
			@Override public void actionPerformed(ActionEvent e) {
				upIsPressed = false;
			}
		}
		"
	
	4. To continue with the process visit github.com/Motirock/An-Introduction-To-Java-Graphics/tree/main/Part%201/1.10
*/

package main;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class KeyHandler {
	public boolean upArrowIsPressed, downArrowIsPressed, leftArrowIsPressed, rightArrowIsPressed, spacePressed;

	//Arrow keys
	public class UpPressedAction extends AbstractAction {
		@Override public void actionPerformed(ActionEvent e) {
			upArrowIsPressed = true;
		}
	}
	public class UpReleasedAction extends AbstractAction {
		@Override public void actionPerformed(ActionEvent e) {
			upArrowIsPressed = false;
		}
	}
	public class DownPressedAction extends AbstractAction {
		@Override public void actionPerformed(ActionEvent e) {
			downArrowIsPressed = true;
		}
	}
	public class DownReleasedAction extends AbstractAction {
		@Override public void actionPerformed(ActionEvent e) {
			downArrowIsPressed = false;
		}
	}
	public class LeftPressedAction extends AbstractAction {
		@Override public void actionPerformed(ActionEvent e) {
			leftArrowIsPressed = true;
		}
	}
	public class LeftReleasedAction extends AbstractAction {
		@Override public void actionPerformed(ActionEvent e) {
			leftArrowIsPressed = false;
		}
	}
	public class RightPressedAction extends AbstractAction {
		@Override public void actionPerformed(ActionEvent e) {
			rightArrowIsPressed = true;
		}
	}
	public class RightReleasedAction extends AbstractAction {
		@Override public void actionPerformed(ActionEvent e) {
			rightArrowIsPressed = false;
		}
	}

	public class SpacePressedAction extends AbstractAction {
		@Override public void actionPerformed(ActionEvent e) {
			spacePressed = true;
		}
	}
	public class SpaceReleasedAction extends AbstractAction {
		@Override public void actionPerformed(ActionEvent e) {
			spacePressed = false;
		}
	}
}	
