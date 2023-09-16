//Preamble//
/*When there are no passages, the first time it treats it as deadend and the next time it backtracks, because it could have
encountered the same loop from a different heading. Previous exercises were incapable of solving loopy mazes because if there
 were no passages, it reversed direction, but in this exercise where 1 loop sets the heading to backtrack, the adjacent loop could 
 also set the heading to backtrack, which means the robot will be switching headings all the time. In this implementation search junction
is called when there are 3 or more beenbefores, which means fully explored. If only 1 beenbefore, it is a mew junction and is recorded.
Count method returns the no. of times the specific junction has been encountered. If it returns 1 and there is a passage left with 3 beenbefores,
it indicates a crossroads, it should go behind because entering the passage would cause it to loop. If there are no passages and the count returns 
1, it should be treated as deadend. If it is second time at the junction with no passages left, it should backtrack. I also included that it
chooses randomly from beenbefores, to ensure no test cases are left. Count is incremented each time search junction is called for the specific x y coordinates.
Junction recorder also has count attribute because for each junction it needs to be known, how many times it has been encountered. I also attempted to implement 
Tremaux, in which the first time it enters a junction it adds 1 to the heading in which it entered from for the specifis junction, and if the heading returns 2,
it should backtrack, effectively reducing loopy maze to a prim maze, however I failed in doing so. */

import uk.ac.warwick.dcs.maze.logic.IRobot;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Ex3
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
        int directionToFace = IRobot.BEHIND;
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

    //if there are 3 or more beenbeofres, it is a explored junction/crossroad and must already exist in the junctions arraylist.
    //it then searches the heading it entered from previously and increments the count within the search junction function to indicate this junction
    //has been visited one more time. If there is a passage and 3 beenbefores it is at a crossroad-> and has done a loop since count returns 1.- treat
    //as deadend. The count only returns 1 when there are 3 or 4 beenbefores, meaning fully explored, so should go back. If count returns 2, and 
    //there are no passages, it should backtrack. Sometimes the robot glitches at loops so I use beenbefores.random to ensure no case fails.
    private int junction(IRobot robot){
        List <Integer> passages = new ArrayList<>();
        List <Integer> beenbefores = new ArrayList<>();
        List <Integer> possibilties = new ArrayList<>(Arrays.asList(IRobot.AHEAD, IRobot.LEFT, IRobot.RIGHT, IRobot.BEHIND)); 
        int xCoordinate= robot.getLocation().x;
        int yCoordinate = robot.getLocation().y;
        int searchOutput= 0;
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
            robotData.recordJunction(robot);// after recording the junction,get its count
            robotData.countMethod(robot, xCoordinate, yCoordinate);
        }else if (n>=3){
            //first encounters--> then increments the count in the searchJunction function because if a junction is being searched for here, it means we are encountering it again. 
            searchOutput = robotData.searchJunction(robot, xCoordinate, yCoordinate);
        }


        if (passages.size() >= 1 ){
            if(n==3 && robotData.countMethod(robot, xCoordinate, yCoordinate)==1){
    //If there is a passage and 3 beenbefores it is at a crossroad-> and has done a loop since count returns 1.- treat as deadend
                directionToFace= IRobot.BEHIND;
            } else {
                //pick random passage
                int randomIndex = (int)(Math.random() * passages.size());
                directionToFace = passages.get(randomIndex);
            }
        }else if (passages.size()==0 && robotData.countMethod(robot, xCoordinate, yCoordinate)==1){
            //explored fully- treat as deadend
            directionToFace=IRobot.BEHIND;
        }else if (passages.size()==0 && robotData.countMethod(robot, xCoordinate, yCoordinate)==2){
            //2nd time at this junction- so backtrack
            robot.setHeading(searchOutput);
            directionToFace= IRobot.BEHIND;
        } else{
            //too many times that it has encountered a junction. 
            explorerMode = 0;
            int randomIndex = (int)(Math.random() * beenbefores.size());
            directionToFace = beenbefores.get(randomIndex);
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
    public List <JunctionRecorder> junctions = new ArrayList<>();
    private JunctionRecorder junctionRecord;


    public RobotData() {
        //robotdata stores a array of JunctionRecorders.
        junctionCounter = 0;
    }


    public void resetJunctionCounter() {
        junctionCounter = 0;
    }

    //adds junctions to the arraylist. In this exercise, I store x,y along with headings to identify if that specific junction has been rencountered. 
    public void recordJunction(IRobot robot) {
        JunctionRecorder junction = new JunctionRecorder(robot.getLocation().x, robot.getLocation().y, robot.getHeading());
        junctions.add(junction);
        junctionCounter+=1;
        this.printJunction(robot);
    }

    public int junctionsCount(){
        return junctions.size();
    }

    public void printJunction(IRobot robot) {
        JunctionRecorder store = junctions.get(junctionCounter-1);
        System.out.println("Junction " + junctionCounter + "(x= " + store.getXCoordinate() + " y= " + store.getYCoordinate() + " ) heading " + direction(store.getHeading()));
    }


    //Loops through, using a for loop, all the junctions that have been encountered so far.
    //returns the heading from which the robot previously entered the junction.
    //also increments the count which tells how many times specific junction has been encountered. 
    public int searchJunction(IRobot robot, int xCoordinate, int yCoordinate){
        for (int i=0; i<junctionsCount(); i++){
            JunctionRecorder nextElement = junctions.get(i);
            if (xCoordinate == nextElement.getXCoordinate() && yCoordinate == nextElement.getYCoordinate()){
                nextElement.count++;
                return nextElement.getHeading();
            }
        }
        return -1; //-1 for not found.
    }

    //returns the count of how many times the robot has been at the specific. It identifies that by matching x and y coordinates, which it takes as parameters from junctions function.
    public int countMethod(IRobot robot, int xCoordinate, int yCoordinate){
        int count = 0;
        for (int i=0; i<junctionsCount(); i++){
            JunctionRecorder nextElement = junctions.get(i);
            if (xCoordinate == nextElement.getXCoordinate() && yCoordinate == nextElement.getYCoordinate()){
                count = nextElement.count;
            }
        }
        return count;
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

    public int xCoordinate, yCoordinate, heading, count;//declared the properties of the class.
    private RobotData robotData; //data store for junctions

    //getters needed for printing individual elements of a junction and matching those to the current junction in searchjunction and count method.
    public int getXCoordinate(){
        return this.xCoordinate;
    }

    public int getYCoordinate(){
        return this.yCoordinate;
    }
    public int getHeading(){
        return this.heading;
    }

    //constructor method, which now also has count as attribute. 
    public JunctionRecorder (int xCoordinate, int yCoordinate, int heading){
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.heading = heading;
        this.count = count;
    }

}
