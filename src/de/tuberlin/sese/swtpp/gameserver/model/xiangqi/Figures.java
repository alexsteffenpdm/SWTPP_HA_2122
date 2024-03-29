package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.util.*;

public class Figures {
	String player;
	Character identifier;
	public Figures(String p,char i){
		this.player = p;
		if(this.player.equals("black"))
		{
			Character.toUpperCase(i);
		}
		this.identifier = i;
	}
	
	public boolean charIsFigure(char val) {
		char[] figChars = {'g','a','e','h','r','c','s'};
		return Arrays.asList(figChars).contains(Character.toLowerCase(val));
	}
	
	public String getBoardCollumn(String board, int col)
	{
		String[] rows = board.split("/");
		char[] column = new char[rows.length];
		int counter = 0;
		for(String r:rows){
			column[counter] = (this.expandRow(new StringBuilder(r))).charAt(col);
			counter++;
		}		
		return String.valueOf(column);
	}
	
	public String getPlayer() {return this.player;	}
	
	public Character getIdentifier(){ return this.identifier;}
	
	public boolean isValidMove(Points p,String board)
	{
		return false;
	}
	
	public String applyMove(Points p, String board)
	{		
	  String[] rows = board.split("/");
	  if(p.s.y == p.e.y){
		  StringBuilder diffRow = new StringBuilder(expandRow(new StringBuilder(rows[9-p.e.y])));
		  char stone = diffRow.charAt(p.s.x);
		  diffRow.setCharAt(p.s.x,'1');
		  diffRow.setCharAt(p.e.x,stone);
		  rows[9-p.e.y] = collapseRow(new StringBuilder(diffRow)).toString();
	  }else{
		  StringBuilder startRow = new StringBuilder(expandRow(new StringBuilder(rows[9-p.s.y])));
		  StringBuilder endRow = new StringBuilder(expandRow(new StringBuilder(rows[9-p.e.y])));
		  char stone = startRow.charAt(p.s.x);
		  startRow.setCharAt(p.s.x,'1');
		  endRow.setCharAt(p.e.x,stone);
		  rows[9-p.s.y] = collapseRow(new StringBuilder(startRow)).toString();
		  rows[9-p.e.y] = collapseRow(new StringBuilder(endRow)).toString();
	  }
	  String returnBoard =String.join("/",rows);
	  return returnBoard;
	}

	public String expandRow(StringBuilder row)
    {
    StringBuilder ret = new StringBuilder();
     for(char c:row.toString().toCharArray())
      {
          int empty = c-48;
          if(empty> 0 && empty <= 9)
          {
              for(int i=0;i<empty;i++)
              {
                  ret.append('1');
              }
          }
          else
          {
              ret.append(c);
          }
      }
	return ret.toString();
    }
    
    public String collapseRow(StringBuilder row)
    {
    StringBuilder ret = new StringBuilder();
    int count = 0;
     for(int i=0;i<row.length();i++)
     {
        char c = row.charAt(i);
          int empty = c-48;
          if(empty> 0 && empty <= 9)
          {
              count++;
          }
          else
          {
              char countC = (char)(count+48);
              if (count > 0)
              {
                ret.append(countC);    
              }
              ret.append(c);
              count = 0;
          }
      }
      if(count != 0)
      {
        char countC = (char)(count+48);
        ret.append(countC);  
      }
	return ret.toString();
    }
    
    public boolean ownFigure(char fig) {
    	if(!Character.isDigit(fig)){
    		if(Character.isLowerCase(this.identifier)){
        		if(Character.isLowerCase(fig))return true;
        	}else{if(Character.isUpperCase(fig))return true;}}
    	return false;
    }
    
    public char getFieldValue(Pair p,String board)
    {	
    	return this.expandRow(new StringBuilder(board.split("/")[9-p.y])).charAt(p.x);
    }
}
