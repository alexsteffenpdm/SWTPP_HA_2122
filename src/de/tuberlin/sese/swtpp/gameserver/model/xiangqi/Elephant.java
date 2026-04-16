package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

public class Elephant extends Figures {
	public Elephant(String p) {
		super(p, 'e');
	}

	@Override
	public boolean isValidMove(Points p, String board) {
		Pair dif = p.absDifference();
		Pair middle = this.meanField(p);
		char middleFieldValue = this.getFieldValue(middle, board);
		if (!Character.isDigit(middleFieldValue))
			return false;
		if (this.player.equals("red") && p.e.y > 4)
			return false;
		else if (this.player.equals("black") && p.e.y < 5)
			return false;
		boolean[] conditions = { dif.x == 2, dif.y == 2, !this.ownFigure(this.getFieldValue(p.e, board)) };
		if (conditions[0] && conditions[1] && conditions[2])
			return true;
		return false;
	}

	public Pair meanField(Points p) {
		Pair dif = new Pair((p.e.x - p.s.x) / 2, (p.e.y - p.s.y) / 2);

		return new Pair(p.s.x + dif.x, p.s.y + dif.y);
	}
}
