/*
* File: DumboController.java
* Created: 17 September 2002, 00:34
* Author: Stephen Jarvis
*/

//Preamble//
/*Using the round function created unequal intervals. The probability of obtaining a right or behind was twice as likely than that of left or ahead. 
This is because rounding allowed for numbers between 0.5 and 1.5 to be rounded as 1 and 1.5 to 2.5 to be rounded as 2 (generating right and behind 
respectively), however only numbers between 0 and 0.5 were rounded to 0 and 2.5- 3 rounded to 3 (generating left and ahead respectively). 
Hence, the range of numbers allocated to each direction was uneven, hence the probability was unequal. To ensure fair probabilities, I generated 
random numbers between 0 (inclusive) and 4(exclusive), removing the Math.round function. If 0 was generated, direction was assigned left. 
If 1 was generated, direction was assigned right. If 2 was generated, direction was assigned behind, otherwise ahead. This created 4 equal intervals, 
hence fair probability in each case. 

To incorporate the 1 in 8 chance, a new variable (chooseDirectionRandomly) with float data type was initialised which generates random decimal numbers 
between 0 and 1. Also, float was included to notify the compiler that the programmer is aware that doubles could be generated, however are not needed. 
If the number generated is less than or equal to 0.125 (which corresponds to 1/8) direction is randomised otherwise it branches to the next part of 
the if else statement which corresponds to 7/8 probability. There is no condition included to check if there exists a wall before randomising 
the direction when 1/8 chance is met.  */

import uk.ac.warwick.dcs.maze.logic.IRobot;

public class Ex2
{
	public void controlRobot(IRobot robot) {
		int randno;
		int direction;
		int numberOfWalls = 0 ;
		String roadtype = ""; //created a roadtype variable with string data type to output the relevant road type depending on no of walls. 
		float chooseDirectionRandomly = (float)(Math.random());

		do {
			// Select a random number
			randno = (int) (Math.random()*4);

			// Convert this to a direction
			if (chooseDirectionRandomly <= 0.125) { //0.125/1 = 1/8 chance. If the chance occurs, it randomly chooses a direction as follows.
				if (randno == 0) { 
					direction = IRobot.LEFT;
					System.out.println("I'm going left " + roadtype);
				}else if (randno == 1) {
					direction = IRobot.RIGHT;
					System.out.println("I'm going right " + roadtype);
				}else if (randno == 2 ) {
					direction = IRobot.BEHIND;
					System.out.println("I'm going backwards  " + roadtype);
				}else {
					direction = IRobot.AHEAD;
					System.out.println("I'm going forward  " + roadtype);
				}

			}else if (randno == 0 ) { // This statement allows for 7/8 chance. 
				direction = IRobot.LEFT; 
				System.out.println("I'm going left " + roadtype);

			}else if (randno == 1) { // This statement allows for 7/8 chance. 
				direction = IRobot.RIGHT;
				System.out.println("I'm going right " + roadtype);

			}else if (randno == 2) { // This statement allows for 7/8 chance. 
				direction = IRobot.BEHIND;
				System.out.println("I'm going backwards  " + roadtype);

			}else { // This statement allows for 7/8 chance. 
				direction = IRobot.AHEAD;
				System.out.println("I'm going forward  " + roadtype);
			}

		} while (robot.look(direction)==IRobot.WALL); //loop is repeated until there is a wall.

		if (robot.look(IRobot.LEFT) == IRobot.WALL & robot.look(IRobot.RIGHT) == IRobot.WALL & robot.look(IRobot.AHEAD) == IRobot.WALL){
			roadtype = "at a deadend"; // if there are walls to the left, right and ahead of the robot, it is at a deadend. Strict operators are used so that each side of the operator is evaluated. 
			numberOfWalls+=3;
		} else if (robot.look(IRobot.LEFT) == IRobot.WALL & robot.look(IRobot.RIGHT) == IRobot.WALL) {
			roadtype = "down a corridor"; // if there are walls to the left and right, the robot is in a corridor. 
			numberOfWalls+=2;
		} else if (robot.look(IRobot.LEFT) == IRobot.WALL | robot.look(IRobot.RIGHT) == IRobot.WALL) {
			roadtype = "at a junction"; // if there are walls to the left or right, the robot is at a junction. 
			numberOfWalls+=1;
		} else {
			roadtype = "crossroads"; // if there are no walls, it must be at a crossroads. 
		}

		robot.face(direction); /* Face the robot in this direction */ 
	}
}