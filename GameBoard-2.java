package hw3;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
//user/alexrubio tesghw3 javac PA1Driver.java GameBoard.java; java PA1Driver
//add the imports necessary for your implementation
public class GameBoard {
	Configuration initConfig; // initial set up
	HashMap<HashKey, Configuration> explored = new HashMap<>(); // for BFS
	//any other attribute that is necessary for your implementation
	//comes here
	int numOfPaths = 0;
	public void readInput(String fName) throws IOException {
	FileInputStream fstream = new FileInputStream(fName);
	BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	String strLine;
	String[] strEl;
	int numOfVehicles;
	int[] locs;
	//reading the first line for the numOfVehicles
	strLine = br.readLine();
	strEl = strLine.trim().split("\\s+");
	numOfVehicles = Integer.parseInt(strEl[0]);
	initConfig = new Configuration();
	ArrayList<int[]> vehicles = new ArrayList<>();
	for (int i=0; i < numOfVehicles; i++) {
		strLine = br.readLine();
		strEl = strLine.trim().split("\\s+");
		locs = new int[strEl.length];
		for (int j=0; j<locs.length; j++) {
			locs[j] = Integer.parseInt(strEl[j]);
		}
		vehicles.add(locs);
	}
	initConfig.setVehicles(vehicles);
	}
	public ArrayList<Pair> getPlan() {
		//this will call explore method below
		//and generate a plan
		// you will need to implement this to get a path
		// this will call the method explore, an implementation of
		// breadth-first exploration
		// retrieves the path/plan using the parent information
		// starting from the (/any) goal configuration
		Configuration solution = explore(initConfig);
		//turn to plan and return it
		ArrayList<Pair> plan = new ArrayList<>();

		if (solution != null) {
            // Reconstruct the plan by backtracking from the goal configuration to the start
            Configuration current = solution;
            while (current != null && current.getParent() != current) {
                Pair move = current.pair;
                plan.add(0, move); // Add moves to the beginning of the plan list
                current = current.getParent();
            }
        }
        return plan;
	}
	//This is BFS exploration
	public Configuration explore(Configuration start) {
		//input parameter is the start configuration for the BFS.
		//output is the goal configuration
		//getplan will use the return of explore to generate the plan
		/* BFS algorithm
		
		add the start configuration to the hashmap (i.e., start has been visited)
		add the start configuration to a queue
		while queue is not empty {
		c = remove the element from the head of the queue;
		if c is a goal
		return c;
		get a list of neighbors of c
		for each neighbor n
		if the n is not present in the hashmap (i.e., not visited)
		c becomes n's parent
		add n to the hashmap
		add n to the queue
		}
		if queue is empty then the goal has not been reach, return null
		 */
        Queue<Configuration> queue = new LinkedList<>();
        explored.put(new HashKey(start.getLastLocs()), start);
        start.setParent(start);
        queue.add(start);
        
        while (!queue.isEmpty()) {
            Configuration current = queue.poll();

            if (current.isGoalReached()) {
                numOfPaths = numOfPaths + 1;
                return current;
            }

            ArrayList<Configuration> neighbors = current.getNext();
            for (Configuration neighbor : neighbors) {
            	HashKey hashKey = new HashKey(neighbor.getLastLocs());
                if (!explored.containsKey(hashKey)) {
                    neighbor.setParent(current);
                    explored.put(hashKey, neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return null;
	}
	//you can write any other method necessary for your implementation
	public int getNumOfPaths() {
		
		return numOfPaths;
	}
}
class Configuration {
	ArrayList<int[]> vehicles = new ArrayList<>(); // location for each vehicle: 0th vehicle is the icecream vehicle
	//any other attribute you want
	Configuration parent; // to be used in the BFS exploration method explore and getPlan method
	//add methods as appropriate;
	//getLastLocs is use to generate the hashkey attribute for the configuration
	Pair pair;
	public int[] getLastLocs() {
		int[] res = new int[vehicles.size()];
		for (int i=0; i<vehicles.size(); i++) {
			int[] locs = vehicles.get(i);
			res[i] = locs[locs.length-1];
		}
		return res;
	}

	public Configuration getParent() {
        return parent;
    }

	public void setParent(Configuration parent) {
		this.parent = parent;	
	}

	public boolean isGoalReached() {
		//newConf.isGoalReached();
		int [] icecream = vehicles.get(0);
		if(icecream[1] == 18) {
			return true;
		}
		return false;
	}

	public void setVehicles(ArrayList<int[]> vehicles2) {
		// TODO Auto-generated method stub		
		for (int [] newV : vehicles2) {
			int[] copy = new int[newV.length];
			for(int j = 0; j < newV.length; j++) {
		    	copy[j] = newV[j];
			}
			vehicles.add(copy);
		}
	}

	public ArrayList<Configuration> getNext() {
		//outputs all moves in array vehicles
		ArrayList<Configuration> nextMoves = new ArrayList<>();
		for(int i = 0; i < vehicles.size(); i++) {
			int [] vehicle = vehicles.get(i);
			if(vehicle[0]+1 == vehicle[1]) {
				if(canMove('e', i)) {
					Configuration conf = new Configuration();
					conf.pair = new Pair(i, 'e');
					conf.setVehicles(vehicles);
					for(int j =0 ; j < vehicle.length; j++) {
						conf.vehicles.get(i)[j] += 1;
					}
					nextMoves.add(conf);
				}
				if(canMove('w', i)) {
					Configuration conf = new Configuration();
					conf.pair = new Pair(i, 'w');
					conf.setVehicles(vehicles);
					for(int j =0 ; j < vehicle.length; j++) {
						conf.vehicles.get(i)[j] -= 1;
					}
					nextMoves.add(conf);
				}
			}
			else {
				if(canMove('n', i)) {
					Configuration conf = new Configuration();
					conf.pair = new Pair(i, 'n');
					conf.setVehicles(vehicles);
					for(int j =0 ; j < vehicle.length; j++) {
						conf.vehicles.get(i)[j] -= 6;
					}
					nextMoves.add(conf);
				}
				if(canMove('s', i)) {
					Configuration conf = new Configuration();
					conf.pair = new Pair(i, 's');
					conf.setVehicles(vehicles);
					for(int j =0 ; j < vehicle.length; j++) {
						conf.vehicles.get(i)[j] += 6;
					}
					nextMoves.add(conf);
				}

			}
		}
		return nextMoves;
		//implement this to find all the neighbors of "this" configuration
	}
	public boolean canMove(char direction, int i) {
		if(direction == 'e') {
			int last = vehicles.get(i).length -1;
			if((vehicles.get(i)[last])%6  != 0) {
				if(emptySlot(i, last, 1)) {
					return true;
				}
			}
		}
		if(direction == 'w') {
			if((vehicles.get(i)[0] -1)%6  != 0) {
				if(emptySlot(i, 0, -1)) {
					return true;
				}
			}
		}
		if(direction == 'n') {
			if((vehicles.get(i)[0]) > 6) {
				if(emptySlot(i, 0, -6)) {
					return true;
				}
			}
		}
		if(direction == 's') {
			int last = vehicles.get(i).length -1;
			if((vehicles.get(i)[last]) < 31) {
				if(emptySlot(i, last, 6)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean emptySlot(int i, int start, int value) {
		int neighbor = vehicles.get(i)[start] + value;
		for(int j = 0; j < vehicles.size(); j++) {
			for(int k = 0; k < vehicles.get(j).length; k++) {
				if(neighbor == vehicles.get(j)[k]) {
					return false;
				}
			}
		}
		return true;
	}
	
}
//No need to modify anything below
class HashKey {
	int[] c;
	public HashKey(int[] inputc) {
		c = new int[inputc.length];
		c = inputc;
	}
	public boolean equals(Object o) {
		boolean flag = true;
		if (this == o) return true;
		if ((o instanceof HashKey)) {
			HashKey h = (HashKey)o;
			int[] locs1 = h.c;
			int[] locs = c;
			if (locs1.length == locs.length) {
				for (int i=0; i<locs1.length; i++) {
					if (locs1[i] != locs[i]) {
						flag = false;
						break;
					}
				}
			}
			else
				flag = false;
		}
		else
			flag = false;
		return flag;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return Arrays.hashCode(c);
	}
}
//For constructing the solution
class Pair {
	int id;
	char direction; // 1: east; 2: south; 3: west; 4: north
	public Pair(int i, char d) {
		id = i; direction = d;
	}
	char getDirection() {
		return direction; 
	}
	int getId() {
		return id;
		}
	void setDirection(char d) { 
		direction = d;
		}
	void setId(int i) {
		id = i;
	}
}
