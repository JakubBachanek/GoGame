public class Board {
    
    private Intersection[][] intersections;
    private int size;
    
    public Board(int size) {
        this.size = size;
        intersections = new Intersection[size][size];
        for(int i = 0; i < size; i++) {
            for(int k = 0; k < size; k++)   {
                intersections[i][k] = new Intersection();
            }
        }
    }
    
    public String getIntersectionState(int x, int y) {
        if(x < 0 || y < 0 || x > size - 1 || y > size - 1)
                return "error";
        return this.intersections[x][y].getState();
    }
    
    public void changeIntersectionState(int x, int y, String state) {
        this.intersections[x][y].changeState(state);
    }
    
    public String getIntersectionTerritory(int x, int y) {
        return this.intersections[x][y].getTerritory();
    }
    
    public void setIntersectionTerritory(int x, int y, String territory) {
        this.intersections[x][y].setTerritory(territory);
    }
}