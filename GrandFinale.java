//Preamble//
/* I recorded x, y and heading of each step the robot takes in a step recorder stack. Each step is an object of steps class.
If at a deadend, it should not record the step since it goes behind. If there are passages in a corridor or a junction, it pushes 
the step to the step Recorder stack as defined in Robotdata class. If there are no passages, it pops the topmost step since the target 
would not be there, so it doesn't need to store it. If it is a new junction, that is when beenbefores =1, the heading is pushed in junctions stack
in robot data class. Also, when there are no passages, in a junction, the top most junction is also popped. 
If it is a new maze and is at the start, it clears robot data and is set to be exploring. Heading is set to the output from the main method. 
In this exercise, I use headings instead of relative directions as the main output. If robot is in its successive runs, secondRun method 
is given control. This gets the heading for the relevant step. i is incremented after each step, and it is used to retrieve 
the heading at that position in step recorder stack. However, it doesn't implement fully and keeps popping off the step recorder 
stack and returns empty stack exception. It can not solve loop mazes.*/

import uk.ac.warwick.dcs.maze.logic.IRobot;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class GrandFinale
{
    List <Integer> allDirections = new ArrayList<>(Arrays.asList(IRobot.AHEAD, IRobot.BEHIND, IRobot.LEFT, IRobot.RIGHT)); 

    private int pollRun = 0; 
    private RobotData robotData;
    private int explorerMode; // 1 = explore, 0 = backtrack. 
    public JunctionRecorder junctionRecord; 
    private int i=0; //incremented after each step of the successive runs, so it knows which step to retrieve.


    //If it is a new maze and is at the start, it should clear robot data and be exploring.  
    //heading is set to the output from the main method.
    //If it is in its successive runs, secondRun method is given control.
    public void controlRobot(IRobot robot) {
        int direction=0;

        if ((robot.getRuns() == 0) && (pollRun == 0)) {
            robotData = new RobotData();
            explorerMode = 1;
        }

        if ((robot.getRuns()==0)){
            robot.setHeading(mainMethod(robot));
            robot.face(IRobot.AHEAD);
        } else {
            secondRun(robot);
        }

        pollRun++;
    }

    //this gets the heading for the relevant step. i is incremented after each step, and it is used to retrieve the heading at that position in step recorder stack.
    public void secondRun(IRobot robot){
        Steps stepAt = robotData.stepRecorder.get(i);
        System.out.println(stepAt);
        int stepAtHeading = stepAt.getHeading();
        if ((robotData.stepRecorder.size() - 1) > i){
            i++;
        }

        robot.setHeading(stepAtHeading);
        robot.face(IRobot.AHEAD);

    }


    //passes control to the relevant method, depending on number of non-wall exits. 
    public int mainMethod(IRobot robot){
        ArrayList<Integer> notWalls = notWallsForAbsoluteDirection(robot);
        int numberOfNotWalls = notWalls.size();
        int directionToFace= 0;

        if (numberOfNotWalls ==1){
            directionToFace = deadEnd(robot, notWalls);
        } else if (numberOfNotWalls ==2){
            directionToFace= corridor(robot, notWalls);
        } else {
            directionToFace= junction(robot, notWalls);
        }
        return directionToFace;
    }


    //Robot has to go behind at a deadend, except when it is starting. Notwalls arraylist is passed as a parameter and since at deadends, 
    //there is only 1 non-wall, it gets the element at the first position in the notwalls which was the output of notwallsforabsolutedirections method.
    private int deadEnd(IRobot robot, ArrayList<Integer> notWalls){
        return notWalls.get(0);
    }


    //add to the steps stack where there are passages. if no passages, pop from the steps stack.
    private int corridor(IRobot robot, ArrayList<Integer> notWalls){
        ArrayList<Integer> passages = passageExitsForAbsoluteDirection(robot);

        int randomIndex = (int)(Math.random() * passages.size());
        int directionToFace = passages.get(randomIndex);
        //choose randomly from passages

        if (passages.size()>0){
            robotData.addStepsToTheStack( robot); //record step
            robotData.printSteps(robot);
        }else{
            robotData.popStep();
            robotData.printSteps(robot);
        }

        return directionToFace;
    }

    //add to the steps stack where there are passages. if no passages, pop from the steps stack.
    private int junction(IRobot robot, ArrayList<Integer>notWalls){
        int directionToFace=0;
        ArrayList<Integer> passages = passageExitsForAbsoluteDirection(robot);
        int heading = robot.getHeading();
        ArrayList<Integer> beenbefores = beenbeforeExitsForAbsoluteDirection(robot);
        int numberOfBeenbefores = beenbefores.size();

        if (numberOfBeenbefores ==1){
            robotData.recordJunction(robot);//new junction so push
            robotData.addStepsToTheStack( robot);
            robotData.printSteps(robot);//prints after each step is added.
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
            robot.face(IRobot.BEHIND);
            directionToFace=robot.getHeading();
            robotData.popStep();//removes the step from the stack.
            robotData.printSteps(robot);
        }
        return directionToFace;
    }


    public void reset(){
        robotData.reset();
        explorerMode = 1;
        pollRun=0;
        i=0;
    }

    private ArrayList<Integer> beenbeforeExitsForAbsoluteDirection(IRobot robot) {
        ArrayList<Integer> beenbefores = new ArrayList<Integer>();

        for (int i = 0; i<4; i++){
            if (lookHeading(robot, IRobot.NORTH + i ) == IRobot.BEENBEFORE){
                beenbefores.add(IRobot.NORTH+i);
            }
        }
        return beenbefores;
    }

    //used for checking which absolute directions are walls and non-walls.
    private int lookHeading(IRobot robot, int direction) {
        int heading = robot.getHeading();
        int relative = ((direction - heading) % 4 + 4) % 4;
        int absolute = IRobot.AHEAD + relative;
        return robot.look(absolute);
    }

    //returns the arraylist of non-wall absolute direction.
    public ArrayList<Integer> notWallsForAbsoluteDirection(IRobot robot){
        ArrayList<Integer> notWalls = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++) {
            if (lookHeading(robot, IRobot.NORTH + i) != IRobot.WALL) {
                notWalls.add(IRobot.NORTH+i);
            }
        }
        return notWalls;
    }

    //returns the arraylist of passages in absolute direction.
    private ArrayList<Integer> passageExitsForAbsoluteDirection(IRobot robot) {
        ArrayList<Integer> passages = new ArrayList<Integer>();

        for (int i = 0; i<4; i++){
            if (lookHeading(robot, IRobot.NORTH + i ) == IRobot.PASSAGE){
                passages.add(IRobot.NORTH+i);
            }
        }
        return passages;
    }

}

class RobotData
{
    public Stack <JunctionRecorder> junctions = new Stack<>();
    private static int junctionCounter; 
    public Stack <Steps> stepRecorder= new Stack<>();//data store for steps which should be followed in succesive runs.
    public int stepCounter;


    public RobotData() {
        junctionCounter = 0;
        stepCounter=0;
    }

    //prints the topmost step on the stack
    public void printSteps(IRobot robot) {
        Steps store = peekSteps();
        System.out.println(store.getXCoordinate() + store.getYCoordinate() + direction(store.getHeading()));
    } 


    public Steps peekSteps(){
        return this.stepRecorder.peek();
    }

    public void reset() {
        this.junctions = new Stack<>(); 
    }

    public void resetJunctionCounter() {
        junctionCounter = 0;
    }

    //records heading of each junction encountered 
    public void recordJunction(IRobot robot) {
        JunctionRecorder newJunction = new JunctionRecorder(robot.getHeading());
        junctions.push(newJunction);
        this.printJunction(robot);
    }
        //records x,y,heading of each step  
    public void addStepsToTheStack(IRobot robot){
        Steps newStep = new Steps(robot.getLocation().x, robot.getLocation().y, robot.getHeading());
        stepRecorder.push(newStep);
        printSteps(robot);
    }

    public int junctionsCount(){
        return junctions.size();
    }

    //prints the top most element in the junctions stack
    public void printJunction(IRobot robot) {
        JunctionRecorder store = peekJunction();
        System.out.println("heading " + direction(store.getHeading()));
    }

    public JunctionRecorder peekJunction(){
        return this.junctions.peek();
    }

    public JunctionRecorder popJunction(){
        return this.junctions.pop();
    }

    public Steps popStep(){
        return this.stepRecorder.pop();
    }

    //for easier readability of heading.
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
    private int heading;


    public int getHeading(){
        return this.heading;
    }
    //constructor
    public JunctionRecorder (int heading){
        this.heading = heading;
    }
}
    

class Steps
{

    private int xCoordinate, yCoordinate, heading;//declared the properties of the class.


    //getters needed for printing individual elements of each step.
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
    public Steps (int xCoordinate, int yCoordinate, int heading){
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.heading = heading;
    }
}
