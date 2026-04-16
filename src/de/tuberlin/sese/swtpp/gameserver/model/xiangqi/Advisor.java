package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

public class Advisor extends Figures {

	public Advisor(String p) {
		super(p, 'a');
	}

	private boolean isInBound(Pair target, String color) {
		if (color.equals("red")) // Red
			return target.y < 3 && target.x > 2 && target.x < 6;

		return target.y > 6 && target.x > 2 && target.x < 6;
	}

	@Override
	public boolean isValidMove(Points p, String board) {
		Pair dif = p.absDifference();
		if (dif.x == 1 && dif.y == 1 && !this.ownFigure(this.getFieldValue(p.e, board))
				&& this.isInBound(p.e, this.player))
			return true;
		return false;
	}

}
