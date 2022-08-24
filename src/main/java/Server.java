
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Server {
    ServerSocket serverSocket = null;
    private final int serverPort = 7777;
    static int tempint = 0;
    static int id;
    private static ArrayList<Player> players = new ArrayList<Player>();
    private static ExecutorService pool = Executors.newFixedThreadPool(512);
    
    private static Bot bot = null;

	public Server() {
		createSocket();
		run();
	}
	
	public void createSocket() {
	    try {
	        serverSocket = new ServerSocket(serverPort);
	    } catch (IOException e) {
	        System.out.println(e.getMessage());
	        System.exit(1);

	    }

	}

	public void run() {
	    while(true) {
	        try {
	            //Socket socket = serverSocket.accept();
	            pool.execute(new PlayerCreator(serverSocket.accept()));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}

	//// INNER CLASS PLAYERCREATOR
	
	private static class PlayerCreator implements Runnable {
	    private Socket socket;
	    
	    PlayerCreator(Socket socket) {
	        this.socket = socket;
	    }
	
	    public void run() {
	        System.out.println("Creating new player in server");
	        try {
	            setup(socket);
	            Server.findOpponent();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	
	    private void setup(Socket socket) throws IOException, Exception {
	        
	        Player newPlayer = null;
	
	        if(tempint == 0) {
	            newPlayer = new Player(socket, "BLACK");
	            tempint = 1;
	        }
	        else if(tempint == 1) {
	            newPlayer = new Player(socket, "WHITE");
	            tempint = 0;
	        }
	
	        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        String clientSettings = in.readLine();
	        if(clientSettings.charAt(0) == 'E') {
	        	PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
	        	output.println("EXIT");
	        	new GameClient();
	        	return;
	        }
	        	
	        clientSettings = clientSettings.substring(11);
	        StringTokenizer tmp = new StringTokenizer(clientSettings);
	
	        int boardSize = Integer.parseInt(tmp.nextToken());
	        int vsBot = Integer.parseInt(tmp.nextToken());
	        
	        if(vsBot > 0) {
	        	if(newPlayer.getColor().equals("BLACK")) {
	        		bot = new Bot("WHITE");
	        	} else {
	        		bot = new Bot("BLACK");
	        	}
	        	bot.setBoardSize(boardSize);
	        }
	        newPlayer.setBoardSize(boardSize);
	        newPlayer.setBotSetting(vsBot);
	        Server.players.add(newPlayer);
	
	        System.out.println("Player choice: " + boardSize + " " + vsBot);
	        pool.execute(newPlayer);
	    }
	}
	
	private static void findOpponent() throws IOException, InterruptedException {
	    int playerId9 = -1;
	    int playerId13 = -1;
	    int playerId19 = -1;
	    
	    synchronized(players) {
            for(int i = 0; i < players.size(); i++) {
                Player player = players.get(i);

                if(!player.getHasOpponent()) {
                    /*
                     *  Determine whether player wants to play with bot or against another player
                     */
                	int BoardSizeChoice = player.getBoardSize();
                    if(player.getWantBot()) {
                    	createNewGame(player, BoardSizeChoice, -1);
                    } else {
                        if(BoardSizeChoice == 9) {
                            if(playerId9 >= 0 && !(players.get(playerId9).getColor().equals(player.getColor()))) {
                                createNewGame(player, BoardSizeChoice, playerId9);
                                playerId9 = -1;
                            } else {
                                playerId9 = i;
                            }
                        } else if(BoardSizeChoice == 13) {
                            if(playerId13 >= 0 && !(players.get(playerId13).getColor().equals(player.getColor()))) {
                                createNewGame(player, BoardSizeChoice, playerId13);
                                playerId13 = -1;
                            } else {
                                playerId13 = i;
                            }
                        } else {
                            if(playerId19 >= 0 && !(players.get(playerId19).getColor().equals(player.getColor()))) {
                                createNewGame(player, BoardSizeChoice, playerId19);
                                playerId19 = -1;
                            } else {
                                playerId19 = i;
                            }
                        }
                    }
                }
            }
        }
	}

	private static void createNewGame(Player player, int BoardSize, int opponent) throws IOException, InterruptedException {
		id = DatabaseConnector.insertGame(BoardSize);
	    Thread.sleep(1000);
	    Manager gameManager;
        Player playerOpponent = null;
        if(opponent >= 0) {
        	 gameManager = new Manager(BoardSize, 0, id);
        	 playerOpponent = players.get(opponent);
        	 playerOpponent.setHasOpponent(true);
        	 playerOpponent.setGameManager(gameManager);
        } else {
        	gameManager = new Manager(BoardSize, 1, id);
        	bot.setGameManager(gameManager);
        	player.opp = bot;
        	bot.opponent = player;
        }
        player.setGameManager(gameManager);
        
        player.output.println("CREATING");
        if(opponent >= 0)
        	playerOpponent.output.println("CREATING");

        player.setHasOpponent(true);
        
        PrintWriter output = new PrintWriter(player.socket.getOutputStream(), true);
        if(opponent >= 0) {
        	PrintWriter output2 = new PrintWriter(playerOpponent.socket.getOutputStream(), true);
        	player.opponent = playerOpponent;
        	playerOpponent.opponent = player;
	        if (player.getColor().equals("BLACK")) {
	        	gameManager.setCurrentPlayer(player);
	            output.println("MESSAGE Connected with WHITE opponent");
	            output2.println("MESSAGE Connected with BLACK opponent");
	            output.println("MESSAGE Your move");
	        } else if (playerOpponent.getColor().equals("BLACK")) {
	            gameManager.setCurrentPlayer(playerOpponent);
	            output2.println("MESSAGE Connected with WHITE opponent");
	            output.println("MESSAGE Connected with BLACK opponent");
	            output2.println("MESSAGE Your move");
	        }
        } else {
        	gameManager.setPlayer(player);
        	if (player.getColor().equals("BLACK")) {
        		gameManager.setCurrentPlayer(player);
        		output.println("MESSAGE Connected with WHITE opponent");
        		output.println("MESSAGE Your move");
        	} else {
        		gameManager.setCurrentPlayer(null);
        		output.println("MESSAGE Connected with BLACK opponent");
        		bot.action();
        	}
        }
    }
	
    public static void main(String[] args)
    {
    	new Server();
    }
}
