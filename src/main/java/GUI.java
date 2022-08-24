import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

public class GUI extends JFrame {
    private int size;
    private int i = 0;
    private int whiteScores = 0;
	private int blackScores = 0;
    private JButton player;
    private JButton bot;
    private JDialog history;
    private JDialog choice;
    private JDialog welcome;
    private GUI gui;
    private BoardGUI boardGUI;
    private JButton resume;
    private JButton pass;
    private JButton countThePoints;
    private JButton surrender;
    private JScrollPane scroll;
    private JPanel container;
    private JPanel buttonPanel;
    JTextArea area;
    PrintWriter out;

    public GUI(final PrintWriter out) {
        this.out = out;
        setup();
    }
    
    public GUI(final PrintWriter out, GUI gui) {
    	this.out = out;
    	this.gui = gui;
    	setup();
    }
    
    public void setup() {
    	
    	createDialogWelcome();
        createDialogChoice();
        createDialogHistory();

        setTitle("Go Game");
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                out.println("QUIT");
                System.exit(1);
            }
        });
        container = new JPanel();
        container.setBackground(Color.GRAY);
        container.setLayout(new BorderLayout());

        container.setBorder(new EmptyBorder(0, 20, 20, 20));

        buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.GRAY);

        pass = new JButton("pass");
        pass.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println("PASS");
            }
        });
        resume = new JButton("resume");
        resume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	out.println("RESUME");
            }
        });

        surrender = new JButton("surrender");
        surrender.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println("SURRENDER");
            }
        });
        countThePoints = new JButton("count the points");
        countThePoints.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	out.println("COUNT");
            }
        });
        
        pass.setEnabled(false);
        resume.setEnabled(false);
        surrender.setEnabled(false);
        countThePoints.setEnabled(false);
        boardGUI = new BoardGUI(out);
        container.add(boardGUI, BorderLayout.CENTER);

        buttonPanel.add(pass);
        buttonPanel.add(resume);
        buttonPanel.add(surrender);
        buttonPanel.add(countThePoints);
        container.add(buttonPanel, BorderLayout.NORTH);

        area = new JTextArea(15, 15);
        area.setEditable(false);
        area.setFont(new Font("Segoe Script", Font.BOLD, 15));
        area.setLineWrap(true);
        area.setText("The course of the game:\nWaiting for an opponent...\n");
        scroll = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(container);
        setResizable(false);
        if(gui != null)
        	gui.dispose();
        welcome.setVisible(true);
        
    }
    
    public void createDialogWelcome() {
    	welcome = new JDialog(this, "Welcome!", true);
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JButton play = new JButton("Play a new game now!");
        play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	welcome.dispose();
                choice.setVisible(true);
            }
        });
        JButton gameHistory = new JButton("Display the game history");
        gameHistory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	welcome.dispose();
                history.setVisible(true);
            }
        });
        panel.add(play);
        panel.add(gameHistory);
        welcome.add(panel);
        welcome.setSize(500, 150);
        if(gui != null) {
        	welcome.setLocationRelativeTo(gui);
        } else {
        	welcome.setLocationRelativeTo(null);
        } 
    }
    
    public void createDialogHistory() {
    	history = new JDialog(this, "The game history", true);
    	List<Game> games = DatabaseConnector.getGames();
    	size = games.size();
    	JPanel panel = new JPanel(new GridLayout(0, 1, 20, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        for(int i = 0; i < size; i++) {
        	String name = "Number: ";
        	name += games.get(i).getIdStr();
            name += " Size of the board: ";
            name += games.get(i).getSizeStr();
            name += " Date: ";;  
            name += games.get(i).getDate().substring(0, 19);
        	JButton play = new JButton(name);
        	final int k = i;
	        play.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	history.dispose();
	            	displayGame(k+1);
	            }
	        });
	        panel.add(play);
        }
        JScrollPane scroll = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        history.add(scroll);
        history.setSize(500, 500);
        history.setLocationRelativeTo(gui);
    }

    public void createDialogChoice() {
    	choice = new JDialog(this, "The first step", true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("Choose an opponent", JLabel.CENTER), BorderLayout.NORTH);
        JPanel choosingGrid = new JPanel(new FlowLayout());
        panel.add(choosingGrid, BorderLayout.CENTER);
        JPanel choosingPlayer = new JPanel(new FlowLayout());
        panel.add(choosingPlayer, BorderLayout.SOUTH);

        player = new JButton("Player");
        player.setEnabled(false);
        player.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action("player");
            }
        });
        bot = new JButton("Bot");
        bot.setEnabled(false);
        bot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action("bot");
            }
        });

        choosingPlayer.add(player);
        choosingPlayer.add(bot);

        JButton grid9 = new JButton("9x9");
        grid9.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action("9x9");
            }
        });

        JButton grid13 = new JButton("13x13");
        grid13.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action("13x13");
            }
        });

        JButton grid19 = new JButton("19x19");
        grid19.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action("19x19");
            }
        });

        choosingGrid.add(grid9);
        choosingGrid.add(grid13);
        choosingGrid.add(grid19);
        
        choice.add(panel);
        choice.setSize(300, 170);
        choice.setLocationRelativeTo(gui);
    }
    
    public void action(String name) {
        if(name.equals("bot")) {
            dispose();
            out.println("PREFERENCES" + size + " " + 1);

            boardGUI.initially(size);
            boardGUI.addMouseListener();
            container.add(scroll, BorderLayout.EAST);
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        } else if(name.equals("player")) {
            dispose();
            out.println("PREFERENCES" + size + " " + 0);
            container.add(scroll, BorderLayout.EAST);
            boardGUI.initially(size);
            boardGUI.addMouseListener();   
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        } else {
            player.setEnabled(true);
            bot.setEnabled(true);
            if(name.equals("9x9")) {
                this.size = 9;
            } else if(name.equals("13x13")) {
                this.size = 13;
            } else {
                this.size = 19;
            }
        }
    }
    
    public void displayGame(int id) {
    	Game gam = DatabaseConnector.getGame(id);
    	size = gam.getSize();
    	List<Move> listOfMoves = DatabaseConnector.getGame(id).getMoves();
    	int sizeOfMoves = listOfMoves.size();
    	final ArrayList<Integer> pointX = new ArrayList<Integer>();
    	final ArrayList<Integer> pointY = new ArrayList<Integer>();
    	final ArrayList<Character> marks = new ArrayList<Character>();
    	for(int k = 0; k < sizeOfMoves; k++) {
    		Move move = listOfMoves.get(k);
    		Game game = move.getGame();
    		if(game.getId() == id) {
    			int position = move.getPosition();
    			int x = position / size;
    			int y = position % size;
    			char mark = move.getPlayer();
    			pointX.add(x);
    			pointY.add(y);
    			marks.add(mark);
    		}
    	}
    	boardGUI.initially(size);
    	final JButton next = new JButton("Next move");
    	if(pointX.size() == 0)
    		next.setEnabled(false);
    	next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(marks.get(i) == 'L' || marks.get(i) == 'H') {
            		int p = 0;
            		while(marks.get(i) == 'L' || marks.get(i) == 'H') {
            			i++;
            			p++;
            		}
            		while(p > 0) {
            			nextMove(pointX.get(i - p), pointY.get(i - p), marks.get(i - p));
            			p--;
            		}
            	} else {
	            	nextMove(pointX.get(i), pointY.get(i), marks.get(i));
	            	i++;
	            	if(i == pointX.size()) {
	            		next.setEnabled(false);
	            	}
            	}
            }
        });
    	final JButton end = new JButton("The end");
    	end.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	for(int q = i; q < pointX.size(); q++) {
            		nextMove(pointX.get(q), pointY.get(q), marks.get(q));
            	}
            	createFinishingDialog(whiteScores, blackScores);
            }
        });
    	buttonPanel.remove(pass);
    	buttonPanel.remove(resume);
    	buttonPanel.remove(surrender);
    	buttonPanel.remove(countThePoints);
    	buttonPanel.add(next);
    	buttonPanel.add(end);
    	pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public void nextMove(int x, int y, char mark) {
    	if(mark == 'B' || mark == 'W') {
			boardGUI.move(x, y, mark);
		} else if (mark == 'L'){
			boardGUI.remove(x, y);
			whiteScores++;
		} else {
			boardGUI.remove(x, y);
			blackScores++;
		}
    }

    public void setEnabledPass(boolean flag) {
    	pass.setEnabled(flag);
    }
    
    public void setEnabledResume(boolean flag) {
    	resume.setEnabled(flag);
    }
    
    public void setEnabledSurrender(boolean flag) {
    	surrender.setEnabled(flag);
    }
    
    public void setEnabledCountThePoints(boolean flag) {
    	countThePoints.setEnabled(flag);
    }
    
    public void move(int x, int y, char mark) {
        boardGUI.move(x, y, mark);
    }
    
    public void setPausedMode(int i) {
    	boardGUI.setPausedMode(i);
    }
    
    public void setOpponentPausedMode(int i) {
    	boardGUI.setOpponentPausedMode(i);
    }

    public void remove(int x, int y) {
        boardGUI.remove(x, y);
    }
    
    public void removeRectangles() {
    	boardGUI.removeRectangles();
    }
    public void addMessage(String message) {
        this.area.append(message + "\n");
    }
    
    public void setEnabledBoard(boolean flag) {
    	boardGUI.setEnabledBoard(flag);
    }
    
    public void changeColorOfCursor(char mark) {
    	boardGUI.changeColorOfCursor(mark);
    }
    
    public void builtFinishingDialog(int opponentCapturedStones, int myCapturedStones, char mark, char opponentMark) {
    	int opponentRectangles = boardGUI.countRectangles(opponentMark);
    	if(opponentRectangles == -1) {
    		addMessage("There must be no red rectangle to finish game!");
    		out.println("ERROR");
    		return;
    	}
    	out.println("WITHOUT_ERROR");
    	int myRectangles = boardGUI.countRectangles(mark);
    	int opponentScores = opponentCapturedStones + opponentRectangles;
    	int myScores = myCapturedStones + myRectangles;
    	String name;
    	if(mark == 'B') {
    		name = "Black ";
    	} else {
    		name = "White ";
    	}
    	if(myScores > opponentScores) {
    		name += "is the winner!";
    	} else if (myScores < opponentScores) {
    		name += "is the loser!";
    	} else {
    		name = "The game ended in a draw";
    	}
    	JDialog dialog = new JDialog(this, name, true);
    	JPanel panel = new JPanel(new GridLayout(6, 1, 20, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("My scores: " + myScores, JLabel.CENTER));
        panel.add(new JLabel("Opponent's scores: " + opponentScores, JLabel.CENTER));
        panel.add(new JLabel("My captured stones: " + myCapturedStones, JLabel.CENTER));
        panel.add(new JLabel("Opponent's captured stones: " + opponentCapturedStones, JLabel.CENTER));
        panel.add(new JLabel("My rectangles: " + myRectangles, JLabel.CENTER));
        panel.add(new JLabel("Opponent's rectangles: " + opponentRectangles, JLabel.CENTER));
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(300, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    public void createFinishingDialog(int whiteScores, int blackScores) {
    	String name;
    	if(whiteScores > blackScores) {
    		name = "White is the winner!";
    	} else if (whiteScores < blackScores) {
    		name = "Black is the winner!";
    	} else {
    		name = "The game ended in a draw!";
    	}
    	JDialog dialog = new JDialog(this, name, true);
    	JPanel panel = new JPanel(new GridLayout(2, 1, 20, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("White scores: " + whiteScores, JLabel.CENTER));
        panel.add(new JLabel("Black scores: " + blackScores, JLabel.CENTER));
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dispose();
        out.println("EXIT");
    }
}
