import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class GameClient {
    private GUI gui = null;
    private final int serverPort = 7777;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private int boardSize = 0;
    private int opponentCapturedStones = 0;
    private int myCapturedStones = 0;
    private int oppCounts, iCount = 0;
    private boolean vBot = false;

    public GameClient() throws Exception {
        socket = new Socket("localhost", serverPort);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);

        gui = new GUI(out);
        play();
    }
    
    public GameClient(GUI gui) throws Exception {
        socket = new Socket("localhost", serverPort);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);

        this.gui = new GUI(out, gui);
        play();
    }

    public void play() throws Exception {
        try {
            // Waiting for response from server
            String response = in.nextLine();
            if(response.charAt(0) == 'E') {
            	socket.close();
            	return;
            }
            	
            char mark = response.charAt(8);
            char opponentMark = mark == 'B' ? 'W' : 'B';
            if(response.charAt(13) == '1') {
            	if(response.charAt(14) == '3') {
            		this.boardSize = 13;
            	} else {
            		this.boardSize = 19;
            	}
            } else {
            	this.boardSize = 9;
            }
            while(!(in.nextLine().equals("CREATING"))) {}
            out.println("CREATED");
           
            // waiting for further messages
            while (in.hasNextLine()) {
                response = in.nextLine();
                if (response.startsWith("VALID_MOVE")) {
                	
                    response = response.substring(11);
                    gui.setEnabledPass(false);
                    
                    oppCounts = 0;

                    StringTokenizer tmp = new StringTokenizer(response);

                    int x = Integer.parseInt(tmp.nextToken());
                    int y = Integer.parseInt(tmp.nextToken());
                    
                    gui.move(x, y, mark);
                    
                    x = boardSize - x;
                    char fileChar = (char) ('A' + y);
                    response = "VALID_MOVE " + x + " " + fileChar;
                    gui.addMessage(response);

                } else if (response.startsWith("OPPONENT_MOVED")) {
                	
                	if(iCount == 1) {
                		gui.addMessage("Opponent don't want to count the scores!");
                		iCount = 0;
                	}
                	if(response.charAt(14) == '_')
                		continue;
              
                    if(response.charAt(14) == ' ') {
                    	gui.setEnabledPass(true);
                    }
                    response = response.substring(15);
                    
                    StringTokenizer tmp = new StringTokenizer(response);

                    int x = Integer.parseInt(tmp.nextToken());
                    int y = Integer.parseInt(tmp.nextToken());
                    
                    gui.move(x, y, opponentMark);
                    
                    x = boardSize - x;
                    char fileChar = (char) ('A' + y);
                    response = "VALID_MOVE " + x + " " + fileChar;
                    gui.addMessage(response);

                } else if (response.startsWith("MESSAGE")) {
                	response = response.substring(8);
                	if(response.charAt(0) == '1') {
                		vBot = true;
                		gui.setEnabledCountThePoints(true);
                		gui.setEnabledSurrender(true);
                	} else if (response.charAt(0) == '0'){
                		gui.setEnabledCountThePoints(true);
                		gui.setEnabledSurrender(true);
                	} else if (response.charAt(0) == 'B'){
                		gui.setEnabledBoard(false);
                	} else if (response.charAt(0) == 'A'){
                		gui.setEnabledBoard(true);
                	} else if(response.charAt(0) == 'Y') {
                    	gui.setEnabledPass(true);
                    	gui.addMessage(response);
                	} else if(response.charAt(0) == 'N') {
                    	out.println("MOVE_");
                    	gui.addMessage(response);
                	} else if (response.charAt(0) == 'C') {
                		gui.addMessage(response);
                	}
                } else if (response.startsWith("REMOVE")) {
                    response = response.substring(6);

                    StringTokenizer tmp = new StringTokenizer(response);

                    int x = Integer.parseInt(tmp.nextToken());
                    int y = Integer.parseInt(tmp.nextToken());
                    String color = tmp.nextToken();
                    
                    if(color.charAt(0) == mark) {
                    	opponentCapturedStones++;
                    } else {
                    	myCapturedStones++;
                    }
                    gui.remove(x, y);
                } else if (response.startsWith("DEFEAT")) {
                    gui.addMessage("You lost!");
                    Thread.sleep(2000);
                    createNewPlayer(gui);
                    break;
                } else if (response.startsWith("OTHER_PLAYER_LEFT")) {
                    gui.addMessage(response);
                    break;
                } else if (response.startsWith("OPPONENT PASSED")) {
                    gui.addMessage(response);
                    gui.setEnabledPass(true);
                    gui.setOpponentPausedMode(1);
                } else if (response.startsWith("YOU PASSED")){
                	gui.changeColorOfCursor(mark);
                	gui.addMessage(response);
                	gui.setEnabledPass(false);
                	if(!(vBot))
                		gui.setPausedMode(1);
                } else if (response.startsWith("THE GAME IS PAUSED")) {
                	gui.setEnabledPass(false);
                	gui.setEnabledResume(true);
                	gui.addMessage(response);
                } else if (response.startsWith("YOU RESUMED THE GAME")) {
                	gui.setPausedMode(0);
                	gui.setEnabledResume(false);
                	gui.removeRectangles();
                	gui.addMessage(response);
                } else if (response.startsWith("THIS GAME WAS RESUMED")) {
                	gui.setPausedMode(0);
                	gui.removeRectangles();
                	gui.setEnabledPass(true);
                	gui.setEnabledResume(false);
                	gui.addMessage(response);
                } else if (response.startsWith("I COUNT SCORES")) {
                	if(vBot) {
                		gui.builtFinishingDialog(opponentCapturedStones, myCapturedStones, mark, opponentMark);
                		response = in.nextLine();
                		createNewPlayer(gui);
                		break;
                	} else {
	                	iCount = 1;
	                	if(oppCounts == 1) {
	                		gui.builtFinishingDialog(opponentCapturedStones, myCapturedStones, mark, opponentMark);
	                		response = in.nextLine();
	                		if(!(response.startsWith("FAIL"))) {
	                			createNewPlayer(gui);
	                			break;
	                		}
	                	}
                	}
                } else if (response.startsWith("OPPONENT COUNTS SCORES")) {
                	oppCounts = 1;
                	if(iCount == 1) {
                		gui.builtFinishingDialog(opponentCapturedStones, myCapturedStones, mark, opponentMark);
                		response = in.nextLine();
                		if(!(response.startsWith("FAIL"))) {
                			createNewPlayer(gui);
                			break;
                		}	
                	} else {
                		gui.addMessage("Opponent want to count the scores");
                	}
                } else if (response.startsWith("OPPONENT SURRENDERED")) {
                    gui.addMessage(response);
                    gui.addMessage("You won!");
                    Thread.sleep(2000);
                    createNewPlayer(gui);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close(); 
            gui.dispose();
            
        }
    }
    
    public void createNewPlayer(GUI gui) throws Exception {
    	new GameClient(gui);
    }

    public static void main(String[] args) throws Exception {
        new GameClient();
    }
}
