package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

public class Rook extends Figures {
	public Rook(String p) {
		super(p, 'r');
	}

	private Pair minmax(int left, int right) {
		return left < right ? new Pair(left, right) : new Pair(right, left);
	}

	@Override
	public boolean isValidMove(Points p, String board) {
		String line = "";
		char targetField = this.getFieldValue(p.e, board);
		Pair dif = p.absDifference();
		Pair subRange = new Pair(0, 0);
		if (!(dif.x == 0 ^ dif.y == 0) || this.ownFigure(targetField))
			return false;
		if (dif.y == 0) {
			subRange = minmax(p.s.x, p.e.x);
			line = this.expandRow(new StringBuilder(board.split("/")[9 - p.e.y])).toString();
		} else {
			subRange = minmax(p.s.y, p.e.y);
			line = this.getBoardCollumn(board, p.s.x);
		}
		line = line.substring(subRange.x, subRange.y + 1);
		int figuresOnSubrange = line.replaceAll("1", "").length();

		if (this.charIsFigure(targetField)) {
			return figuresOnSubrange == 2;
		} // own figure + target figure
		else {
			return figuresOnSubrange == 1;
		} // own figure

	}
}
