package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

public class Horse extends Figures {
	public Horse(String p) {
		super(p, 'h');
	}

	@Override
	public boolean isValidMove(Points p, String board) {
		Pair absDif = p.absDifference();
		Pair dif = p.difference();
		Pair start = p.s;

		if (absDif.x + absDif.y != 3 || this.ownFigure(this.getFieldValue(p.e, board)))
			return false;

		if (absDif.x > 1) {
			if (dif.x > 0) {
				Pair temp = new Pair(start.x + 1, start.y);
				// System.out.println( this.getFieldValue(temp, board) );
				return Character.isDigit(this.getFieldValue(temp, board));
			} else {
				Pair temp = new Pair(start.x - 1, start.y);
				// System.out.println( this.getFieldValue(temp, board) );
				return Character.isDigit(this.getFieldValue(temp, board));
			}
		} else {
			if (dif.y > 1) {
				Pair temp = new Pair(start.x, start.y + 1);
				// System.out.println( this.getFieldValue(temp, board) );
				return Character.isDigit(this.getFieldValue(temp, board));
			} else {
				Pair temp = new Pair(start.x, start.y - 1);
				// System.out.println( this.getFieldValue(temp, board) );
				return Character.isDigit(this.getFieldValue(temp, board));
			}
		}
	}

}
