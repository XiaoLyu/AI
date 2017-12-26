import java.io.*;
import java.util.*;

public class Solver{
	
	public static final String Digits = "123456789";
	public static final String Rows = "ABCDEFGHI";
	public static final String Cols = "123456789";
	
	//square: A1 -- I9
	public final static ArrayList<String> square = cross(Rows, Cols);
	
	//unitlist: store all units and peers for the square
	public final static ArrayList<ArrayList<String>> unitlist = new ArrayList<ArrayList<String>>();
	
	//store three unitlists for every square
	public final static HashMap<String, ArrayList<ArrayList<String>>> units = new HashMap<String, ArrayList<ArrayList<String>>>();
	
	public final static HashMap<String, HashSet<String>> peers = new HashMap<String, HashSet<String>>();
	
	public static void main(String[] args) {
		
		
		Solver solver = new Solver();
		Grid grid = solver.giveSolution("85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4.");
		try {
			System.out.println("This is the result of 50 easy problems:");
			solver.timeRecord("/Users/lyuxiao/Desktop/AI project/easy50.txt");
			System.out.println("--------------------------------------------");
			System.out.println("This is the result of 11 hardest problems:");
			solver.timeRecord("/Users/lyuxiao/Desktop/AI project/hardest.txt");
			System.out.println("--------------------------------------------");
			System.out.println("This is the result of top 95 hard problems:");
			solver.timeRecord("/Users/lyuxiao/Desktop/AI project/top95.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//set the value of unitlist, units and peers
	static{
		//one columns * 9
		for(int i = 0; i < Cols.length(); i++){
			ArrayList<String> s = cross(Rows, Character.toString(Cols.charAt(i)));
			unitlist.add(s);
		}
		
		//one rows * 9
		for(int i =  0; i < Rows.length(); i++){
			ArrayList<String> s = cross(Character.toString(Rows.charAt(i)), Cols);
			unitlist.add(s);
		}
		
		//one box * 9
		String rs[] = {"ABC", "DEF", "GHI"};
		String cs[] = {"123", "456", "789"};
		
		for(int i = 0; i < rs.length; i++){
			for(int j = 0; j < cs.length; j ++){
				ArrayList<String> s = cross(rs[i], cs[j]);
				unitlist.add(s);
			}
		}
		
		for(String s : square){ 
			//units: for every square, unitlists containing this square
			ArrayList<ArrayList<String>> unitsvalue = new ArrayList<ArrayList<String>>();
			for(ArrayList<String> u : unitlist){
				if(u.contains(s)){
					unitsvalue.add(u);
				}
				units.put(s, unitsvalue);
			}
			
			//peers: for every square, store its 20 peers, no repeated
			HashSet<String> peersvalue = new HashSet<String>();
			for(ArrayList<String> i : units.get(s)){
				for(String t : i){
					if(!t.equals(s)){
						peersvalue.add(t);
					}
				}
			}
			peers.put(s, peersvalue);
		}
	}
	
	//generate cross of A and B, ex: A1, A2...
	private static ArrayList<String> cross(String A, String B){
		int lengthA = A.length();
		int lengthB = B.length();
		
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i = 0; i < lengthA; i++){
			for(int j = 0; j < lengthB; j++){
				String s = Character.toString(A.charAt(i)) + Character.toString(B.charAt(j));
				result.add(s);
			}
		}
		return result;
	}
	
	//check the format of input grid
	public boolean checkValidGrid(String grid){
		if(grid == null)
			return false;
		
		int length = 0;
		for(int i = 0; i < grid.length(); i++){
			if(grid.charAt(i) == '0' | grid.charAt(i) == '.' | Digits.indexOf(grid.charAt(i)) != -1){
				//get useful length of a grid
				length++;
			}
		}
		if(length == 81)
			return true;
		else
			System.out.println("Not a valid grid");
			
			return false;
	}

	//get gridvalue from a string, convert grid into HashMap<String, Character>
	//if it's '.' or '0', put empty
	private HashMap<String, String> gridValues(String grid){
		
		HashMap<String, String> gridValues = new HashMap<String, String>();
		
		int j = 0;
		if(checkValidGrid(grid)){
			for(int i = 0; i < grid.length(); i++){
				if(grid.charAt(i) == '0' | grid.charAt(i) == '.' | Digits.indexOf(grid.charAt(i)) != -1){
					j++;
					if(Digits.indexOf(grid.charAt(i)) != -1){
						gridValues.put(square.get(j-1), Character.toString(grid.charAt(i)));
					}
				}
			}
		}else{
			return null;
		}
		return gridValues;
	}
	
	//convert string into grid 
	//if contradiction, break
	public Grid parseGrid(String grid){
		Grid gridValue = new Grid();
		
		//initialize: for every square, allow all the digits
		for(String key : square){
			gridValue.getValues().put(key, Digits);
		}
		
		HashMap<String, String> gridvalue = gridValues(grid);
		
		if (gridvalue != null)
		{
			for(Map.Entry<String, String> entry : gridvalue.entrySet()){
				String key = entry.getKey();
				String d = entry.getValue();
				if(!assign(gridValue, key, d).isValid()){
					break;
				}
			}
		}
		else
			gridValue.setValid(false);
		return gridValue;
	}

	//display grid
	public Grid displayGrid(String grid){
		Grid gridValue = new Grid();
		
		//initialize: for every square, allow all the digits
		for(String key : square){
			gridValue.getValues().put(key, Digits);
		}
		
		HashMap<String, String> gridvalue = gridValues(grid);
		
		if (gridvalue != null)
		{
			for(Map.Entry<String, String> entry : gridvalue.entrySet()){
				String key = entry.getKey();
				String d = entry.getValue();
				gridValue.getValues().put(key, d);
			}
		}
		else
			gridValue.setValid(false);
		return gridValue;
	}
	
	//assign : eliminate all other values except d from values
	//return values
	//return false if contradiction
	public Grid assign(Grid values, String squareKey, String d){
		HashMap<String, String> gridValues = values.getValues();
		String leftValues = gridValues.get(squareKey);
		leftValues = leftValues.replace(d, "");
		for(int i = 0; i < leftValues.length(); i++){
			String s = Character.toString(leftValues.charAt(i));
			if(!eliminate(values, squareKey, s)){
				break;
			}
		}
		return values;
	}
	
	//eliminate d from values[squarekey] using constraints
	public boolean eliminate(Grid values, String squarekey, String eliminateNum){
		String squareValue = values.getValues().get(squarekey);
		
		//if value wanted to be eliminated is not in the values, already eliminated
		if(!squareValue.contains(eliminateNum)){
			return true;
		}
		
		//if eliminateNum is in the values, eliminate it
		String newDigit = squareValue.replace(eliminateNum, "");
		values.getValues().put(squarekey, newDigit);
		
		//
		//eliminate last value of a square -> Contradiction
		if(newDigit.length() == 0){
			values.setValid(false);
			return false;
		}
		
		//if only one value left, if other peers don't contains the value -> Contradiction
		else if(newDigit.length() == 1){
			for(String peersKey : peers.get(squarekey)){
				if(!eliminate(values, peersKey, newDigit)){
					values.setValid(false);
					return false;
				}
			}
		}
		
		//if a unit(box, row, column) has a value only in certain square, assign it
		for(ArrayList<String> unitsValue : units.get(squarekey)){
			ArrayList<String> places = new ArrayList<String>();
			for(String s : unitsValue){
				if(values.getValues().get(s).contains(eliminateNum)){
					places.add(s);
				}	
			}
			
			if(places.size() == 0){
				values.setValid(false);
				return false;
			}
			else if(places.size() == 1){
				if(!assign(values, places.get(0), eliminateNum).isValid()){
					values.setValid(false);
					return false;
				}
			}
		}
		return values.isValid();
	}
	
	//search
	public Grid search(Grid grid){
		
		if(!grid.isValid())
			return grid;
			
		//If every square has only one value, then solved.
		boolean solved = true;
		for(String s : square){
			if(grid.getValues().get(s).length() != 1){
				solved = false;
			}
		}
		if(solved){
			return grid;
		}
		
		//If not solved, then choose square with fewest possibilities(>1)
		String chooseSquare = mrv(grid);
		String possibilities = grid.getValues().get(chooseSquare);
		int size = possibilities.length();
		
		for(int i = 0; i < size; i++){
			//get one possibility
			String chooseValue = Character.toString(possibilities.charAt(i));
			
			//copy current grid
			Grid currentGrid = copy(grid);
			
			//if return false, than change other possibilities
			currentGrid = search(assign(currentGrid, chooseSquare, chooseValue));
			if(currentGrid.isValid())
				return currentGrid;
		}
		
		grid.setValid(false);
		return grid;
	}
	
	//copy a grid
	public Grid copy(Grid grid){
		Grid currentGrid = new Grid();
		currentGrid.setValid(grid.isValid());
		
		HashMap<String, String> values = grid.getValues();
		HashMap<String, String> currentValues = currentGrid.getValues();
		for(Map.Entry<String, String> entry : values.entrySet()){
			currentValues.put(entry.getKey(), new String(entry.getValue()));
		}
		return currentGrid;
	}
	
	//choose the square with fewest possibilites(minimum remaining value)(>1)
	public String mrv(Grid grid){
		int minlength = Integer.MAX_VALUE;
		String chooseSquare = null;
		for(String s : square){
			String possibilities = grid.getValues().get(s);
			if(possibilities.length() < minlength && (possibilities.length() > 1)){
				minlength = possibilities.length();
				chooseSquare = s;
			}	
		}
		return chooseSquare;
	}
	
	//parse a grid and give solution
	public Grid giveSolution(String problem){
		Grid grid = parseGrid(problem);
		Grid solved = search(grid);
	//	timeRecord(problem);
		return solved;
	}
	
	//Testing
	//get time of the solving
	public void timeRecord(String path) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(path));
		BufferedReader reader1 = new BufferedReader(new FileReader(path));
		
		int num = 0;
		
		while(reader.readLine() != null){
			num++;
		}
		
		String[] problems = new String[num];
		
		for(int i = 0; i < num; i++){
			problems[i] = reader1.readLine().trim();
		}
		
		long starttime = System.currentTimeMillis();
		
		for(int i = 0; i < num; i++){
			giveSolution(problems[i]);
		}
		
		long endtime = System.currentTimeMillis();
		
		double totalTime = (double)(endtime - starttime);
		System.out.println("Solved " + num + " problems.");
		System.out.println("Solved in " + (double)totalTime/(double)1000 + " seconds.");
		System.out.println("Solved every problem in average " + (double)totalTime/((double)(1000) * (double)num) + " seconds.");
	}
}
