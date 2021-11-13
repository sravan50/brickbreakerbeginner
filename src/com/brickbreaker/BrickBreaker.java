package com.brickbreaker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
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
	// movement keys..
	private static boolean right = false;
	private static boolean left = false;

	private static final int brickBreadth = 30;
	private static final int brickHeight = 20;
	// variables declaration for brick...............................
	// ===============================================================
	// declaring ball, paddle,bricks
	private Rectangle Ball;//
	private Rectangle Bat;//
	private Rectangle[] Brick;
	private Rectangle background = new Rectangle(0, 0, 350, 450);

//reverses......==>
	private int movex = -1;
	private int movey = -1;
	private boolean ballFallDown = false;
	private boolean bricksOver = false;
	private int count = 0;
	private String status;
	private JButton button;

	private static enum STATUS {
		START, PAUSE, RESUME, STOP
	}

	private static boolean PAUSE = false;
	private static boolean RUNNING = false;

	BrickBreaker() {
		initializeVariables();
		JFrame frame = new JFrame();
		button = new JButton(STATUS.START.name());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// using this as parent layout exists (JFrame)
		this.setPreferredSize(new Dimension(background.width, background.height));
		frame.getContentPane().add(this);
		frame.pack();

		frame.add(button, BorderLayout.SOUTH);
//		frame.setLocationRelativeTo(null);
//		frame.setResizable(false);
		frame.setVisible(true);

		button.addActionListener(this);

		this.addKeyListener(this);
		this.setFocusable(true);
	}

	public static void main(String[] args) {
		new BrickBreaker();

	}

	// declaring ball, paddle,bricks

	public void paint(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(background.x, background.y, background.width, background.height);
		g.setColor(Color.blue);
		g.fillOval(Ball.x, Ball.y, Ball.width, Ball.height);
		g.setColor(Color.green);
		g.fill3DRect(Bat.x, Bat.y, Bat.width, Bat.height, true);

		// this will paint below the peddle
		g.setColor(Color.GRAY);
		g.fillRect(0, 251, background.width, 200);

		// this will draw border line
		g.setColor(Color.RED);
		g.drawRect(0, 0, background.width - 1, 250);
		for (int i = 0; i < Brick.length; i++) {
			if (Brick[i] != null) {
				g.fill3DRect(Brick[i].x, Brick[i].y, Brick[i].width, Brick[i].height, true);
			}
		}

		if (ballFallDown == true || bricksOver == true) {
			Font f = new Font("Arial", Font.BOLD, 20);
			g.setFont(f);
			g.drawString(status, 70, 120);
			ballFallDown = false;
			bricksOver = false;
		}

	}

	// /...Game Loop...................

	// /////////////////// When ball strikes borders......... it

	public void run() {

		// == ball reverses when touches the brick=======
//ballFallDown == false && bricksOver == false
		while (RUNNING) {
			if (PAUSE) {
				sleep();
				continue;
			}

//   if(gameOver == true){return;}
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

			// /////////// =================================

			if (count == Brick.length) {// check if ball hits all bricks
				bricksOver = true;
				status = "YOU WON THE GAME";
				repaint();
			}
			// /////////// =================================
			repaint();
			Ball.x += movex;
			Ball.y += movey;

			if (left == true) {

				Bat.x -= 3;
				right = false;
			}
			if (right == true) {
				Bat.x += 3;
				left = false;
			}
			if (Bat.x <= 4) {
				Bat.x = 4;
			} else if (Bat.x >= 298) {
				Bat.x = 298;
			}
			// /===== Ball reverses when strikes the bat
			if (Ball.intersects(Bat)) {
				movey = -movey;
				// if(Ball.y + Ball.width >=Bat.y)
			}
			// //=====================================
			// ....ball reverses when touches left and right boundary
			if (Ball.x <= 0 || background.width - Ball.width <= Ball.x) {
				movex = -movex;
			} // if ends here
			if (Ball.y <= 0) {// ////////////////|| bally + Ball.height >= 250
				movey = -movey;
			} // if ends here.....
			if (Ball.y >= 250) {// when ball falls below bat game is over...
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

	// loop ends here

	// ///////..... HANDLING KEY EVENTS................//
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
		requestFocus(true);
		initializeVariables();
		Thread t = new Thread(this);
		t.start();
	}

	public void initializeVariables() {
		// default size of a brick...............................
		int brickx = 70;
		int bricky = 50;
		RUNNING = true;
		// x = 160, y = 218, width = 5, height = 5
		Ball = new Rectangle(160, 218, 5, 5);
		// x = 160, y = 245, width = 40, height = 5

		Bat = new Rectangle(160, 245, 40, 5);

		Brick = new Rectangle[12];
		// //////////// =====Creating bricks for the game===>.....
		createBricks(brickx, bricky);
		// ===========BRICKS created for the game new ready to use===
		movex = -1;
		movey = -1;
		ballFallDown = false;
		bricksOver = false;
		count = 0;
		status = null;

	}

	public void createBricks(int brickx, int bricky) {
		// //////////// =====Creating bricks for the game===>.....
		/*
		 * creating bricks again because this for loop is out of while loop in run
		 * method
		 */
		for (int i = 0; i < Brick.length; i++) {
			Brick[i] = new Rectangle(brickx, bricky, brickBreadth, brickHeight);
			if (i == 5) {
				brickx = 70;
				bricky = (bricky + brickHeight + 2);

			}
			if (i == 9) {
				brickx = 100;
				bricky = (bricky + brickHeight + 2);

			}
			brickx += (brickBreadth + 1);
		}
	}

}