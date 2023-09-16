/*
* File: DumboController.java
* Created: 17 September 2002, 00:34
* Author: Stephen Jarvis
*/
/*Preamble 
To prevent the robot from colliding into walls, I used a do while loop. It is unbounded repetition since the number of steps it takes the 
robot to reach the goal is unknown. Robot.look method is used which senses the square robot is facing and returns its integer value. 
If it matches the integer value of a wall, it keeps choosing another random number until it is not a wall. */

import uk.ac.warwick.dcs.maze.logic.IRobot;

public class Ex1
{
	public void controlRobot(IRobot robot) {
		int randno;
		int direction;
		int numberOfWalls = 0; //number of walls is initialised as integer since the number of will always have to be an integer. it is also initialised as 0 to prevent the program from crashing if it's called. 
		String roadtype = ""; //created a roadtype variable with string data type to output the relevant road type depending on no of walls. 
		String directionTaken;

		
		do {
			// Select a random number
			randno = (int) Math.round(Math.random()*3);
			// Convert this to a direction
			if (randno == 0) {
				direction = IRobot.LEFT;
				directionTaken = "left";

			} else if (randno == 1) {
				direction = IRobot.RIGHT;
				directionTaken = "right";
			
			} else if (randno == 2) {
				direction = IRobot.BEHIND;
				directionTaken = "backwards";

			} else {
				direction = IRobot.AHEAD;
				directionTaken = "forward";

			}
				if (robot.look(IRobot.LEFT) == IRobot.WALL & robot.look(IRobot.RIGHT) == IRobot.WALL & robot.look(IRobot.AHEAD) == IRobot.WALL){
					roadtype = " at a deadend";// if there are walls to the left, right and ahead of the robot, it is at a deadend. Strict operators are used so that each side of the operator is evaluated. 
					numberOfWalls+=3;
					System.out.println("I'm going "+ directionTaken + roadtype); //directiontaken and roadtype variable is concatenated with the statement. 
				} else if (robot.look(IRobot.LEFT) == IRobot.WALL & robot.look(IRobot.RIGHT) == IRobot.WALL) {
					roadtype = " down a corridor"; // if there are walls to the left and right, the robot is in a corridor. 
					numberOfWalls+=2;
					System.out.println("I'm going "+ directionTaken + roadtype);
				} else if (robot.look(IRobot.LEFT) == IRobot.WALL | robot.look(IRobot.RIGHT) == IRobot.WALL) { //used strict operators so that both sides of the statement are evaluated. 
					roadtype = " at a junction"; // if there are walls to the left or right, the robot is at a junction. 
					numberOfWalls+=1;
					System.out.println("I'm going "+ directionTaken + roadtype);
				} else {
					roadtype = " at crossroads";// if there are no walls, it must be at a crossroads. 
					System.out.println("I'm going "+ directionTaken + roadtype);
				}

		} while (robot.look(direction)==IRobot.WALL);//loop is repeated until there is a wall.


		robot.face(direction); /* Face the robot in this direction */ 
	}
}