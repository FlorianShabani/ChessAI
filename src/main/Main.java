package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import setup.Manager;
import setup.Window;

public class Main implements Manager {

	public static final int Width = 576, Height = 600;

	private int cx, cy, rx, ry;

	// False-White, True-Black
	private boolean colorTurn;

	public Board board;

	public Main() {
		board = new Board(true);
		// board.board = board.turnBoard(board.board);
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, Width, Height);
		board.draw(g);
		if(board.isColorTurn()) {
			g.setColor(Color.BLACK);
		}else
			g.setColor(Color.WHITE);
		
		g.fillOval(0,  Height - 40, 10, 10);
	}

	public void moveP(int y1, int x1, int y2, int x2) {
		board.moveP(cy, cx, ry, rx);
	}
	
	@Override
	public void tick() {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		colorTurn = board.isColorTurn();
		if (colorTurn) {
			cx = 7 - e.getX() / 72;
			cy = 7 - e.getY() / 72;
		} else {
			cx = e.getX()/72;
			cy = e.getY() / 72;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		colorTurn = board.isColorTurn();
		if (colorTurn) {
			rx = 7 - e.getX() / 72;
			ry = 7 - e.getY() / 72;
		} else {
			rx = e.getX() / 72;
			ry = e.getY() / 72;
		}
		board.moveP(cy, cx, ry, rx);
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void keyPressed(int e) {

	}

	@Override
	public void keyReleased(int e) {

	}

	@Override
	public void keyTyped(int e) {

	}

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Window wind = new Window(Width, Height, 60, 60, "Chess AI", new Main());
	}
}
