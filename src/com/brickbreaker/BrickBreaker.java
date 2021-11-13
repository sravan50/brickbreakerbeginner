package com.brickbreaker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BrickBreaker extends JPanel implements KeyListener, ActionListener, Runnable {
	private static final long serialVersionUID = 1L;

	private static final int WINDOWS_WIDTH = 350;
	private static final int WINDOWS_HEIGHT = 450;
	private static final int CONTROL_LAYOUT_HEIGHT = 200;
	private static final int BALL_SIZE = 10;

	private static final Dimension WINDOW_DIMINSIONS = new Dimension(WINDOWS_WIDTH, WINDOWS_HEIGHT);
	private static final Dimension CANVAS_DIMINSIONS = new Dimension(WINDOWS_WIDTH,
			WINDOW_DIMINSIONS.height - CONTROL_LAYOUT_HEIGHT);
	private static final Rectangle CONTROL_LAYOUT_DIMINSIONS = new Rectangle(0, CANVAS_DIMINSIONS.height, WINDOWS_WIDTH,
			CONTROL_LAYOUT_HEIGHT);
	private static boolean right = false;
	private static boolean left = false;
	private static final int BRICK_BREADTH = 30;
	private static final int BRICK_HEIGHT = 20;
	private static final int BAT_HEIGHT = 5;
	private static final int BAT_WIDTH = 40;

	private static boolean PAUSE = false;
	private static boolean RUNNING = false;

	private static int movex = -1;
	private static int movey = -1;
	private static final int BAT_SPEED = 3;

	private boolean ballFallDown = false;
	private boolean bricksOver = false;
	private static int count = 0;

	private Rectangle Ball;
	private Rectangle Bat;
	private Rectangle[] Brick;
	private Rectangle gameScreen;
	private String status;
	private JButton button;

	private static enum STATUS {
		START, PAUSE, RESUME, STOP
	}

	BrickBreaker() {
		initializeVariables();
		JFrame frame = new JFrame();
		button = new JButton(STATUS.START.name());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// using this as parent layout exists (JFrame)
		this.setPreferredSize(WINDOW_DIMINSIONS);
		frame.getContentPane().add(this);
		frame.pack();
		frame.add(button, BorderLayout.SOUTH);
		frame.setVisible(true);
		button.addActionListener(this);

		this.addKeyListener(this);
		this.setFocusable(true);
		button.setFocusable(false);
	}

	public static void main(String[] args) {
		new BrickBreaker();

	}

	public void paint(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(gameScreen.x, gameScreen.y, gameScreen.width, gameScreen.height);

		g.setColor(Color.blue);
		g.fillOval(Ball.x, Ball.y, Ball.width, Ball.height);
		g.setColor(Color.green);
		g.fill3DRect(Bat.x, Bat.y, Bat.width, Bat.height, true);
		// this will paint below the peddle
		g.setColor(Color.GRAY);
		g.fillRect(CONTROL_LAYOUT_DIMINSIONS.x, CONTROL_LAYOUT_DIMINSIONS.y, CONTROL_LAYOUT_DIMINSIONS.width,
				CONTROL_LAYOUT_DIMINSIONS.height);
		// this will draw border line
		g.setColor(Color.RED);
		g.drawRect(gameScreen.x, gameScreen.y, gameScreen.width - 1, gameScreen.height);

		for (int i = 0; i < Brick.length; i++) {
			if (Brick[i] != null) {
				g.fill3DRect(Brick[i].x, Brick[i].y, Brick[i].width, Brick[i].height, true);
			}
		}

		if (ballFallDown == true || bricksOver == true) {
			Font f = new Font("Arial", Font.BOLD, 20);
			g.setFont(f);
			g.drawString(status, 70, 140);
			ballFallDown = false;
			bricksOver = false;
		}

	}

	// Game Loop
	public void run() {
		while (RUNNING) {
			if (PAUSE) {
				sleep();
				continue;
			}

			for (int i = 0; i < Brick.length; i++) {
				if (Brick[i] != null) {
					if (Brick[i].intersects(Ball)) {
						Brick[i] = null;
						// movex = -movex;
						movey = -movey;
						count++;
					} // end of 2nd if..
				} // end of 1st if..
			} // end of for loop..

			if (count == Brick.length) {// check if ball hits all bricks
				bricksOver = true;
				status = "YOU WON THE GAME";
				repaint();
			}
			repaint();
			Ball.x += movex;
			Ball.y += movey;

			if (left == true && Bat.x >= gameScreen.x) {
				Bat.x -= BAT_SPEED;
				right = false;
			}
			if (right == true && Bat.x <= gameScreen.width - Bat.width) {
				Bat.x += BAT_SPEED;
				left = false;
			}
			// /===== Ball reverses when strikes the bat
			if (Ball.intersects(Bat)) {
				movey = -movey;
			}
			// ball reverses when touches left and right boundary
			if (Ball.x <= gameScreen.x || gameScreen.width - Ball.width <= Ball.x) {
				movex = -movex;
			} // if ends here
			if (Ball.y <= gameScreen.y) {
				movey = -movey;
			} // if ends here.....
			if (Ball.y >= gameScreen.height) {// when ball falls below bat game is over...
				ballFallDown = true;
				status = "YOU LOST THE GAME";
				repaint();
				button.setText(STATUS.START.toString());
				break;
			}

			sleep();
		} // while loop ends here

	}

	private void sleep() {
		try {
			Thread.sleep(10);
		} catch (Exception ex) {
		} // try catch ends here
	}

	// HANDLING KEY EVENTS
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT) {
			left = true;
			// System.out.print("left");
		}

		if (keyCode == KeyEvent.VK_RIGHT) {
			right = true;
			// System.out.print("right");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT) {
			left = false;
		}

		if (keyCode == KeyEvent.VK_RIGHT) {
			right = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String str = e.getActionCommand();
		if (str.equals(STATUS.START.toString())) {
			button.setText(STATUS.PAUSE.toString());
			this.startGame();
		}
		if (str.equals(STATUS.PAUSE.toString())) {
			PAUSE = true;
			button.setText(STATUS.RESUME.toString());
		}
		if (str.equals(STATUS.RESUME.toString())) {
			PAUSE = false;
			button.setText(STATUS.PAUSE.toString());
		}

//		if (str.equals(STATUS.STOP.toString())) {
//			RUNNING = false;
//			button.setText(STATUS.START.toString());
//		}

	}

	public void startGame() {
		requestFocusInWindow(true);
		initializeVariables();
		Thread t = new Thread(this);
		t.start();
	}

	public void initializeVariables() {
		// x = 0, y = 0, width = 350, height = 450.
		gameScreen = new Rectangle(CANVAS_DIMINSIONS);
		RUNNING = true;

		// x = 160, y = 245, width = 40, height = 5
//		Bat = new Rectangle(160, 245, 40, 5);
		Point batCoordinates = getPosition(gameScreen.x, gameScreen.width, gameScreen.height, BAT_WIDTH, BAT_HEIGHT);

		Bat = new Rectangle(batCoordinates.x, batCoordinates.y, BAT_WIDTH, BAT_HEIGHT);
		// initial ball position.
		// x = 160, y = 218, width = 5, height = 5
		Point ballCoordinates = getPosition(Bat.x, Bat.x + Bat.width, Bat.y, BALL_SIZE, BALL_SIZE);
		Ball = new Rectangle(ballCoordinates.x, ballCoordinates.y, BALL_SIZE, BALL_SIZE);

		Brick = new Rectangle[12];
		// Creating bricks for the game, with size width = 70, height = 50
		createBricks(70, 50);
		// BRICKS created for the game new ready to use
		movex = -1;
		movey = -1;
		ballFallDown = false;
		bricksOver = false;
		count = 0;
		status = null;

	}

	private Point getPosition(int startPosition, int endPosition, int yAxis, int width, int height) {
		Point point = null;
		if (startPosition < endPosition) {
			int totalWidthMedian = (endPosition + startPosition) / 2;
			int objectWidthMedian = width / 2;
			point = new Point(totalWidthMedian - objectWidthMedian, yAxis - height);
		}
		return point;
	}

	/**
	 * Creating bricks for the game:- creating bricks again because this for loop is
	 * out of while loop in run method
	 */
	public void createBricks(int brickx, int bricky) {
		for (int i = 0; i < Brick.length; i++) {
			Brick[i] = new Rectangle(brickx, bricky, BRICK_BREADTH, BRICK_HEIGHT);
			if (i == 5) {
				brickx = 70;
				bricky = (bricky + BRICK_HEIGHT + 2);

			}
			if (i == 9) {
				brickx = 100;
				bricky = (bricky + BRICK_HEIGHT + 2);

			}
			brickx += (BRICK_BREADTH + 1);
		}
	}

}