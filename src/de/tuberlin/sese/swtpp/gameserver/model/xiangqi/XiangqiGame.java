package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import de.tuberlin.sese.swtpp.gameserver.model.*;

import java.io.Serializable;
import java.util.*;

public class XiangqiGame extends Game implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5424778147226994452L;

	/************************
	 * member
	 ***********************/

	// just for better comprehensibility of the code: assign red and black player
	public Player blackPlayer;
	public Player redPlayer;
	public String board;

	// internal representation of the game state

	/************************
	 * constructors
	 ***********************/

	public XiangqiGame() {
		super();
		// this.board = "RHEAGAEHR/9/1C5C1/S1S1S1S1S/9/9/s1s1s1s1s/1c5c1/9/rheagaehr";
		this.board = "rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR";
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
				this.blackPlayer = players.get(1);
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

	/*
	 * ****************************************** Helpful stuff
	 */

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
		// Note: This method is for automatic testing. A regular game would not start at
		// some artificial state.
		// It can be assumed that the state supplied is a regular board that can be
		// reached during a game.
		this.board = state;
	}

	@Override
	public String getBoard() {
		return this.board;
	}

	public String[] getBoardRows() {
		Figures fig = new Figures("null", 'n');
		String[] rows = this.board.split("/");
		for (int i = 0; i < rows.length; i++) {
			rows[i] = fig.expandRow(new StringBuilder(rows[i])).toString();
		}
		return rows;
	}

	public Pair generalFromBoard(String color) {
		char identifier = color.equals("red") ? 'G' : 'g';
		Pair pos = new Pair(-1, -1);
		String[] rows = getBoardRows();
		for (int i = 0; i < rows.length; i++) {
			for (int j = 0; j < rows[9 - i].length(); j++) {
				if (rows[9 - i].charAt(j) == identifier) {
					pos = new Pair(j, i);
				}
			}
		}
		return pos;
	}

	public Figures getFigureFromPos(Pair pos, String playerColor) {
		Pair opposing = playerColor.equals("red") ? generalFromBoard("black") : generalFromBoard("red");

		char f = getBoardRows()[9 - pos.y].charAt(pos.x);
		Figures fig;
		switch (Character.toLowerCase(f)) {
		case 'g':
			fig = new General(playerColor, opposing);
			break;
		case 'a':
			fig = new Advisor(playerColor);
			break;
		case 'e':
			fig = new Elephant(playerColor);
			break;
		case 'h':
			fig = new Horse(playerColor);
			break;
		case 'r':
			fig = new Rook(playerColor);
			break;
		case 'c':
			fig = new Cannon(playerColor);
			break;
		case 's':
			fig = new Soldier(playerColor);
			break;
		default:
			fig = new Figures("null", 'n');
			break;
		}
		return fig;
	}

	public Pair getFieldValue(String field) {
		Pair p = new Pair(field.charAt(0) - 97, Integer.parseInt(String.valueOf(field.charAt(1))));
		return p;
	}

	public boolean validateMoveString(String moveString) {
		if (moveString.length() != 5)
			return false;
		char[] msLetter = moveString.toCharArray();
		boolean[] conditions = { (msLetter[0] > 96 && msLetter[0] < 106), (msLetter[1] > 47 && msLetter[1] < 58),
				(msLetter[2] == 45), (msLetter[3] > 96 && msLetter[3] < 106), (msLetter[4] > 47 && msLetter[4] < 58) };
		for (int i = 0; i < conditions.length; i++) {
			if (!conditions[i]) {
				return false;
			}
		}
		return true;
	}

	public Pair posOfGeneral(String color) {
		// if(this.debug)System.out.println(this.board);
		Pair res = new Pair(-1, -1);
		for (int x = 3; x < 6; x++) {
			if (color.equals("black")) {
				for (int y = 7; y < 10; y++) {
					Pair temp = new Pair(x, y);
					Figures result = this.getFigureFromPos(temp, color);
					if (result.identifier == 'g')
						res = temp;
				}
				if (res.x != -1)
					return res;
			}

			for (int y = 0; y < 3; y++) {
				Pair temp = new Pair(x, y);
				Figures result = this.getFigureFromPos(temp, color);
				if (result.identifier == 'G')
					res = temp;
			}
		}
		return res;
	}

	public List<Pair> figurePositionsOf(String color) {
		List<Pair> result = new LinkedList<Pair>();

		for (int x = 0; x < 9; ++x) {
			for (int y = 0; y < 10; ++y) {
				Pair temp = new Pair(x, y);
				Figures f = this.getFigureFromPos(temp, color);
				if (f.player.equals(color))
					result.add(temp);
			}
		}

		return result;
	}

	public boolean isReachable(Pair pos, String color) {
		List<Pair> figures = figurePositionsOf(color);

		for (Pair p : figures) {
			Figures f = this.getFigureFromPos(p, color);
			if (f.isValidMove(new Points(p, pos), board)) {
				return true;
			}
		}

		return false;
	}

	public boolean isInCheckmate(Player pl) {
		String tmpBoard = "";
		String color = pl.equals(this.redPlayer) ? "red" : "black";
		String col = color.equals("red") ? "black" : "red";
		Pair pos = posOfGeneral(color);
		Figures general = this.getFigureFromPos(pos, color);

		Pair[] dests = {

				new Pair(pos.x + 1, pos.y), new Pair(pos.x - 1, pos.y), new Pair(pos.x, pos.y + 1 > 9 ? 9 : pos.y + 1),
				new Pair(pos.x, pos.y - 1 < 0 ? 0 : pos.y - 1) };

		for (int i = 0; i < 4; ++i) {
			Points move = new Points(pos, dests[i]);
			tmpBoard = this.board;

			if (general.isValidMove(move, this.board)) {
				this.board = general.applyMove(move, board);
				if (!this.isReachable(dests[i], col)) {
					this.board = tmpBoard;
					return false;
				}
			}
			this.board = tmpBoard;
		}
		this.board = tmpBoard;
		return true;
	}

	public void dummy() {
		Figures dummyfigure = new Figures("null", 'n');
		boolean dummy = dummyfigure.isValidMove(new Points(new Pair(0, 0), new Pair(0, 0)), this.board);
		String dummyPlayerName = dummyfigure.getPlayer();
		char dummyIdentifier = dummyfigure.getIdentifier();
	}

	public boolean correctFigure(Pair field, String color) {
		String[] boardRows = this.getBoardRows();
		boolean is_red = Character.isUpperCase(boardRows[9 - field.y].charAt(field.x)) && color.equals("red");
		boolean is_black = Character.isLowerCase(boardRows[9 - field.y].charAt(field.x)) && color.equals("black");
		return is_red ^ is_black;
	}

	@Override
	public boolean tryMove(String moveString, Player player) {
		dummy();
		String color = player.equals(this.redPlayer) ? "red" : "black";

		if (isInCheckmate(player))
			return this.regularGameEnd(nextPlayer);

		Player opponent = player.equals(this.redPlayer) ? this.blackPlayer : this.redPlayer;
		if (!validateMoveString(moveString))
			return false;

		String[] fields = moveString.split("-");
		Pair field = getFieldValue(fields[0]);
		if (!correctFigure(field, color))
			return false;

		Figures figure = getFigureFromPos(field, color);
		Points p = new Points(getFieldValue(fields[0]), getFieldValue(fields[1]));
		if (figure.isValidMove(p, this.board)) {
			this.history.add(new Move(moveString, this.board, player));
			this.board = figure.applyMove(p, this.board);
			boolean checkmate = isInCheckmate(opponent);

			if (checkmate) {
				return this.regularGameEnd(player);
			}
			if (player == this.redPlayer) {
				nextPlayer = this.blackPlayer;
			} else {
				nextPlayer = this.redPlayer;
			}
			return true;
		}
		return false;
	}

}
