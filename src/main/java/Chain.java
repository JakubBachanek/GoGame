import java.util.ArrayList;

public class Chain {

    private Manager manager;
    private ArrayList<Stone> listOfStones;
    private String color;
    private int eyes = 0;

    public Chain(Manager manager, String color, int x, int y) {
        this.manager = manager;
        this.color = color;
        listOfStones = new ArrayList<Stone>();
        addStone(x, y);
    }
    
    public void addStone(int x, int y) {
        listOfStones.add(new Stone(x, y));
    }

    public int countLiberties() {
        ArrayList<Liberty> liberties = new ArrayList<Liberty>();
        
        int x;
        int y;
        
        for(int i = 0; i < listOfStones.size(); i++) {
            
            x = listOfStones.get(i).getX();
            y = listOfStones.get(i).getY();
            
            if(manager.getIntersectionState(x + 1, y).equals("FREE")) {
                if(liberties.size() == 0) {
                    liberties.add(new Liberty(x + 1, y));
                } else {
                    for(int k = 0; k <= liberties.size(); k++) {
                        if(liberties.get(k).getX() == x + 1 && liberties.get(k).getY() == y) {
                            break;           
                        } else if(k == liberties.size() - 1) {
                            liberties.add(new Liberty(x + 1, y));
                        }
                    }
                }
            }
            
            if(manager.getIntersectionState(x - 1, y).equals("FREE")) {
                if(liberties.size() == 0) {
                    liberties.add(new Liberty(x - 1, y));
                } else {
                    for(int k = 0; k <= liberties.size(); k++) {
                        if(liberties.get(k).getX() == x - 1 && liberties.get(k).getY() == y) {
                            break;                 
                        } else if(k == liberties.size() - 1) {
                            liberties.add(new Liberty(x - 1, y));
                        }
                    }
                }
            }
            
            if(manager.getIntersectionState(x, y + 1).equals("FREE")) {
                if(liberties.size() == 0) {
                    liberties.add(new Liberty(x, y + 1));
                } else {
                    for(int k = 0; k <= liberties.size(); k++) {
                        if(liberties.get(k).getX() == x && liberties.get(k).getY() == y + 1) {
                            break;
                        } else if(k == liberties.size() - 1) {
                            liberties.add(new Liberty(x, y + 1));
                        }
                    }
                }
            }
            
            if(manager.getIntersectionState(x, y - 1).equals("FREE")) {
                if(liberties.size() == 0) {
                    liberties.add(new Liberty(x, y - 1));
                } else {
                    for(int k = 0; k <= liberties.size(); k++) {
                        if(liberties.get(k).getX() == x && liberties.get(k).getY() == y - 1) {
                            break;
                        } else if(k == liberties.size() - 1) {
                            liberties.add(new Liberty(x, y - 1));
                        }
                    }
                }
            }
        }
        return liberties.size();
    }
    
    
    public String getColor() {
        return color;
    }
    
    public boolean isHere(int x, int y) {
        for(int i = 0; i < listOfStones.size(); i++) {
            if(listOfStones.get(i).getX() == x && listOfStones.get(i).getY() == y) {
                return true;
            }       
        }
        return false;
    }
    
    public int getX(int counter) {
        return listOfStones.get(counter).getX();
    }
    
    public int getY(int counter) {
        return listOfStones.get(counter).getY();
    }
    
    public int numberOfElements() {
        return listOfStones.size();
    }
    
    public void setEyes(int i) {
        this.eyes = i;
    }
    
    public int getEyes() {
        return this.eyes;
    }
}
