/*
 * Author: Connor Allred
 * Date: February 12th, 2023
 * 
 * Purpose: Design three search algortihms to be utilized in searching for the solution of the 8 pussle problem
 * TODO: Finish implementation of astar2 algorithm using different heuristic than astar1
 *          - Manhatten distance function
 *          - Unable to complete due to time constraints
 */

package Program1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Main {

    // - - - - - - - - - - - - - - - - - Main driver of program - - - - - - - - - - - - - - - - -// 

    public static void main(String[] args) throws IOException{
        Node root = EstablishBoard(args[1]);
        SearchAlgo search = new SearchAlgo();
        root.setRoot(root); // Set root as itself for future nodes' usage
        root.setMaxDepth(10);   // Set max depth for DFS, IDS overrides this in a for loop

        // Desired search algorithm to be used picked from command line with created intitial state/board
        switch(args[0]){
            case "dfs":
                search.DFS(root);
                DisplayResults(search, true);
                break;
            case "ids":
                search.IDS(root);
                DisplayResults(search, true);
                break;
            case "astar1":
                search.Astar1(root, null);
                DisplayResults(search, false);
                break;
            case "astar2":
                search.Astar2();
                DisplayResults(search, false);
                break;
            default:
                System.out.println("Error! Invalid search method inputted");
        }
    }

    // - - - - - - - - - - - - - - - - - Create intial board state - - - - - - - - - - - - - - - - -// 

    public static Node EstablishBoard(String file) throws IOException{
        Scanner input = new Scanner(new File(file));
        int[][] temp = new int[3][3], temp2 = new int[3][3];
        int BlankX = 0, BlankY = 0;

        // Read in intial state of board
        for(int i = 0; i<3; i++){
            for(int j = 0; j<3; j++){
                temp[i][j] = input.nextInt();
                if(temp[i][j] == 0){
                    BlankX = j;
                    BlankY = i;
                }
            }
        }

        // Read in goal state of board
        for(int i = 0; i<3; i++){
            for(int j = 0; j<3; j++){
                temp2[i][j] = input.nextInt();
            }
        }
        
        // Create root node with starting board state before reading goal state
        Node initial = new Node(0, temp, temp2, BlankX, BlankY);
        
        input.close();

        return initial;
    }
    
    // - - - - - - - - - - - - - - - - - Display board state - - - - - - - - - - - - - - - - -// 
    public static void DisplayResults(SearchAlgo search, boolean type){
        int moves;
        // Display final results of search
        if(search.Found){
            List<Node> temp = search.getGoalPath();
            moves = temp.size()-1;
            if(type){
                // Tracks number of moves needed to get to goal state
                for(int i = moves; i >=0; i--){
                    DisplayState(temp.get(i));
                }
            }else{
                // Tracks number of moves needed to get to goal state
                for(int i = 0; i <=moves; i++){
                    DisplayState(temp.get(i));
                }
            }
            System.out.println("Number of moves: " + moves);
            System.out.println("Number of states enqueued: " + search.getEnqueued());
        }else{
            System.out.println("Goal state not achieved within set depth limit!");
        }
    }
    public static void DisplayState(Node Board){

        for(int[] row : Board.getCurrentState()){
            for(int value : row){
                if(value == 0){
                    System.out.print("  ");
                }else{
                    System.out.print(value + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}

// - - - - - - - - - - - - - - - - - - - - - - - - Node Class - - - - - - - - - - - - - - - - - - - - - - - -// 

class Node {
    // BlankX and BlankY indicate position of empty spot in respective board state
    private int BlankX, BlankY, Depth, MaxDepth; // Pre-set max depth for traversing search tree
    private int[][] CurrentState = new int[3][3], GoalState = new int[3][3];
    private boolean[] Visited = new boolean[4];  // 0 - left neighbor, 1 - Top neighbor, 2 - Right neighbor, 3 - Bottom neighbor
    private Node Root;

    // Attributes for A* algorithm usage
    private int WrongTiles;
    Node[] PossibleMoves = new Node[4];

    // - - - - - - - - - - - - - - - - - Node Constructor - - - - - - - - - - - - - - - - -// 
    Node(int Depth, int[][] CurrentState, int[][] GoalState, int BlankX, int BlankY){
        this.Depth = Depth;
        this.CurrentState = CurrentState;
        this.GoalState = GoalState;
        this.BlankX = BlankX;
        this.BlankY = BlankY;
        Arrays.fill(Visited, false);
    }

    Node(int Depth, int BlankX, int BlankY, int MaxDepth){
        this.Depth = Depth;
        this.BlankX = BlankX;
        this.BlankY = BlankY;
        this.MaxDepth = MaxDepth;
        Arrays.fill(Visited, false);
    }

    Node(){}
    
    // - - - - - - - - - - - - - - - - - Node Set Functions - - - - - - - - - - - - - - - - -// 
    public void setGoalState(int[][] GoalState){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                this.GoalState[i][j] = GoalState[i][j];
            }
        }
    }
    public void setCurrentState(int[][] CurrentState){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                this.CurrentState[i][j] = CurrentState[i][j];
            }
        }    
    }
    public void setBlankY(int BlankY){
        this.BlankY = BlankY;
    }
    public void setBlankX(int BlankX){
        this.BlankX = BlankX;
    }
    public void setDepth(int Depth){
        this.Depth = Depth;
    }
    public void setMaxDepth(int MaxDepth){
        this.MaxDepth = MaxDepth;
    }
    public void setRoot(Node Root){
        this.Root = Root;
    }
    public void setVisited(int index){
        Visited[index] = !Visited[index];   // Flips given current boolean value of the specific index
    }
    public void resetVisited(){
        Arrays.fill(Visited, false);
    }
    public void setWrongTiles(int WrongTiles){
        this.WrongTiles = WrongTiles;
    }


    // - - - - - - - - - - - - - - - - - Node Get Functions - - - - - - - - - - - - - - - - -// 
    public int[][] getCurrentState(){
        return CurrentState;
    }
    public int[][] getGoalState(){
        return GoalState;
    }
    public int getDepth(){
        return Depth;
    }
    public int getMaxDepth(){
        return MaxDepth;
    }
    public boolean[] getVisited(){
        return Visited;
    }
    public Node getRoot(){
        return Root;
    }
    public int getWrongTiles(){
        return WrongTiles;
    }
    public int getCost(){
        return WrongTiles+Depth;
    }
    public Node getLeftNeighbor(Node Current){
        // Check to see if we will be at max depth now (null returned as we do not care for deeper nodes)
        if(Current.Depth >= MaxDepth){
            return null;
        }
        // Check if we are located on the edge of given board
        if(BlankX-1 < 0){
            return null;
        }
        // Perform respective swap of empty space and top neighbor if possible
        Node newNode = new Node(Current.Depth+1, BlankX, BlankY, getRoot().getMaxDepth());
        newNode.setRoot(Current.getRoot());
        newNode.setCurrentState(Current.getCurrentState());
        newNode.setGoalState(Current.getGoalState());        

        int temp = newNode.getCurrentState()[BlankY][BlankX-1]; // Saving above value to be swapped with blank space right it
        newNode.getCurrentState()[BlankY][BlankX-1] = Current.getCurrentState()[BlankY][BlankX];    // Copy blank space to index left of it
        newNode.getCurrentState()[BlankY][BlankX] = temp;   // Replace blank space with stored value
        newNode.BlankX--;   // Adjust stored index of blank space for respective board state accordingly
        
        newNode.setVisited(2); // Reset index of previous index of blank space to prevent loop
        return newNode;
    }
    public Node getTopNeighbor(Node Current){
        // Check to see if we will be at max depth now (null returned as we do not care for deeper nodes)
        if(Current.Depth >= MaxDepth){
            return null;
        }
        // Check if we are located on the edge of given board
        if(BlankY-1 < 0){
            return null;
        }
        // Perform respective swap of empty space and top neighbor if possible 
        Node newNode = new Node(Current.Depth+1, BlankX, BlankY, getRoot().getMaxDepth());
        newNode.setCurrentState(Current.getCurrentState());
        newNode.setGoalState(Current.getGoalState());
        newNode.setRoot(Current.getRoot());

        int temp = newNode.getCurrentState()[BlankY-1][BlankX]; // Saving above value to be swapped with blank space below it
        newNode.getCurrentState()[BlankY-1][BlankX] = Current.getCurrentState()[BlankY][BlankX];    // Copy blank space to index above it
        newNode.getCurrentState()[BlankY][BlankX] = temp;   // Replace blank space with value above it
        newNode.BlankY--;   // Adjust stored index of blank space for respective board state accordingly

        newNode.setVisited(3); // Reset index of previous index of blank space to prevent loop
        return newNode;

    }
    public Node getRightNeighbor(Node Current){
        // Check to see if we will be at max depth now (null returned as we do not care for deeper nodes)
        if(Current.Depth >= MaxDepth){
            return null;
        }
        // Check if we are located on the edge of given board
        if(BlankX+1 >= 3){
            return null;
        }
        // Perform respective swap of empty space and top neighbor if possible
        Node newNode = new Node(Current.Depth+1, BlankX, BlankY, getRoot().getMaxDepth());
        newNode.setCurrentState(Current.getCurrentState());
        newNode.setGoalState(Current.getGoalState());
        newNode.setRoot(Current.getRoot());

        int temp = newNode.getCurrentState()[BlankY][BlankX+1]; // Saving above value to be swapped with blank space left it
        newNode.getCurrentState()[BlankY][BlankX+1] = Current.getCurrentState()[BlankY][BlankX];    // Copy blank space to index right of it
        newNode.getCurrentState()[BlankY][BlankX] = temp;   // Replace blank space with value stored
        newNode.BlankX++;   // Adjust stored index of blank space for respective board state accordingly

        newNode.setVisited(0); // Reset index of previous index of blank space to prevent loop
        return newNode;
    }
    public Node getBottomNeighbor(Node Current){
        // Check to see if we will be at max depth now (null returned as we do not care for deeper nodes)
        if(Current.Depth >= MaxDepth){
            return null;
        }
        // Check if we are located on the edge of given board
        if(BlankY+1 >= 3){
            return null;
        }
        // Perform respective swap of empty space and top neighbor if possible
        Node newNode = new Node(Current.Depth+1, BlankX, BlankY, getRoot().getMaxDepth());
        newNode.setCurrentState(Current.getCurrentState());
        newNode.setGoalState(Current.getGoalState());
        newNode.setRoot(Current.getRoot());

        int temp = newNode.getCurrentState()[BlankY+1][BlankX]; // Saving above value to be swapped with blank space above it
        newNode.getCurrentState()[BlankY+1][BlankX] = Current.getCurrentState()[BlankY][BlankX];    // Copy blank space to index below it
        newNode.getCurrentState()[BlankY][BlankX] = temp;   // Replace blank space with value stored
        newNode.BlankY++;   // Adjust stored index of blank space for respective board state accordingly

        newNode.setVisited(1); // Reset index of previous index of blank space to prevent loop
        return newNode;
    }
}


class Sort_Comparator implements Comparator<Node>{
    @Override
    public int compare(Node one, Node two){
        if(one.getCost() > two.getCost()){
            return 1;
        }else if(one.getCost() < two.getCost()){
            return -1;
        }else{
            if(one.getWrongTiles() > two.getWrongTiles()){
                return 1;
            }else if(one.getWrongTiles() < two.getWrongTiles()){
                return -1;
            }else{
                return 0;
            }
        }
    }
}
// - - - - - - - - - - - - - - - - - - - - - - - - DFS Class - - - - - - - - - - - - - - - - - - - - - - - -// 

class SearchAlgo{

    boolean Found;
    private List<Node> GoalPath;  // Keeps track of current/goal path
    
    private int Enqueued = 0;   // Keeps track of total number of states enqueued/searched

    // Max of 10 nodes as this is the max depth allowed
    PriorityQueue<Node> Neighbors = new PriorityQueue<Node>(new Sort_Comparator()); // Priority queue of possible moves in order of least cost
    SearchAlgo(){
        Found = false;
        GoalPath = new ArrayList<Node>();
    }

    // - - - - - - - - - - - - - - - - - Path Set Functions - - - - - - - - - - - - - - - - -// 

    
    // - - - - - - - - - - - - - - - - - Path Get Functions - - - - - - - - - - - - - - - - -// 
    public List<Node> getGoalPath(){
        return GoalPath;
    }
    public int getEnqueued(){
        return Enqueued;
    }

    // - - - - - - - - - - - - - - - - - DFS Related Functions - - - - - - - - - - - - - - - - -//
    public void DFS(Node CurrentState){

        // Check if we have reached the end/limit
        if(CurrentState == null){
            return;
        }
        // Check if we have reached the desired goal state
        if(Check(CurrentState) || !GoalPath.isEmpty()){
            GoalPath.add(CurrentState);
            Found = true;
            return;
        }

        // Traverse down the tree further - till max depth 10 [Prevent loop of switching same indexes]
        if(!CurrentState.getVisited()[2] && !Found){
            CurrentState.resetVisited();
            Enqueued++;
            DFS(CurrentState.getRightNeighbor(CurrentState)); // Will return node with right neighbor swapped out
        }
        if(!CurrentState.getVisited()[0] && !Found){
            CurrentState.resetVisited();
            Enqueued++;
            DFS(CurrentState.getLeftNeighbor(CurrentState)); // Will return node with left neighbor swapped out
        }
        if(!CurrentState.getVisited()[1] && !Found){
            CurrentState.resetVisited();
            Enqueued++;
            DFS(CurrentState.getTopNeighbor(CurrentState)); // Will return node with top neighbor swapped out
        }
        if(!CurrentState.getVisited()[3] && !Found){
            CurrentState.resetVisited();
            Enqueued++;
            DFS(CurrentState.getBottomNeighbor(CurrentState)); // Will return node with bottom neighbor swapped out
        }

        if(Found){
            GoalPath.add(CurrentState);
        }
    }
    // Checks if board state is desired goal state
    public boolean Check(Node CurrentState){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(CurrentState.getCurrentState()[i][j] != CurrentState.getGoalState()[i][j]){
                    return false;
                }
            }
        }
        // Goal state has been found
        return true;
    }

    // - - - - - - - - - - - - - - - - - IDS Related Functions - - - - - - - - - - - - - - - - -//
    public void IDS(Node Root){
        // Perform DFS but with iterative max depth
        for(int i = 0; i <= 10; i++){
            Root.setMaxDepth(i);
            DFS(Root);
            if(Found){
                break; // End iterative looping once 
            }
        }
    }
    
    // - - - - - - - - - - - - - - - - - A* Related Functions - - - - - - - - - - - - - - - - -//
    public void Astar1(Node Current, Node previous){
        // Total cost for path obtained by adding depth and wrong tiles
        // Check if we have reached the end/limit
        if(Current == null){
            return;
        }

       // Add node to potential path to goal
        GoalPath.add(Current);

        // Check if we have reached the desired goal state
        if(Check(GoalPath.get(GoalPath.size()-1))){
            Found = true;
            return;
        }

        // Create nodes for next possible states
        Current.PossibleMoves[0] = Current.getRightNeighbor(Current); // Will return node with right neighbor swapped out
        Current.PossibleMoves[1] = Current.getLeftNeighbor(Current); // Will return node with left neighbor swapped out
        Current.PossibleMoves[2] = Current.getTopNeighbor(Current); // Will return node with top neighbor swapped out
        Current.PossibleMoves[3] = Current.getBottomNeighbor(Current); // Will return node with bottom neighbor swapped out

        // Remove possibility for repeating previous move
        for(Node move : Current.PossibleMoves){
            if(previous == null){
                WrongTiles(Current);
            }
            if(move != null){
                if((previous == null) || !CheckStates(move, previous)){
                    WrongTiles(move);   // Calculate f(x) [wrong tiles and depth = cost of path]
                    Neighbors.add(move);    // Insert into priority queue to traverse
                }
            }
        }

        int temp = GoalPath.size();
        for(int i = temp-1; i >= 0; i--){
            if((GoalPath.size() >= Neighbors.peek().getDepth()) && Neighbors.peek().getDepth() <= GoalPath.get(i).getDepth()){
                GoalPath.remove(GoalPath.get(i));
            }
        }

        // Add to count for number of states checked
        Enqueued++;
        Astar1(Neighbors.poll(), Current); // Progress down chosen node path

    }
    public void WrongTiles(Node BoardState){
        if(BoardState == null){
            return;
        }
        // Count number of tiles in wrong space and assign
        int total = 0;
        for(int i = 0; i<3; i++){
            for(int j = 0; j<3; j++){
                if(BoardState.getCurrentState()[i][j] != BoardState.getGoalState()[i][j]){
                    total++;
                }
            }
        }
        // Assign number of wrong tiles to respective board state
        BoardState.setWrongTiles(total);
    }
    public boolean CheckStates(Node future, Node previous){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(future.getCurrentState()[i][j] != previous.getCurrentState()[i][j]){
                    return false;
                }
            }
        }
        return true;
    }

    public void Astar2(){

    }
    public void ManhattenDistance(){

    }
    
}