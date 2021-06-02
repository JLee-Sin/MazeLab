package mod;

import java.util.Random;

public class Maze {
	//A random object used for calculations
	Random r = new Random();

	//The integer representing the selected maze
	private int _i;

	//The current maze
	private boolean [][] _curMaze;

	//The possible mazes to choose from
	private final boolean[][][] _mazeList =
			{
					{
							{false,	false, 	true, 	false, 	false, 	false, 	false, 	false, 	false, 	false},
							{false,	false, 	true, 	false, 	true, 	true, 	true, 	false, 	false, 	false},
							{false,	false, 	true, 	true, 	true, 	false, 	true, 	false, 	false, 	false},
							{false,	false, 	true, 	false, 	true, 	true, 	true, 	false, 	false, 	false},
							{false,	false, 	true, 	false, 	false, 	false, 	false, 	true, 	true, 	true },
							{true,	true, 	true, 	true, 	true, 	true, 	true, 	true, 	false, 	true },
							{false,	false, 	true, 	false, 	false, 	false, 	false, 	true, 	true, 	true },
							{false,	false, 	true, 	false, 	false, 	false, 	false, 	false, 	false, 	false},
							{false,	false, 	true, 	false, 	false, 	false, 	false, 	false, 	false, 	false},
							{false,	false, 	false, 	false, 	false, 	false, 	false, 	false, 	false, 	false}
					},
					{
							{true,  false,  true,  true,  true,   false,  false,  false,  true,  false},
							{true,  true,   true,  false, true,   true,   true,   true,   true,  false},
							{true,  false,  true,  false, false,  false,  false,  true,   false, false},
							{true,  false,  true,  true,  true,   false,  false,  true,   true,  false},
							{true,  false,  true,  false, true,   true,   true,   true,   false, false},
							{false, false,  true,  false, false,  false,  false,  true,   true,  false},
							{false, false,  true,  true,  true,   true,   true,   true,   false, false},
							{false, true,   true,  true, false,  true,   false,  false,  false, false},
							{false, false,  false, true,  true,   true,   true,   true,   false, false},
							{false, false,  false, true,  false,  false,  false,  true,   false, false},
					},
					{
							{true,  false, true,  true,  true,  true,  true, true,  true,  false, true,  true,  true,  true,   false},
							{true,  true,  true,  false, true,  false, false, true,  false, false, true,  false, true,  false, true },
							{false, true,  false, true,  true,  false, false, true,  false, true,  true,  false, true,  true,  true },
							{true,  true,  false, true,  false, false, false, true,  true,  true,  false, false, false, true,  false},
							{true,  false, true,  true,  false, false, false, true,  false, false, false, false, false, false, false},
							{false, true,  true,  true,  false, false, true,  true,  false, false, false, false, false, true,  true },
							{true,  true,  false, true,  true,  true,  true,  false, false, false, false, true,  true,  true,  false},
							{false, true,  true,  true,  false, false, false, false, false, false, true,  false, true,  false, false},
							{false, false, false, true,  true,  true,  false, false, false, true,  true,  true,  true,  false, true },
							{false, false, false, false, false, true,  true,  true,  true,  true,  false, false, true,  true,  true },
					}
			};

	//The lists of possible start locations for each object
	private final int[][] _plyStartLocations = {{0,2}, {3,2}, {0,8}};
	private final int[][] _minStartLocations = {{5,9}, {4,0}, {2,4}};
	private final int[][] _exit = {{5,0}, {9,7}, {6,0}};
	private final int[][] _sword = {{8,2}, {0,8}, {5,14}};
	
	// currentLocations of the objects in the maze
	private int[] _curPlyStartLocations;
	private int[] _curMinStartLocation;
	private int[] _curExit;
	private int[] _curSwordLocations;

	public void set_curPlyStartLocations(int i) {_curPlyStartLocations = _plyStartLocations[i];}
	public void set_curMinStartLocation(int i) {_curMinStartLocation = _minStartLocations[i];}
	public void set_curExit(int i) {_curExit = _exit[i];}
	public void set_curSwordLocations(int i) { _curSwordLocations = _sword[i];}
	
	public boolean[][] getMaze() { return _curMaze; }
	public int[] getPlyStart() { return _curPlyStartLocations; }
	public int[] getMinStart() { return _curMinStartLocation; }
	public int[] getExit() { return _curExit; }
	public int[] get_swordLocations() { return _curSwordLocations; }
	public int getMazeNum() {return _i;}
	
	public void setCurMaze(int mazeNum) {
		_i = mazeNum;
		_curMaze = _mazeList[_i];
		set_curExit(_i);
		set_curMinStartLocation(_i);
		set_curPlyStartLocations(_i);
		set_curSwordLocations(_i);
	}

	public int randR() {
		int x = r.nextInt(8)+1;
		return x;
	}

	public int randC() {
		int y = 0;
		if(_i == 0 || _i == 1) {
			y = r.nextInt(8)+1;
		}
		else if(_i == 2) {
			y = r.nextInt(13)+1;
		}
		return y;
	}

	public int randRWiz() {
		int x = r.nextInt(8)+1;
		return x;
	}

	public int randCWiz() {
		int y =0;
		if(_i == 0 || _i == 1) {
			y = r.nextInt(8)+1;
		}
		else if(_i == 2) {
			y = r.nextInt(13)+1;
		}
		return y;
	}
}
