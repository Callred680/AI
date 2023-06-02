To Run Program:
    - Place Main.java in same folder/directory as input.txt
        - Input desired intial state and goal state into input.txt sepearated by spaces and in two lines
            - "0" is used to represent blank space within puzzle
            - First line: intital state EX: 6 7 1 8 2 0 5 4 3
            - Second line: goal state EX: 7 8 1 6 0 2 5 4 3
    - Open terminal and navigate to directory/folder containing Main.java and input.txt
        - Placing in downloads folder would be simplest way
            - cd Downloads
    - Compile Main with the command:
        - javac Main.java
    - Run program with the command:
        - java Main.java <desired search mode> input.txt
            - Search modes include [dfs, ids, astar1, astar2]
    - astar2 search mode hasn't been implemented due to time constraints, but the idea of the search is expanded upon in the report

Preconditions:
    - Only valid input is inputted into the file
        - Correct dimensions
        - Valid numbers (no duplicates or numbers outside 0-8 range)
        - "Blank space" is included as one of inputted values for intitial and goal state

Search Modes:
    - dfs
        - Search tree is searched starting with possible moves for right, left, top, then bottom neighbor for each node
        - Optimal path not always selected given search nature
    - ids
        - Higher chance of finding optimal path
        - Utilizes DFS search method with for loop changing max depth for each iteration
    - astar1
        - Utilizes the Hamming priority function
            - Combination of number of wrong tiles for each state and cost to reach state
    - astar2
        - Utilizes the Manhatten distance priority function
            - Comination of total distance of each tile in specific state from its goal state and cost to reach state

Input file path: /input.txt

Program file name: /Main.java

Input values: 
    6 7 1 8 2 0 5 4 3
    
    6 7 1
    8 2 0
    5 4 3

    - 0 is used to represent an empty slot in the 8 puzzle problem
    - 0 will be replace with blank on final display of board states

Goal State:
    - Second line in input file will represent desired goal state

Output:
    - Once completed, each step in reaching goal state will be printed
    - Total number of moves will be also displayed
    - Total number of states enqueued will be also displayed
        - Enqueue includes states that are visited, not just created to influence search decisions (astar usage)
