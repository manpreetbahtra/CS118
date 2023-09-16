//Preamble//
/* Subsidiary methods (deadend, corridor, junctions) output the direction robot needs to move in. These are called by explorer or backtrack method. 
If the robot is at deadend, backtrack mode is on, because the only option for the robot at deadends is to go backwards. However, if the robot
is starting rather than going behind, robot.look method is used to check for the only non-wall direction and it is returned, which the robot then faces.
Two possible scenarios at a corridor are corridor itself and a corner. Behind cant be a wall, since it came from there. So left and right are checked 
and ahead is returned, since it can't go back on itself. At corners, ahead will be blocked off by the wall. Either left or right will be non-wall.
If right is a wall, left must not be wall, because it has to have 2 non-wall exits. Hence, it chooses left (or vice-versa). Junctions and crossroads 
have similar behaviour, hence are combined into 1. If non-wall exits are greater than 2, junctions method is called. In the arraylists passages and 
beenbefores are added. Using the size of the beenbefore list, if it is equal to 1, then it is a new junction, which is then recorded. Its current 
x position, y position and heading is stored inside a junction recorder object type. Junction recorder class has attributes x location, y location and heading
which allows for storing 3 pieces of information together. These junction recorder objects are then stored in the junctions arraylist in robot data class.
In junctions, if there are 1 or more passages, it chooses randomly from those passages. If there are no passages, the robot must have been at the current 
junction before, so the backtrack mode is active. It retrieves the heading from which it previously entered the junction using the search junction function in robot data.
Moreover, if there are no passages, it chooses randomly from beenbefores. Thorough testing of storing data is done so that same junction is not repeatedly stored. 
Explorer control and backtrack control methods could have been implemented better and have repeated code. Instead, I could have implemented the call to the deadend,corridor,
junction method inside controlRobot and if a deadends, do explorermode =0 otherwise 1. It already sets inside junction method that if no passages explorer=0. Worst case 
analysis is that it searches most of the squares. The time complexity is O(V+E) where v is the number of vertices and e is the number of edges. Hence, it has linear time complexity.  */


import uk.ac.warwick.dcs.maze.logic.IRobot;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Ex1
{
    List <Integer> allDirections = new ArrayList<>(Arrays.asList(IRobot.AHEAD, IRobot.BEHIND, IRobot.LEFT, IRobot.RIGHT)); 

    private int pollRun = 0; 
    private RobotData robotData; 
    private int explorerMode; // 1 = explore, 0 = backtrack. 
    public JunctionRecorder junctionRecord; 

    //main method controlRobot which figures out which mode-exploring/backtracking the robot is in. 
    public void controlRobot(IRobot robot) {
        //On the first move of the first run of a new maze
        if ((robot.getRuns() == 0) && (pollRun == 0)) {
            robotData = new RobotData(); //clears the robot data stored previously, since it is a new maze, previous data is not useful.
            explorerMode = 1;
        }

        if (explorerMode == 1){
            exploreControl(robot);
        } else{
            backtrackControl(robot);
        }
        //according to the value of the variable, the relevant method is given control. 
    }

    //regardless of the method called, at a deadend, the robot is set to backtrack since it has nowhere else to go. 
    //depending on the number of non wall exits, the relevant method is given control. 
    private void exploreControl(IRobot robot){
        int exits = nonWallExits(robot);
        int direction;


        if (exits < 2) {
            explorerMode = 0;
            direction = deadEnd(robot); 
        } else if (exits == 2 ){
            direction = corridor(robot);
        }else {
            direction = junction(robot);
        }

        robot.face(direction);
        pollRun++; 
    }

    //after each step poll run is incremented. 
    private void backtrackControl(IRobot robot){
        int exits = nonWallExits(robot);
        int direction;


        if (exits < 2) {
            explorerMode = 0;
            direction = deadEnd(robot); 
        } else if (exits == 2 ){
            direction = corridor(robot);
        }else {
            direction = junction(robot);
        }
        
        robot.face(direction);
        pollRun++; 
    }


    //Robot has to go behind at a deadend, except when it is starting. Using the robot's starting position a boolean is created.
    //If the boolean isStarting returns true, it checks which direction is not a wall, otherwise goes behind.
    private int deadEnd(IRobot robot){
        boolean isStarting = robot.getLocation().x == 1 && robot.getLocation().y == 1;
        if (isStarting) {
            explorerMode = 1;
            for (int direction : this.allDirections) {
                if (robot.look(direction) != IRobot.WALL){
                    return direction;
                }
            }
        }
        return IRobot.BEHIND;
    }

    
    private int corridor(IRobot robot){
            // left, right blocked -> go ahead
        int directionToFace = IRobot.AHEAD;
        if (robot.look(IRobot.RIGHT) == IRobot.WALL && robot.look(IRobot.LEFT) == IRobot.WALL){
            return IRobot.AHEAD;
        }

            // case at corner
        if (robot.look(IRobot.AHEAD) == IRobot.WALL){
            if (robot.look(IRobot.RIGHT) == IRobot.WALL) {
                directionToFace = IRobot.LEFT;
            } else {
                directionToFace = IRobot.RIGHT;
            }
        }
        return directionToFace;
    }


    //adds the directions which are passages or beenbefores in their corresponding arraylists.
    //if there is only 1 beenbefore, it is a new junction, which is then recorded. 
    //if there are passages, it randomly chooses between all the passages, otherwise reverses the way which it entered from last time. 
    private int junction(IRobot robot){
        List <Integer> passages = new ArrayList<>();
        List <Integer> beenbefores = new ArrayList<>();
        List <Integer> possibilties = new ArrayList<>(Arrays.asList(IRobot.AHEAD, IRobot.LEFT, IRobot.RIGHT, IRobot.BEHIND)); 
        int directionToFace = IRobot.AHEAD;
        int xCoordinate=robot.getLocation().x;
        int yCoordinate=robot.getLocation().y;

        for (int relativeDirection : possibilties){
            if (robot.look(relativeDirection) == IRobot.PASSAGE){
                passages.add(relativeDirection);
            }else if (robot.look(relativeDirection) == IRobot.BEENBEFORE){
                beenbefores.add(relativeDirection);
            }
        }

        int n = beenbefores.size();
        if (n ==1){
            robotData.recordJunction(robot);
        }

        if (passages.size() >= 1 ){
            //randomly choose between all the passages.
            explorerMode = 1;
            int randomIndex = (int)(Math.random() * passages.size());
            directionToFace = passages.get(randomIndex);
        }else if (passages.size() ==0) {
            explorerMode = 0;
            int arrivalHeading= robotData.searchJunction(robot, xCoordinate, yCoordinate);
            robot.setHeading(arrivalHeading);
            directionToFace = IRobot.BEHIND;
        }
        return directionToFace;
    }

    //this method returns the number of non wall exits relative to the robot's position. 
    //It checks using a for loop 4 relative directions and returns how many were non-wall.
    private int nonWallExits(IRobot robot) {
        int withoutWallExits = 0;

        for (int i = 0; i<4; i++){
            if (robot.look(IRobot.AHEAD+i) != IRobot.WALL){
                withoutWallExits +=1;
                }
        }
        return withoutWallExits;
    }


    public void reset(){
        robotData.resetJunctionCounter();
        explorerMode = 1;
    }


}

class RobotData
{
    private static int junctionCounter; 
    public List <JunctionRecorder> junctions = new ArrayList<>();//Stores all the instances of the junctionRecorder class.
    private JunctionRecorder junctionRecord; //data store for junctions


    public RobotData() {
        junctionCounter = 0;
    }


    public void resetJunctionCounter() {
        junctionCounter = 0;
    }

    //adds junctions to the arraylist 
    public void recordJunction(IRobot robot) {
        JunctionRecorder junction = new JunctionRecorder(robot.getLocation().x, robot.getLocation().y, robot.getHeading());
        junctions.add(junction);
        junctionCounter+=1;
        this.printJunction(robot);
    }

    public int junctionsCount(){
        return junctions.size();
    }

    //prints the latest junction added.
    public void printJunction(IRobot robot) {
        JunctionRecorder store = junctions.get(junctionCounter-1);
        System.out.println("Junction " + junctionCounter + "(x= " + store.getXCoordinate() + " y= " + store.getYCoordinate() + " ) heading " + direction(store.getHeading()));
    }

    //Loops through, using a for loop, all the junctions that have been encountered so far.
    //returns the heading from which the robot previously entered the junction. --> will only be the case when 0 passage.
    public int searchJunction(IRobot robot, int xCoordinate, int yCoordinate){
        for (int i=0; i<junctionsCount(); i++){
            JunctionRecorder nextElement = junctions.get(i);
            if (xCoordinate == nextElement.getXCoordinate() && yCoordinate == nextElement.getYCoordinate()){
                return nextElement.getHeading();
            }
        }
        return -1; //-1 for not found.
    }


    //for readability of headings. 
    public String direction(int absoluteDirection) {
        switch (absoluteDirection) {
            case IRobot.NORTH:
                return "NORTH";
            case IRobot.SOUTH:
                return "SOUTH";
            case IRobot.WEST:
                return "WEST";
            case IRobot.EAST:
                return "EAST";
        }
        return "";
    }


}

class JunctionRecorder
{

    private int xCoordinate, yCoordinate, heading;//declared the properties of the class.
    private RobotData robotData; //data store for junctions


    //getters needed for printing individual elements of each junction.
    public int getXCoordinate(){
        return this.xCoordinate;
    }

    public int getYCoordinate(){
        return this.yCoordinate;
    }
    public int getHeading(){
        return this.heading;
    }

    //constructor method
    public JunctionRecorder (int xCoordinate, int yCoordinate, int heading){
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.heading = heading;
    }
}
