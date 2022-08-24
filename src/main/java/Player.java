
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Player implements Runnable {
	
	String color;
	Socket socket;
	private int boardSize;
	private boolean wantBot;
    private boolean hasOpponent;
    private int flag = 0;
    Player opponent = null;
    Bot opp;

    Manager gameManager;

    Scanner input;
    PrintWriter output;
    
    public Player(Socket socket, String color) {
        this.socket = socket;
        this.color = color;
    }

    public void run() {
        try {
            setup();
            processCommands();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setup() throws IOException, InterruptedException {
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);

        output.println("WELCOME " + color + boardSize);  
        System.out.println("WELCOME " + color + boardSize);
        while(!(input.nextLine().equals("CREATED"))) {}
        
        if(opp != null) {
        	output.println("MESSAGE 1");
        } else {
        	output.println("MESSAGE 0");
        }
        
    }


    private void processCommands() {
        while (input.hasNextLine()) {
            String command = input.nextLine();

            if (command.startsWith("MOVE")) {
            	if(command.charAt(4) == '_') {
                	opponent.output.println("OPPONENT_MOVED_");
                	continue;
            	}
                command = command.substring(4);
                StringTokenizer tmp = new StringTokenizer(command);
                int x = Integer.parseInt(tmp.nextToken());
                int y = Integer.parseInt(tmp.nextToken());

                processMoveCommand(x, y);
            } else if (command.startsWith("QUIT")) {
                output.println("OTHER_PLAYER_LEFT");
                return;
            } else if (command.startsWith("PASS")) {
                if(gameManager.isIntersectionBlocked(this.color)) {
                    gameManager.deleteIntersectionBlockade();
                }
                output.println("YOU PASSED");
                gameManager.setCurrentPlayer(opponent);
                if(!(wantBot)) {
                	opponent.output.println("OPPONENT PASSED");
                } else {
                	output.println("MESSAGE BEFORE");
                	opp.action();
                	output.println("MESSAGE AFTER");
                }
            } else if (command.startsWith("THE GAME IS PAUSED")) {
            	flag = 1;
            	opponent.setFlag(1);
            	output.println("THE GAME IS PAUSED");
            	opponent.output.println("THE GAME IS PAUSED");
            } else if (command.startsWith("RESUME")) {
            	flag = 0;
            	opponent.setFlag(0);
            	output.println("YOU RESUMED THE GAME");
            	opponent.output.println("THIS GAME WAS RESUMED");
            } else if (command.startsWith("COUNT")) {
            	output.println("I COUNT SCORES");
            	if(!(wantBot))
            		opponent.output.println("OPPONENT COUNTS SCORES");
            } else if (command.startsWith("ERROR")) {
            	output.println("FAIL");
            	opponent.output.println("FAIL");
            } else if (command.startsWith("WITHOUT_ERROR")) {
            	output.println("WITHOUT_FAIL");
            	if(!(wantBot))
            		opponent.output.println("WITHOUT_FAIL");
            } else if (command.startsWith("SURRENDER")) {
                output.println("DEFEAT");
                if(!(wantBot))
                	opponent.output.println("OPPONENT SURRENDERED ");
                return;
            }
        }
    }

    private void processMoveCommand(int x, int y) {
        try {
        	if(flag == 0) {
	            if (this != gameManager.getCurrentPlayer()) {
	                throw new IllegalStateException("Not your turn");
	            }
            	if(gameManager.addStoneToChain(x, y, this.color)) {
            		if(gameManager.isIntersectionBlocked(this.color)) {
            			gameManager.deleteIntersectionBlockade();
            		}
            		output.println("VALID_MOVE " + x + " " + y);
            		if(!(wantBot)) {
            			opponent.output.println("OPPONENT_MOVED " + x + " " + y);
            		} else {
            			output.println("MESSAGE BEFORE");
                    	opp.action();
                    	output.println("MESSAGE AFTER");
            		}
            	}
            } else {
            	gameManager.setCurrentPlayer(opponent);
            	output.println("VALID_MOVE " + x + " " + y);
        		opponent.output.println("OPPONENT_MOVED1" + x + " " + y); // (1) flag not to enable button pass
            }
        } catch (IllegalStateException e) {
            output.println("MESSAGE " + e.getMessage());
        }
    }


    public int getBoardSize() {
        return boardSize;
    }

    public boolean getWantBot() {
        return wantBot;
    }

    public boolean getHasOpponent() {
        return hasOpponent;
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

    public void setBotSetting(int wantBot) {
        if(wantBot > 0) {
            this.wantBot = true;
        } else {
            this.wantBot = false;
        }
    }

    public void setGameManager(Manager gameManager) {
        this.gameManager = gameManager;
    }
    
    public void setHasOpponent(boolean a) {
        this.hasOpponent = a;
    }
    
    public void setFlag(int a) {
    	flag = a;
    }
}
