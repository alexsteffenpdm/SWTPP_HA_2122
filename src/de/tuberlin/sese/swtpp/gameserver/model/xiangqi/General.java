package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

public class General extends Figures {
	public Pair opG;

	public General(String p, Pair gen) {
		super(p, 'g');
		this.opG = gen;

	}

	private boolean isInBound(Pair target) {
		if (this.player == "red") // Red
			return target.y < 3 && target.x > 2 && target.x < 6;
		// Black
		return target.y > 6 && target.x > 2 && target.x < 6;
	}

	private Pair minmax(int left, int right) {
		return left < right ? new Pair(left, right) : new Pair(right, left);
	}

	@Override
	public boolean isValidMove(Points p, String board) {

		if (p.e.x == this.opG.x) {
			Pair minmax = minmax(p.e.y, this.opG.y);
			if (minmax.x == minmax.y)
				return false;
			String col = this.getBoardCollumn(board, this.opG.x);
			col = col.substring(minmax.x + 1, minmax.y);
			String colCpy = col.replaceAll("1", "");
			if (colCpy.length() == 0)
				return false;
		}
		Pair dif = p.absDifference();
		if (dif.x + dif.y != 1 && this.ownFigure(this.getFieldValue(p.e, board)))
			return false;
		else
			return this.isInBound(p.e);
	}
}
