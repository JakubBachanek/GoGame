import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Bot {
	
	private int boardSize;

	Player opponent;
    Manager gameManager;
    String color;

	public Bot(String color) {
		this.color = color;
	}

    private boolean processMoveCommand(int x, int y) { 
    	if(gameManager.addStoneToChain(x, y, this.color)) {
    		if(gameManager.isIntersectionBlocked(this.color)) {
    			gameManager.deleteIntersectionBlockade();
            }
    		opponent.output.println("OPPONENT_MOVED " + x + " " + y);
    		return true;
        }
    	return false;
    }
	
	public void action() {
		try {
			TimeUnit.SECONDS.sleep(2);
    	} catch(InterruptedException ex) {
    	    Thread.currentThread().interrupt();
    	}
		while(true) {
				//sprawdza łancuch z najmniejsza iloscia oddechow
				int a = 0; //liczba oddechow
				int p = -1; //indeks lancucha
				for(int i = 0; i < getSizeOfChains(); i++) {
					if(!(getColorOfChain(i).equals(color))) {
						if(p == -1) {
							a = getCountLiberties(i);
							p = i;
						}
						if(getCountLiberties(i) < a) {
							a = getCountLiberties(i);
							p = i;
						}
					}
				}
			
				//sprawdza swoj wlasny lancuch z najmniejsza iloscia oddechow
				int b = 0;
				int q = -1;
				for(int i = 0; i < getSizeOfChains(); i++) {
					if(getColorOfChain(i).equals(color)) {
						if(q == -1) {
							b = getCountLiberties(i);
							q = i;
						}
						if(getCountLiberties(i) < b) {
							b = getCountLiberties(i);
							q = i;
						}
					}
				}
				
				if(a == 1) { //zabija lancuch wroga
					if(searchFreeSpace(p)) {
						break;
					}
				}
				
				if(b == 1) { //probuje ratowac swoj lancuch
					if(searchFreeSpace(q)) {
						break;
					}
				}
				
				Random r = new Random(); //gdy zadne z powyzszych to losuje czy powiekszac swoj lancuch, czy gdziekolwiek, czy kolo wroga
				int x = r.nextInt(4);
				if(x == 1 && q != -1) {
					if(searchFreeSpace(q)) {
						break;
					}
				} else if(x == 2) {
					if(randomChoice()) {
						break;
					}
				} else if (p != -1){
					if(searchFreeSpace(p)) {
						break;
					}
				}
		}
	}
	
	public boolean randomChoice() {
		int size = gameManager.getSizeOfBoard();
		Random r = new Random();
		int x = r.nextInt(size);
		int y = r.nextInt(size);
		if(processMoveCommand(x, y)) {
			return true;
		} else {
			return false;
		}
	}
	
	public int countLiberties(int x, int y) {
		int l = 0;
		if(gameManager.getIntersectionState(x + 1, y).equals("FREE")) {
			l++;
		}
		if(gameManager.getIntersectionState(x - 1, y).equals("FREE")) {
			l++;
		}
		if(gameManager.getIntersectionState(x, y + 1).equals("FREE")) {
			l++;
		}
		if(gameManager.getIntersectionState(x, y - 1).equals("FREE")) {
			l++;
		}
		return l;
	}
	
	public boolean searchFreeSpace(int a) {
		for(int i = 0; i < getNumberOfElements(a); i++) {
			int flag = 0;
			int x = getX(a, i);
			int y = getY(a, i);
			if(getColorOfChain(a).equals("BLACK")) {
				flag = 1; //gdy chcemy postawic kamien obok wroga, poniższe warunki z oddechami nie obowiazuja
			}
			if(gameManager.getIntersectionState(x + 1, y).equals("FREE")) {
				if(countLiberties(x + 1, y) > 2 || flag == 1) { // dodajac kamien ginie jeden oddech, wiec lancuch nadal bedzie mial tylko jeden oddech
					return processMoveCommand(x + 1, y);
				}
			}
			if(gameManager.getIntersectionState(x - 1, y).equals("FREE")) {
				if(countLiberties(x - 1, y) > 2 || flag == 1) {
					return processMoveCommand(x - 1, y);
				}
			}
			if(gameManager.getIntersectionState(x, y + 1).equals("FREE")) {
				if(countLiberties(x, y + 1) > 2 || flag == 1) {
					return processMoveCommand(x, y + 1);
				}
			}
			if(gameManager.getIntersectionState(x, y - 1).equals("FREE")) {
				if(countLiberties(x, y - 1) > 2 || flag == 1) {
					return processMoveCommand(x, y - 1);
				}
			}
			
		}
		return false;
	}

    public void setGameManager(Manager gameManager) {
        this.gameManager = gameManager;
    }

    public Player getOpponent() {
        return opponent;
    }

    public String getColor() {
        return color;
    }
    
    public void setBoardSize(int size) {
        this.boardSize = size;
    }
    
    public int getBoardSize() {
        return boardSize;
    }
    
    public int getSizeOfChains() {
    	return gameManager.getSizeOfChains();
    }
    
    public String getColorOfChain(int index) {
    	return gameManager.getColorOfChain(index);
    }
    
    public int getCountLiberties(int index) {
    	return gameManager.getCountLiberties(index);
    }
    
    public int getNumberOfElements(int index) {
    	return gameManager.getNumberOfElements(index);
    }
    
    public int getX(int a, int i) {
    	return gameManager.getX(a, i);
    }
    
    public int getY(int a, int i) {
    	return gameManager.getY(a, i);
    }
}
