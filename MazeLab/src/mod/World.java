package mod;

import cont.JOP;
import view.StringMap;
import java.util.Random;

//The class representing the game itself
public class World {

	//A random object used in calculations
	private final Random rand = new Random();

	//The player
	private Player _player;

	//The minotaur
	private Minotaur _minotaur;

	//The maze
	private final Maze _maze;

	//The sword
	private Sword _sword;

	//The tunnel
	private Tunnel _tunnel;

	//The wizard
	private Wizard _wiz;

	//The string map used to illustrate the game
	private StringMap _s;

	//The number of steps the player has taken
	private int _cnt;

	//The randomly generated exit point for the tunnel
	private int _rpm = 0;
	private int _cpm = 0;

	//A boolean indicating if the wizard is alive or not
	private boolean _wizKill = false;

	//A counter for the number of steps an invisibility potion takes effect for
	private int _inviscnt;

	//The selected difficulty level
	private int _level;

	public World() {
		JOP.msg("Welcome to the Maze Game. Use WASD to move or E to exit at anytime, this message can also be viewed at anytime by pressing R", "Rules");
		JOP.msg("To win either navigate yourself, identified as the letter P to the exit, marked X, or kill the Minotaur, M, after collecting the Sword, ⴕ.", "Rules");
		JOP.msg("In addition on Easy and Medium difficulty there is a randomly placed wizard that will kill the Minotaur for you in exchange for 10 coins", "Rules");
		JOP.msg("Meanwhile on all difficulties there is a randomly placed tunnel that will transport you to a random location in the maze for 5 coins", "Rules");
		JOP.msg("Coins are given for each step you take and the amount of coins you have will reset each game", "Rules");
		JOP.msg("Coins, Tunnels, and Wizards are hidden on the map and thus cannot be seen on any difficulty", "Rules");
		JOP.msg("You can check your inventory by pressing I at anytime, the inventory shows if you have a sword or a shield and the number of coins you have", "Rules");
		JOP.msg("You can access the shop at anytime by pressing Z, the Shop allows you to buy special items that you can use to help you", "Rules");
		JOP.msg("You can also change the difficulty at any time using X; Warning this will reset the amount of coins you have", "Rules");
		_maze = new Maze();
		play();
	}
	public void play() {
		selectMaze();
		JOP.msg("Enjoy the game!", " ");
		// startROW startCOL
		_player = new Player(_maze.getPlyStart()[0], _maze.getPlyStart()[1]);
		_minotaur = new Minotaur(_maze.getMinStart()[0], _maze.getMinStart()[1]);
		_sword = new Sword(_maze.get_swordLocations()[0], _maze.get_swordLocations()[1]);
		_tunnel = new Tunnel(_maze.randR(), _maze.randC());
		while(!legitTun()) {
			if(_maze.randR() < 9 &&  _maze.randC() < 9){
				_tunnel = new Tunnel(_maze.randR()+1, _maze.randC()+1);
			}
			else if(_maze.randR() >= 9 && _maze.randC() < 9) {
				_tunnel = new Tunnel(_maze.randR()-1, _maze.randC()+1);
			}
			else if(_maze.randC() >= 9 && _maze.randR() < 9) {
				_tunnel = new Tunnel(_maze.randR()+1, _maze.randC()-1);
			}
			else if(_maze.randR() >= 9 && _maze.randR() >= 9) {
				_tunnel = new Tunnel(_maze.randR()-1, _maze.randC()-1);
			}
		}
		_s = new StringMap(_maze, _player, _minotaur, _sword);
		update();
	}

	public void update() {
		boolean isPlaying = true;
		while (isPlaying) {
			boolean on = true;
			// Get the Map
			String map = _s.generateMap();
			String msg = "WASD to move, E to exit, R to view rules again, X to change difficulty, I for inventory, Z for shop. What direction do you want to move?";
			hasSword();
			if (hasSword())
				JOP.msg("You got the Sword, you can now kill the Minotaur", "Sword");

			while (on) {
				// Get the player move
				String move = JOP.in(map + msg, "Maze Game");

				// move the player
				if (move == null) {

				} else if (getPlayerMove(move)) {
					on = false;
				}
			}

			// move the minotaur and account for invis potion
			if(!_player.hasInvisibilityPotion()) {
				moveMinotaur();
			}
			else if(_player.hasInvisibilityPotion()) {
				_inviscnt++;
				if(_inviscnt == 5) {
					_player.useInvisibilityPotion();
					_inviscnt = 0;
				}
			}

			if(_minotaur.getRow() == _player.getRow() && _minotaur.getCol() == _player.getCol() && _player.hasShield()) {
				JOP.msg("You managed to block the Minotaur's blow but your shield crumbled and sent you flying", "Block");
				_player.useShield();
				if(_maze.getMaze()[_player.getRow() - 1][_player.getCol() - 1] == true) {
					_player.setPos(_player.getRow()-1, _player.getCol()-1);
				}
				else if(_maze.getMaze()[_player.getRow() + 1][_player.getCol() - 1] == true) {
					_player.setPos(_player.getRow()+1, _player.getCol()-1);
				}
				else if(_maze.getMaze()[_player.getRow() - 1][_player.getCol() + 1] == true) {
					_player.setPos(_player.getRow()-1, _player.getCol()+1);
				}
				else if (_maze.getMaze()[_player.getRow() + 1][_player.getCol() + 1] == true) {
					_player.setPos(_player.getRow()+1, _player.getCol()+1);
				}
				update();
			}

			if(_level < 2 && _player.getRow() == _wiz.getRow() && _player.getCol() == _wiz.getCol()) {
				cast();
			}

			//check if minotaur has been killed
			if (kill() && !_wizKill) {
					isPlaying = false;
					JOP.msg("Congrats, you killed the Minotaur and found a way out of the maze in " + _cnt + " steps !", "Victory");
					JOP.msg("Maybe try a different maze???", "Victory");
					play();
			}

			if(_wizKill) {
				isPlaying = false;
				JOP.msg("Congrats, you paid a Wizard to kill the Minotaur and found a way out of the maze in " + _cnt + " steps !", "Victory");
				JOP.msg("Maybe try a different maze???", "Victory");
				play();
			}

			// check for victory by escape
			if (victory() && _minotaur.isAlive()) {
				isPlaying = false;
				JOP.msg("Congrats, you escaped by finding the exit in " + _cnt + " steps !", "Victory");
				JOP.msg("Maybe try a different maze???", "Victory");
				play();
			}

			// check for death
			if (death()) {
				isPlaying = false;
				JOP.msg("Wow what a loser. You suck. You lost, even after " + _cnt + " steps.", "Lost");
				JOP.msg("Maybe try a different maze???", "Lost");
				play();
			}

			if(_player.getRow() == _tunnel.getCol() && _player.getCol() == _tunnel.getCol()) {
				warp();
			}
		}


	}

	// change to getPlayerMove(String s) change to private.
	private boolean getPlayerMove(String s) {
		//Go to Shop
		if(s.equalsIgnoreCase("Z")) {
			JOP.msg("Shopkeeper: Welcome to the shop, what item would you like", "Shop");
			goToShop();
			return false;
		}
		//Check inventory
		if(s.equalsIgnoreCase("I")) {
			checkInventory();
			return false;
		}
		//Change Difficulty
		if (s.equalsIgnoreCase("X")) {
			play();
		}
		//End program
		if (s.equalsIgnoreCase("E")) {
			JOP.msg("Thank you for playing, close this message to end the game. ", "Exit");
			System.exit(0);
		}
		// View Rules
		if (s.equalsIgnoreCase("R")) {
			JOP.msg("Welcome to the Maze Game. Use WASD to move or E to exit at anytime, this message can also be viewed at anytime by pressing R", "Rules");
			JOP.msg("To win either navigate yourself, identified as the letter P to the exit, marked E, or kill the Minotaur, M, after collecting the Sword, S.", "Rules");
			JOP.msg("In addition on Easy and Medium difficulty there is a randomly placed wizard that will kill the Minotaur for you in exchange for 10 coins", "Rules");
			JOP.msg("Meanwhile on all difficulties there is a randomly placed tunnel that will transport you to a random location in the maze for 5 coins", "Rules");
			JOP.msg("Coins are given for each step you take and the amount of coins you have will reset each game", "Rules");
			JOP.msg("Coins, Tunnels, and Wizards are hidden on the map and thus cannot be seen on any difficulty", "Rules");
			JOP.msg("You can check your inventory by pressing I at anytime, the inventory shows if you have a sword or a shield and the number of coins you have", "Rules");
			JOP.msg("You can access the shop at anytime by pressing Z, the Shop allows you to buy special items that you can use to help you", "Rules");
			JOP.msg("You can also change the difficulty at any time using X; Warning this will reset the amount of coins you have", "Rules");
			return false;
		}
		// Moving North
		if (s.equalsIgnoreCase("W")) {
			if ((_player.getRow() - 1) >= 0 && _maze.getMaze()[_player.getRow() - 1][_player.getCol()]) {
				_player.setPos(_player.getRow() - 1, _player.getCol());
				coinAndStep();
				return true;
			} else {
				return false;
			}
		}
		// Moving South
		if (s.equalsIgnoreCase("S")) {
			if ((_player.getRow() + 1) < _maze.getMaze().length && _maze.getMaze()[_player.getRow() + 1][_player.getCol()]) {
				_player.setPos(_player.getRow() + 1, _player.getCol());
				coinAndStep();
				return true;
			} else {
				return false;
			}
		}
		// Moving East
		if (s.equalsIgnoreCase("D")) {
			if ((_player.getCol() + 1) < _maze.getMaze()[0].length && _maze.getMaze()[_player.getRow()][_player.getCol() + 1]) {
				_player.setPos(_player.getRow(), _player.getCol() + 1);
				coinAndStep();
				return true;
			} else {
				return false;
			}
		}
		// Moving West
		if (s.equalsIgnoreCase("A")) {
			if ((_player.getCol() - 1) >= 0 && _maze.getMaze()[_player.getRow()][_player.getCol() - 1]) {
				_player.setPos(_player.getRow(), _player.getCol() - 1);
				coinAndStep();
				return true;
			} else {
				return false;
			}
		}
		else if(s.isEmpty()) {
			JOP.msg("Thank you for playing, close this message to end the game. ", "Exit");
			System.exit(0);
		}
		JOP.msg("Please input a valid command.", "Error");
		return false;
	}

	private void moveMinotaur() {
		int rDist = _player.getRow() - _minotaur.getRow();
		int cDist = _player.getCol() - _minotaur.getCol();
		int r = _minotaur.getRow();
		int c = _minotaur.getCol();

		// Minotaur moving North
		if (rDist < 0 && _maze.getMaze()[r - 1][c]) {
			_minotaur.setPos(r - 1, c);
		}

		// Minotaur moving South.
		if (rDist > 0 && _maze.getMaze()[r + 1][c]) {
			_minotaur.setPos(r + 1, c);
		}
		// Minotaur moving East
		if (cDist > 0 && _maze.getMaze()[r][c + 1]) {
			_minotaur.setPos(r, c + 1);
		}
		// Minotaur moving west
		if (cDist < 0 && _maze.getMaze()[r][c - 1]) {
			_minotaur.setPos(r, c - 1);
		}
	}

	public boolean hasSword() {
		if (_player.getRow() == _sword.getRow() && _player.getCol() == _sword.getCol()) {
			_player.giveSword();
			return true;
		}
		return false;
	}

	public boolean death() {
		_player.kill();
		return _minotaur.getRow() == _player.getRow() && _minotaur.getCol() == _player.getCol() && !_player.hasSword();
	}

	public boolean kill() {
		if (_minotaur.getRow() == _player.getRow() && _minotaur.getCol() == _player.getCol() && _player.hasSword()) {
			_minotaur.kill();
			return true;
		}
		return false;
	}

	public boolean victory() {
		if (_player.getRow() == _maze.getExit()[0] && _player.getCol() == _maze.getExit()[1])
			return true;
		else return !_minotaur.isAlive();
	}


	public void selectMaze() {
		String select = "Would you like to play the Easy level, the Medium level, or the Hard level. You can also use E to exit.";
		String selection = JOP.in(select, "Level Select");
		if(selection.equalsIgnoreCase("Easy")) {
			_maze.setCurMaze(0);
			makeWiz();
			_level=0;
		}
		else if(selection.equalsIgnoreCase("Medium")) {
			_maze.setCurMaze(1);
			makeWiz();
			_level=1;
		}
		else if(selection.equalsIgnoreCase("Hard")) {
			_maze.setCurMaze(2);
			_level=2;
		}
		else if(selection.equalsIgnoreCase("E")) {
			JOP.msg("Thank you for playing!", "Exit");
			System.exit(0);
		}
		else {
			JOP.msg("Please pick maze Easy, Medium, or Hard", "Error");
			selectMaze();
		}
	}

	public boolean legitTun() {
		int r = _tunnel.getRow();
		int c = _tunnel.getCol();
		boolean spot = _maze.getMaze()[r][c];
		return spot;
	}

	public void warp() {
		JOP.msg("You found a Tunnel", "Tunnel");
		String decide = "Would you like to use it? (Y/N)";
		String decision = JOP.in(decide, "Tunnel");
		if (decision.equalsIgnoreCase("Y") && _player.getNumOfCoins() >= 5) {
			_player.warp();
			findExit();
			while(!_maze.getMaze()[_rpm][_cpm]) {
				if(_rpm > 8) {
					_rpm--;
				}
				else if(_rpm < 2) {
					_rpm++;
				}
				if(_cpm > 8){
					_cpm--;
				}
				else if(_cpm < 2) {
					_cpm++;
				}
			}
			_player.setPos(_rpm,_cpm);
		}
		else if (decision.equalsIgnoreCase("N")) {
			JOP.msg("You back away from the tunnel and head back into the maze", "Tunnel");
		}
		else if (_player.getNumOfCoins() < 5) {
			JOP.msg("Sorry, you dont have enough coins to use the tunnel", "Tunnel");
		}
		else if (!decision.equalsIgnoreCase("Y") && !decision.equalsIgnoreCase("N")) {
			JOP.msg("Please give a valid input", "Error");
			warp();
		}
	}

	public void findExit() {
		if (_maze.getMazeNum() == 0 || _maze.getMazeNum() == 1) {
			_rpm = rand.nextInt(10)+1;
		}
		else{
			_rpm = rand.nextInt(15)+1;
		}
		if (_maze.getMazeNum() == 0 || _maze.getMazeNum() == 1 || _maze.getMazeNum() == 2) {
			_cpm = rand.nextInt(10)+1;
		}
	}

	public void coinAndStep() {
		_player.addCoin();
		_cnt++;
	}

	public void checkInventory() {
		if(_player.hasSword()) {
			JOP.msg("You have a sword and " + _player.getNumOfCoins() + " coins", "Inventory");
		}
		else if (_player.hasShield()) {
			JOP.msg("You have a shield and " + _player.getNumOfCoins() + " coins", "Inventory");
		}
		else {
			JOP.msg("You have " + _player.getNumOfCoins() + " coins", "Inventory");
		}
	}

	public boolean legitWiz() {
		int rW = _wiz.getRow();
		int cW = _wiz.getCol();
		boolean spot = _maze.getMaze()[rW][cW];
		return spot;
	}

	public void cast() {
		JOP.msg("A mysterious cloaked figure approaches you", "Event");
		JOP.msg("Wizard: Hello young traveller, for a small price I will rid you of the beast in this place.", "Wizard");
		String choice = "Would you like me to use my magic? (Y/N)";
		String madeChoice = JOP.in(choice, "Wizard");
		if (madeChoice.equalsIgnoreCase("Y") && _player.getNumOfCoins() >= 10) {
			JOP.msg("Wizard: Very well, it shall be done", "Wizard");
			_wizKill = true;
			_player.spell();
			_minotaur.kill();
		} else if (madeChoice.equalsIgnoreCase("N")) {
			JOP.msg("Wizard: Very well I shall await here in the meantime...", "Wizard");
		}
		else if (_player.getNumOfCoins() < 10) {
			JOP.msg("It appears you have insufficient funds, please return here once you have 10 coins", "Wizard");
		}
		else if (!madeChoice.equalsIgnoreCase("Y") && !madeChoice.equalsIgnoreCase("N")) {
			JOP.msg("Please give a valid input", "Error");
			cast();
		}
	}

	public void goToShop() {
		String items = "Press I for an Invisibility Potion, press S for a Shield, press R for a Return Potion or press E to leave";
		String chosenItem = JOP.in(items, "Shop");
		if(chosenItem.equalsIgnoreCase("I")) {
			String invis = "The Potion of Invisibility will stop the Minotaur for 5 moves, it costs 20 coins and takes effect immediately. Would you like this item? (Y/N)";
			String invischoice = JOP.in(invis, "Potion of Invisibility");
			if(_player.getNumOfCoins() < 20) {
				JOP.msg("It seems like you dont have enough for this...", "Shop");
				goToShop();
			}
			else if(invischoice.equalsIgnoreCase("Y") && _player.getNumOfCoins() >= 20) {
				_player.invis();
				_player.giveInvisibilityPotion();
				JOP.msg("Shopkeeper: Thank you, please come again soon", " ");
			}
			else if(invischoice.equalsIgnoreCase("N")) {
				JOP.msg("Shopkeeper: Is there anything else that catches your eye?", "Shop");
				goToShop();
			}
		}
		if(chosenItem.equalsIgnoreCase("S")) {
			String shield = "The Shield will protect you from the Minotaur once but looks flimsy and can't be used with a sword, it costs 30 coins. Would you like this item? (Y/N)";
			String shieldchoice = JOP.in(shield, "Shield");
			if(_player.getNumOfCoins() < 30) {
				JOP.msg("It seems like you dont have enough for this...", "Shop");
				goToShop();
			}
			else if(_player.hasSword()) {
				JOP.msg("This is too heavy to hold with your sword so you put it back on the shelf", "Shop");
				goToShop();
			}
			else if(shieldchoice.equalsIgnoreCase("Y") && _player.getNumOfCoins() >= 30 && !_player.hasSword()) {
				_player.shield();
				_player.giveShield();
				JOP.msg("Shopkeeper: Thank you, please come again soon", "Shop");
			}
			else if(shieldchoice.equalsIgnoreCase("N") && _player.getNumOfCoins() >= 30 && !_player.hasSword()) {
				JOP.msg("Shopkeeper: Is there anything else that catches your eye?", "Shop");
				goToShop();
			}
		}
		if(chosenItem.equalsIgnoreCase("R")) {
			String ret = "The Potion of Returning will bring you to the entrance of the maze, it costs 5 coins and takes effect immediately. Would you like this item? (Y/N)";
			String retchoice = JOP.in(ret, "Potion of Returning");
			if(_player.getNumOfCoins() < 5) {
				JOP.msg("It seems like you dont have enough for this...", "Shop");
				goToShop();
			}
			else if(retchoice.equalsIgnoreCase("Y") && _player.getNumOfCoins() >= 5) {
				_player.warp();
				JOP.msg("Shopkeeper: Thank you, please come again soon", "Shop");
				_player.setPos(_maze.getPlyStart()[0], _maze.getPlyStart()[1]);
				JOP.msg("You were suddenly moved in a gust of wind", "Shop");
				update();
			}
			else if(retchoice.equalsIgnoreCase("N")) {
				JOP.msg("Shopkeeper: Is there anything else that catches your eye?", "Shop");
				goToShop();
			}
		}
		if(chosenItem.equalsIgnoreCase("E")) {
			JOP.msg("Shopkeeper: Please come again soon", "Shop");
		}
	}

	public void makeWiz() {
		_wiz = new Wizard(_maze.randRWiz(), _maze.randCWiz());
		while(!legitWiz()) {
			if(_maze.randRWiz() > 1 && _maze.randRWiz() < 9 && _maze.randCWiz() > 1 && _maze.randCWiz() < 9){
				_wiz = new Wizard(_maze.randRWiz()+1, _maze.randCWiz()+1);
			}
			else if(_maze.randRWiz() >= 9 && _maze.randCWiz() < 9) {
				_wiz = new Wizard(_maze.randRWiz()-1, _maze.randCWiz()+1);
			}
			else if(_maze.randCWiz() >= 9 && _maze.randRWiz() < 9) {
				_wiz = new Wizard(_maze.randRWiz()+1, _maze.randCWiz()-1);
			}
			else if(_maze.randRWiz() >= 9 && _maze.randRWiz() >= 9) {
				_wiz = new Wizard(_maze.randRWiz()-1, _maze.randCWiz()-1);
			}
		}
	}
}