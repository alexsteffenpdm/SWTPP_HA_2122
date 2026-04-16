package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

public class Soldier extends Figures {

	public Soldier(String p) {
		super(p, 's');
	}

	@Override
	public boolean isValidMove(Points p, String board) {
		Pair absDif = p.absDifference();
		Pair dif = p.difference();
		boolean valid_vertical_move = absDif.x == 0 && absDif.y == 1;
		boolean valid_horizontal_move = absDif.x == 1 && absDif.y == 0;

		if (this.ownFigure(this.getFieldValue(p.e, board)))
			return false;

		if (this.player.equals("red")) {
			if (p.s.y < 5) {
				return valid_vertical_move && dif.y > 0;
			} else {
				return valid_horizontal_move ^ (valid_vertical_move && dif.y > 0);
			}
		} else {
			if (p.s.y > 4) {
				return valid_vertical_move && dif.y < 0;
			} else {
				return valid_horizontal_move ^ (valid_vertical_move && dif.y < 0);
			}
		}
	}
}
