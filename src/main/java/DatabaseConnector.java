import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/** Offers functions for inserting and selecting data from database. */
public class DatabaseConnector {

	/** Inserts new game into games table. */
	public static int insertGame(int size) {
		 
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
         
        Game game = new Game();
        game.setSize(size);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = now.format(formatter);
        game.setDate(formatDateTime);
        
         
        session.save(game);
        session.getTransaction().commit();
        session.close();

        return game.getId();
    }
	
	/** Returns game object by specified id. */
	public static Game getGame(int id) {
		 
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        
        Game game = (Game) session.get(Game.class, id);
        
        session.getTransaction().commit();
        session.close();
        
        return game;
    }
	
	/** Returns list of games. */
	public static List<Game> getGames() {
		 
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        
        List<Game> games = session.createQuery("from Game").list();
        
        session.getTransaction().commit();
        session.close();
        
        return games;
    }
	
	/** Inserts new move into moves table. */
	public static void insertMove(int gameId, int moveNumber, int position, char player) {
		
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        
        Game game = (Game) session.get(Game.class, gameId);
        Move move = new Move(game, moveNumber, player, position);
        
        session.save(move);
        session.getTransaction().commit();
        session.close();
	}
	
	/** Returns list of moves. */
	public static List<Move> getMoves(int gameId) {
		
		String gameStr = Integer.toString(gameId);
		
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        
        List<Move> moves = session.createQuery("from Move where game_id = " + gameStr).list();
        
        session.getTransaction().commit();
        session.close();
        
        return moves;
    }
	
	public static void main(String[] args) {

	}
}
