package de.tuberlin.sese.swtpp.gameserver.model;

public class Rook extends Figures{
	public Rook(String p) {
		super(p, 's');
	}
	
	@Override
	public boolean isValidMove(Points p,String board){
		return false;
	}

}
