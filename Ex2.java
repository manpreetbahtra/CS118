//Preamble//
/*It saves space by only storing headings. Improvements- rather than storing junctions as junction recorder objects, I could have saved them as integers.
This would have removed the need for junction recorder class. Tested if each junction is adding the correct heading through the use of print 
statements. */

import uk.ac.warwick.dcs.maze.logic.IRobot;

import java.util.List;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Arrays;

public class Ex2
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
    //if there are passages, it randomly chooses between all the passages.
    //if no passages, it pops the top heading and reverses it.  
    private int junction(IRobot robot){
        List <Integer> passages = new ArrayList<>();
        List <Integer> beenbefores = new ArrayList<>();
        List <Integer> possibilties = new ArrayList<>(Arrays.asList(IRobot.AHEAD, IRobot.LEFT, IRobot.RIGHT, IRobot.BEHIND)); 
        int directionToFace = IRobot.AHEAD;

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
            //calculate opposite of the direction it came from.
            JunctionRecorder junc = robotData.popJunction();
            int arrivalHeading = junc.getHeading();
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
        robotData.reset();
        explorerMode = 1;
    }


}

class RobotData
{   
    //implemented a stack to store heading of each junction. 
    private Stack <JunctionRecorder> junctions = new Stack<>();

    public void reset() {
        this.junctions = new Stack<>(); 
    }

    //to optimise storage, only headings are stored. 
    public void recordJunction(IRobot robot) {
        JunctionRecorder newJunction = new JunctionRecorder(robot.getHeading());
        junctions.push(newJunction);
        this.printJunction(robot);
    }

    public int junctionsCount(){
        return junctions.size();
    }

    public void printJunction(IRobot robot) {
        JunctionRecorder store = peekJunction();
        System.out.println("heading " + direction(store.getHeading()));
    }

    //this returns the topmost elements in the stack. 
    public JunctionRecorder peekJunction(){
        return this.junctions.peek();
    }
    //this returns the topmost elements in the stack and removes it. 
    public JunctionRecorder popJunction(){
        return this.junctions.pop();
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

//Junction recorder class now only has heading attribute to save storage.
class JunctionRecorder
{

    private int heading;//declared the properties of the class.
    

    //getter needed for printing heading of each junction.
    public int getHeading(){
        return this.heading;
    }

    //constructor method
    public JunctionRecorder (int heading){
        this.heading = heading;
    }
}
