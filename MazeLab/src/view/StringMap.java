package view;

import mod.Maze;
import mod.Minotaur;
import mod.Player;
import mod.Sword;

//The class responsible for illustrating the game
public class StringMap {

	//The Strings used to represent the objects in the maze
	private final String _wall = "●";
	private final String _path = "○";
	private final String _ply = "P";
	private final String _min = "M";
	private final String _exit = "X";
	private final String _space = "     ";
	private final String _sword = "ⴕ";

	//The selected maze
	private Maze _maze;

	//The current player
	private Player _plyr;

	//The minotaur in the maze
	private Minotaur _mint;

	//The sword in the maze
	private Sword _swrd;
	
	public StringMap(Maze m, Player p, Minotaur t, Sword s) {
		_maze = m;
		_plyr = p;
		_mint = t;
		_swrd = s;
	}
	
	public String generateMap() {
		 String map = "";
		 for(int r = 0; r < _maze.getMaze().length; r++) {
			 for(int c = 0; c < _maze.getMaze()[0].length; c++) {
				 if(_plyr.getRow() == r && _plyr.getCol() == c) {
					 map += _ply + _space;
				 }
				 else if(_mint.getRow() == r && _mint.getCol() == c) {
					 map += _min + _space; 
				 }
				 else if(_swrd.getRow() == r && _swrd.getCol() == c && _plyr.hasSword() == false) {
				 	map += _sword + _space;
				 }
				 else if(_maze.getExit()[0] == r && _maze.getExit()[1] == c) {
					 map += _exit + _space; 
				 }
				 else if(_maze.getMaze()[r][c]) {
					 map += _path + _space;
				 }
				 else {
					map += _wall + _space; 
				 }
			 }
			 map += "\n";
		 }
		 map += "\n";
		
		 
		 
		 
		 
		 return map;
	}
	
}
