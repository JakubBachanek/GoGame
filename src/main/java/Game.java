import java.sql.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/*****************************
 * games
 * INT id
 * INT size
 * DATE date
 *****************************/

@Entity
@Table(name="games")
public class Game {
	
	@Id
    @GeneratedValue
    @Column(name="id")
    private int id;
    

    @Column(name="date")
    private String date;
    
    @Column(name="size")
    private int size;
    
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "game", orphanRemoval = true)
    private List<Move> moves;
	
    public Game(int size, String date) {
		super();
		this.size = size;
		this.date = date;
	}

	public Game() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
    public List<Move> getMoves() {
		return moves;
	}

	public void setMoves(List<Move> moves) {
		this.moves = moves;
	}
	
	public String getIdStr() {
		return Integer.toString(id);
	}
	
	public String getSizeStr() {
		return Integer.toString(size);
	}
}

