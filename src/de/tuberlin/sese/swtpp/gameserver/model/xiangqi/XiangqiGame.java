package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import de.tuberlin.sese.swtpp.gameserver.model.*;
//TODO: more imports from JVM allowed here


import java.io.Serializable;

public class XiangqiGame extends Game implements Serializable{
	

	
	/**
	 *
	 */
	private static final long serialVersionUID = 5424778147226994452L;

	/************************
	 * member
	 ***********************/

	// just for better comprehensibility of the code: assign red and black player
	private Player blackPlayer;
	private Player redPlayer;
	private String board;
	private String[] boardRows;

	// internal representation of the game state
	// TODO: insert additional game data here

	/************************
	 * constructors
	 ***********************/

	public XiangqiGame() {
		super();
		this.board = "rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR";
		this.boardRows = getBoardRows();

		// TODO: initialization of game state can go here
	}

	public String getType() {
		return "xiangqi";
	}

	/*******************************************
	 * Game class functions already implemented
	 ******************************************/

	@Override
	public boolean addPlayer(Player player) {
		if (!started) {
			players.add(player);

			// game starts with two players
			if (players.size() == 2) {
				started = true;
				this.redPlayer = players.get(0);
				this.blackPlayer= players.get(1);
				nextPlayer = redPlayer;
			}
			return true;
		}

		return false;
	}

	@Override
	public String getStatus() {
		if (error)
			return "Error";
		if (!started)
			return "Wait";
		if (!finished)
			return "Started";
		if (surrendered)
			return "Surrendered";
		if (draw)
			return "Draw";

		return "Finished";
	}

	@Override
	public String gameInfo() {
		String gameInfo = "";

		if (started) {
			if (blackGaveUp())
				gameInfo = "black gave up";
			else if (redGaveUp())
				gameInfo = "red gave up";
			else if (didRedDraw() && !didBlackDraw())
				gameInfo = "red called draw";
			else if (!didRedDraw() && didBlackDraw())
				gameInfo = "black called draw";
			else if (draw)
				gameInfo = "draw game";
			else if (finished)
				gameInfo = blackPlayer.isWinner() ? "black won" : "red won";
		}

		return gameInfo;
	}

	@Override
	public String nextPlayerString() {
		return isRedNext() ? "r" : "b";
	}

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 2;
	}

	@Override
	public boolean callDraw(Player player) {

		// save to status: player wants to call draw
		if (this.started && !this.finished) {
			player.requestDraw();
		} else {
			return false;
		}

		// if both agreed on draw:
		// game is over
		if (players.stream().allMatch(Player::requestedDraw)) {
			this.draw = true;
			finish();
		}
		return true;
	}

	@Override
	public boolean giveUp(Player player) {
		if (started && !finished) {
			if (this.redPlayer == player) {
				redPlayer.surrender();
				blackPlayer.setWinner();
			}
			if (this.blackPlayer == player) {
				blackPlayer.surrender();
				redPlayer.setWinner();
			}
			surrendered = true;
			finish();

			return true;
		}

		return false;
	}

	/* ******************************************
	 * Helpful stuff
	 ***************************************** */

	/**
	 *
	 * @return True if it's red player's turn
	 */
	public boolean isRedNext() {
		return nextPlayer == redPlayer;
	}

	/**
	 * Ends game after regular move (save winner, finish up game state,
	 * histories...)
	 *
	 * @param winner player who won the game
	 * @return true if game was indeed finished
	 */
	public boolean regularGameEnd(Player winner) {
		// public for tests
		if (finish()) {
			winner.setWinner();
			winner.getUser().updateStatistics();
			return true;
		}
		return false;
	}

	public boolean didRedDraw() {
		return redPlayer.requestedDraw();
	}

	public boolean didBlackDraw() {
		return blackPlayer.requestedDraw();
	}

	public boolean redGaveUp() {
		return redPlayer.surrendered();
	}

	public boolean blackGaveUp() {
		return blackPlayer.surrendered();
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 ******************************************/

	@Override
	public void setBoard(String state) {
		// Note: This method is for automatic testing. A regular game would not start at some artificial state.
		//       It can be assumed that the state supplied is a regular board that can be reached during a game.
		// TODO: implement
	}

	@Override
	public String getBoard() {
		// TODO: implement
		//return "rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR";
		return this.board;
	}
	
	public String[] getBoardRows() {
		return this.board.split("/");
	}
	
	public Figures getFigureFromField(String field,String playerColor)
	{
		Pair p = getFieldValue(field);
		char f = boardRows[p.x].charAt(p.y);
		Figures fig = new Figures(" ",' ');
		switch(Character.toLowerCase(f)) {
			case 'g': fig = new General(playerColor);break; 
			case 'a': fig = new Advisor(playerColor);break;
			case 'e': fig = new Elephant(playerColor);break;
			case 'h': fig = new Horse(playerColor);break;
			case 'r': fig = new Rook(playerColor);break;
			case 'c': fig = new Cannon(playerColor);break;
			case 's': fig = new Soldier(playerColor);break;
		}
		return fig; 
	}
	
	public Pair getFieldValue(String field)
	{ 
		Pair p = new Pair(field.charAt(0)- 97,Integer.parseInt(String.valueOf(field.charAt(1))));
		return p;	
	}
	
	public Pair pop(Pair s, Pair e)
	{
		return new Pair(Math.abs(s.x - s.x),Math.abs(e.y - e.y));
	}

	public String getPlayerColor(Player player){
		if(player.equals(redPlayer)){
			return "red";
		}
		return "black";
	}
	

	@Override
	public boolean tryMove(String moveString, Player player) {
		// TODO: implement
		String[] fields = moveString.split("-");
		String playerColor = getPlayerColor(player);
		Figures figure = getFigureFromField(fields[0],playerColor);
		Points p = new Points(getFieldValue(fields[0]),getFieldValue(fields[1]));
		boolean valid = figure.isValidMove(p, this.board);
		if(valid)
		{
			this.board = figure.applyMove(p, board);
		}
		return valid;
	}

}
