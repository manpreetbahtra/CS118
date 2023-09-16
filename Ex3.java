/*
 * File:    Broken   .java
 * Created: 7 September 2001
 * Author:  Stephen Jarvis
 */

/* To check whether the target is to the north of the robot, isTargetNorth method is called. Y-coordinate of the
target's position with y coordinate of the robot's position using arithmetic operators. If y coordinate of the target is less
than robot's y coordinate, the target is to the north of robot. If their y coordinate is the same, they are on the same level
vertically. Otherwise, the target is to the south of the robot.

To check whether the target is to the east of the robot, isTargetEast method is used. x coordinates of the robot's and target's 
location is compared. If target's x coordinate is greater than robot's, then the target is to the east of robot. If the target's
x coordinate is less than robot's, the target is to the west of the robot, otherwise x coordinates are equal 
and they are on the same level horizontally.

To test the north method, I tested the logical paths of the method. I tested the case when y of target is less than robot's y, in 
it correctly outputted north; the case where target and robot were equal vertically, again outputting the expected result. I also tested 
when robot was to the north of target, and it produced an output of -1. Similar testing approach was used for isTargetEast. 
I used this approach because there were only 3 logical paths that needed to be tested in each method. Hence, it was easy to evaluate
all the cases to see if any produced errors. note- terminal prints the direction in words and the number too. So you will see 2 numbers and 2 words for direction. 

For the lookheading function, I used the getHeading method to save the robot's initial heading, so that the robot could return back to its heading after 
checking for wall, beenbefore and passage. Then I calculated the difference between the heading given and heading that the robot is facing. If statement
is then used to check which relative direction robot should look in. Then, the robot is set to its initial heading and the result of the look method is returned.

For the heading controller method, I created array lists. In one of the array list, directions which head the robot towards the target(priorities) were stored. 
In possibilties arraylist, all headings were stored. If the output from isTargetNorth or isTargetEast is true, the corresponding heading is added to the priorities 
array. To remove wall facing headings, lookHeading method is used with the heading from arrays. If lookHeading method does not output wall, the heading is added to a new
arraylist(named priority/possibility without walls). If there are no elements in the priority without walls array, a random direction from possibilties without walls is 
chosen, otherwise they are randomly chosen from the priority without walls array. This then becomes the output for the method. */

import uk.ac.warwick.dcs.maze.logic.IRobot;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Ex3
{

   private int lookHeading (IRobot robot, int absoluteDirection){
   
      int initialHeading = robot.getHeading(); //saves robot's intial heading
      int differenceBetweenAbsoluteAndInitial = absoluteDirection - initialHeading;
      int objectAhead;  
      int directionToLookIn;
      

      if (differenceBetweenAbsoluteAndInitial ==-1 | differenceBetweenAbsoluteAndInitial ==3) {
         directionToLookIn = IRobot.LEFT;
      } else if (differenceBetweenAbsoluteAndInitial == -2 | differenceBetweenAbsoluteAndInitial ==2){
         directionToLookIn = IRobot.BEHIND;
      } else if (differenceBetweenAbsoluteAndInitial == -3 | differenceBetweenAbsoluteAndInitial == 1){
         directionToLookIn = IRobot.RIGHT;
      } else {
         directionToLookIn = IRobot.AHEAD;
      }

      objectAhead = robot.look(directionToLookIn);
      
      robot.setHeading(initialHeading);


      if (objectAhead == IRobot.WALL) {
         //return IRobot.WALL or objectAhead- better approach
         System.out.println("IRobot.WALL");
      } else if (objectAhead == IRobot.PASSAGE){
         //return IRobot.PASSAGE or objectAhead
         System.out.println("IRobot.PASSAGE");
      } else {
         //return IRobot.BEENBEFORE or objectAhead
         System.out.println("IRobot.BEENBEFORE");
      }
      return objectAhead;
   }


   private byte isTargetNorth(IRobot robot) {
      byte northResult;
      
      if (robot.getTargetLocation().y < robot.getLocation().y) { //if the y coordinate of the target's location is less than robot's location, the target is to the north of robot. 
         northResult = 1; //This works because the maze's leftmost corner is (0,0) and bottom right (n,n) 
         System.out.println("Target is North");
      }else if (robot.getTargetLocation().y == robot.getLocation().y) {// if their y coordinate is the same, they are in the same latitude.
         northResult = 0;
         System.out.println("Target is on the same vertical level.");
      }else {
         northResult = -1; // otherwise the target must be to the south of the robot.
         System.out.println("Target is South");
      }return northResult;

   }

   private byte isTargetEast(IRobot robot) {
      byte eastResult;

      if (robot.getTargetLocation().x > robot.getLocation().x) { //if the x coordinate of the target's location is greater than robot's location, the target is to the east of robot. 
         eastResult = 1;
         System.out.println("Target is East");
      }else if (robot.getTargetLocation().x < robot.getLocation().x) { //if the x coordinate of the target's location is less than robot's location, the target is to the west of robot. 
         eastResult = -1;
         System.out.println("Target is West");
      }else {
         eastResult = 0; // if their x coordinate is the same, they are in the same longitude. 
         System.out.println("Target is on the Same longitude");
      }return eastResult;
   }

   private int headingController(IRobot robot){
      List <Integer> priorities = new ArrayList<>();
      List <Integer> possibilities = new ArrayList<>(Arrays.asList(IRobot.NORTH, IRobot.SOUTH, IRobot.EAST, IRobot.WEST));
      List <Integer> priorityWithoutWalls = new ArrayList<>();
      List <Integer> possibilityWithoutWalls = new ArrayList <>();

      //create priorities
      int targetIsNorth = this.isTargetNorth(robot);
      int targetIsEast = this.isTargetEast(robot);

      if (targetIsNorth == 1) {
         priorities.add(IRobot.NORTH);
      } else if (targetIsNorth == -1) {
         priorities.add(IRobot.SOUTH);
      }

      if (targetIsEast == 1) {
         priorities.add(IRobot.EAST);
      } else if (targetIsEast == -1) {
         priorities.add(IRobot.WEST);
      }


      //remove walls from priority

      for (int i=0; i<priorities.size(); i++) {
         if (lookHeading(robot, priorities.get(i)) != IRobot.WALL) {
            priorityWithoutWalls.add(priorities.get(i));
         }
      }

      //remove walls from possibility

      for (int j=0; j<possibilities.size(); j++) {
         if (lookHeading(robot, possibilities.get(j)) != IRobot.WALL) {
            possibilityWithoutWalls.add(possibilities.get(j));
         }
      }

      //if there are no priorites without walls then just pick a random direction. priorites is empty set.
      int directionChosen;
      if (priorityWithoutWalls.isEmpty()) {
         int randomIndex = (int)(Math.random() * possibilityWithoutWalls.size());
         directionChosen= possibilityWithoutWalls.get(randomIndex);
      } else{
            //if there are priorities without walls, pick a direction from the priorities without walls. 
         int randomIndex = (int)(Math.random() * priorityWithoutWalls.size());
         directionChosen= priorityWithoutWalls.get(randomIndex);
      }

      return directionChosen;
   }


   public void controlRobot(IRobot robot) {
      int direction; 
      int randno;

      do {
         randno = (int) Math.round(Math.random()*3);

         if (randno == 0){
            direction = IRobot.LEFT;
         }else if (randno == 1){
            direction = IRobot.RIGHT;
         }else if (randno == 2){
            direction = IRobot.BEHIND;
         }else {
            direction = IRobot.AHEAD;
         }
      }while (robot.look(direction)==IRobot.WALL);

      robot.face(direction);  /* Face the direction */  

      isTargetNorth(robot);
      isTargetEast(robot);
      lookHeading(robot, IRobot.NORTH);
      headingController(robot);
      int heading = headingController(robot);
      ControlTest.test(heading, robot);
      robot.setHeading(heading);

  }

   public void reset() {
     ControlTest.printResults();
   }
}