
public class Intersection {
    private String state = "FREE";
    private String territory = null;
    
    public void changeState(String state) {
        if(state.equals("FREE")) {
            this.state = state;
        } else if(state.equals("BLACK")) {
            this.state = state;
        } else if(state.equals("WHITE")) {
            this.state = state;
        }
    }
    
    public String getState() {
        return this.state;
    }
    
    public void setTerritory(String territory) {
        this.territory = territory;
    }
    
    public String getTerritory() {
        return this.territory;
    }
    
}
