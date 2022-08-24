
import java.io.PrintWriter;
import java.util.ArrayList;

public class Manager {

    private int size;
    private Board board;
    private int id;
    private int numberMove = 0;
    private ArrayList<Chain> chains;
    private Player currentPlayer;
    private int vBot = 0;
    private Player player;

    int x = -1;
    int y = -1;
    String color = null;

    public Manager(int size, int flag, int id) {
        this.size = size;
        this.board = new Board(size);
        this.chains = new ArrayList<Chain>();
        this.vBot = flag;
        this.id = id;
    }

    public void setPlayer(Player player) {
    	this.player = player; 
    }
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getSizeOfBoard() {
        return this.size;
    }

    public String getIntersectionState(int x, int y) {
        return board.getIntersectionState(x, y);
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void changeIntersectionState(int x, int y, String state) {
        board.changeIntersectionState(x, y, state);
    }

    public int getSizeOfChains() {
        return chains.size();
    }
    
    public String getColorOfChain(int index) {
    	return chains.get(index).getColor();
    }
    
    public int getCountLiberties(int index) {
    	return chains.get(index).countLiberties();
    }
    
    public int getNumberOfElements(int index) {
    	return chains.get(index).numberOfElements();
    }
    
    public int getX(int a, int i) {
    	return chains.get(a).getX(i);
    }
    
    public int getY(int a, int i) {
    	return chains.get(a).getY(i);
    }
    
    public void addMoveToDB(int x, int y, String color, int flag) {
    	numberMove++;
    	int position = y + x * size;
    	char charPlayer;
    	if(flag == 0) {
    		charPlayer = color.charAt(0);
    	} else {
    		charPlayer = color.charAt(1);
    	}
    	DatabaseConnector.insertMove(id, numberMove, position, charPlayer);
    }

    public boolean checkConditionToAddStone(int x, int y, String color, int counter) {

        // if there aren't friendly neighbor chains

        if(counter == 0) {
            if(board.getIntersectionState(x + 1, y).equals("FREE") || board.getIntersectionState(x - 1, y).equals("FREE") || 
                    board.getIntersectionState(x, y + 1).equals("FREE") || board.getIntersectionState(x, y - 1).equals("FREE")) {
                return true;
            } else {
                // coordinates to block in next turn
                int p = -1;
                int q = -1;

                String col = null;

                // number of neighboring enemy chains with one liberty
                int oneLiberty = 0;

                int oneStone = 0;

                for(int i = 0; i < chains.size(); i++) {
                    if(!(chains.get(i).getColor().equals(color))) {

                        // iteruje po wrogich lancuchach

                        if(chains.get(i).isHere(x + 1, y) || chains.get(i).isHere(x - 1, y) || 
                                chains.get(i).isHere(x, y + 1) || chains.get(i).isHere(x, y - 1)) {

                            // jezeli lancuch worgi jest w sasiedztwie

                            if(chains.get(i).countLiberties() == 1) {

                                // jezeli ma on jeden oddech 

                                oneLiberty++;
                                if(chains.get(i).numberOfElements() == 1) {

                                    // jezeli ten lancuch jest jednym oddechem

                                    oneStone++;
                                    p = chains.get(i).getX(0); //jest tylko jeden element w tym lancuchu,
                                    q = chains.get(i).getY(0); //gdy bedzie wiecej takich kamieni bedzie sie nadpisywaly parametry,
                                    col = chains.get(i).getColor(); //ale nas interesuje sytuacja, gdy jest tylko taki jeden kamien
                                }
                            }
                        }
                    }
                }

                // if the number of singletons with one liberty is 1                
                if(oneLiberty == 1 && oneStone == 1) {
                    this.x = p;
                    this.y = q;
                    this.color = col;
                    return true;
                } else if (oneLiberty != 0) {
                    return true;
                }
            }
        } else {

        // jezeli dookoła sa kamienie roznych kolorow to mozna wstawic, gdy chociaz jeden z przyjacielskich bedzie mial odechow wiecej niz 1

            for(int i = 0; i < chains.size(); i++) {
                if(chains.get(i).getColor().equals(color)) {
                    if(chains.get(i).isHere(x + 1, y) || chains.get(i).isHere(x - 1, y) || 
                            chains.get(i).isHere(x, y + 1) || chains.get(i).isHere(x, y - 1)) {

                        // jezeli jest przyjacielski i ma wiecej niz 1 oddech
                        if(chains.get(i).countLiberties() > 1 || (new Chain(this, color, x, y).countLiberties() > 0)) {
                            return true;
                        }
                    }
                }
            }

            //tutaj wszystkie przyjacielskie maja po jednym oddechu
            // jesli jeden z nieprzyjacielskich bedzie mial jeden oddech, to po dodaniu bedzie usuniety

            for(int i = 0; i < chains.size(); i++) {
                if(!(chains.get(i).getColor().equals(color))) {
                    if(chains.get(i).isHere(x + 1, y) || chains.get(i).isHere(x - 1, y) || 
                            chains.get(i).isHere(x, y + 1) || chains.get(i).isHere(x, y - 1)) {
                        if(chains.get(i).countLiberties() == 1) {
                            return true;
                        }
                    }
                }
            }
        }
        // gdy sa 4 przyjacielskie po jednym oddechu zawsze to jest samobojstwo
        return false;
    }

    public void deleteIntersectionBlockade() {
        this.x = -1;
        this.y = -1;
        this.color = null;
    }

    public boolean checkBlockedIntersection(int x, int y, String color) {
        if(this.x == x && this.y == y && this.color == color) {
                return true;
        }
        return false;
    }

    public boolean isIntersectionBlocked(String color) {
        if(x != -1 && this.color.equals(color)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean addStoneToChain(int x, int y, String color) {
        // Checking if intersection x y is blocked
        if(checkBlockedIntersection(x, y, color)) {
            return false;
        }

        // neighboring chains id's are indexes
        // counter counts neighboring chains

        int[] indexes = new int[4];
        int counter = 0;

        for(int i = chains.size() - 1; i >= 0; i--) {
            if(chains.get(i).getColor().equals(color)) {
                if(chains.get(i).isHere(x + 1, y) || chains.get(i).isHere(x - 1, y) || 
                        chains.get(i).isHere(x, y + 1) || chains.get(i).isHere(x, y - 1)) {
                    indexes[counter] = i;
                    counter++;
                }
            }
        }

        // checking conditions for adding stone

        boolean flag = checkConditionToAddStone(x, y, color, counter);

        if(counter == 0 && flag) {
            board.changeIntersectionState(x, y, color);
            addMoveToDB(x, y, color, 0);

            // searching for zero liberties and removing them
            findZeroLiberties();

            chains.add(new Chain(this, color, x, y));
            // Change current player
            if(vBot == 0) {
            	currentPlayer = currentPlayer.getOpponent();
            } else {
            	if(currentPlayer == null) {
            		currentPlayer = player;
            	} else {
            		currentPlayer = null;
            	}
            }
            return true;
        } else if(flag) {

            // connecting neighboring friendly chains into one

            Chain temp = chains.get(indexes[0]);
            for(int i = 1; i < counter; i++) {
                for(int k = 0; k < chains.get(indexes[i]).numberOfElements(); k++) {
                    int a = chains.get(indexes[i]).getX(k);
                    int b = chains.get(indexes[i]).getY(k);
                    temp.addStone(a, b);
                }
                chains.remove(indexes[i]);
            }
            temp.addStone(x, y);
            board.changeIntersectionState(x, y, color);
            addMoveToDB(x, y, color, 0);

            // searching for zero liberties and removing them
            findZeroLiberties();

            // Change current player
            if(vBot == 0) {
            	currentPlayer = currentPlayer.getOpponent();
            } else {
            	if(currentPlayer == null) {
            		currentPlayer = player;
            	} else {
            		currentPlayer = null;
            	}
            }
            return true;
        } else {
            return false;
        }
    }

    public void sendRemovedStones(int index) {
        PrintWriter out1 = null;
        PrintWriter out2 = null;
        if(vBot == 0) {
        	out1 = currentPlayer.output;
        	out2 = currentPlayer.opponent.output;
        } else {
        	out1 = player.output;
        }

        for(int i = 0; i < chains.get(index).numberOfElements(); i++) {
            int a = chains.get(index).getX(i);
            int b = chains.get(index).getY(i);
            out1.println("REMOVE" + a + " " + b + " " + chains.get(index).getColor());
            if(vBot == 0)
            	out2.println("REMOVE" + a + " " + b + " " + chains.get(index).getColor());
        }
    }

    public void findZeroLiberties() {
        for(int i = chains.size() - 1; i >= 0; i--) {
            if(chains.get(i).countLiberties() == 0) {
                sendRemovedStones(i);
                removeChain(i);
            }
        }
    }

    public void removeChain(int index) {
        for(int i = 0; i < chains.get(index).numberOfElements(); i++) {
        	String color = chains.get(index).getColor();
            int a = chains.get(index).getX(i);
            int b = chains.get(index).getY(i);
            board.changeIntersectionState(a, b, "FREE");
            addMoveToDB(a, b, color, 1);
        }
        chains.remove(index);
    }
}
