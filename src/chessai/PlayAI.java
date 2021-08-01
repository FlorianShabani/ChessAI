package chessai;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import main.Board;
import neuralnetwork.MNeuralNetwork;
import setup.Manager;
import setup.Window;

public class PlayAI implements Manager {
	public static final int Width = 576, Height = 600;

	static Board B = new Board(true);
	static MNeuralNetwork chessai;
	
	private int ix, iy, fx, fy;
	
	public static void main(String[] args) {
		chessai = MNeuralNetwork.readBMNN("chessAI6");
		
		float[][] co = chessai.feedforward(B.getBoardL());
		int y1 = (int) (co[0][0] * 8);
		int x1 = (int) (co[1][0] * 8);
		int y2 = (int) (co[2][0] * 8);
		int x2 = (int) (co[3][0] * 8);
		int c = B.moveP(y1, x1, y2, x2);
		if(c != 1) {
			System.out.println("ouch");
		}
		
		@SuppressWarnings("unused")
		Window wind = new Window(Width, Height, 60, 60, "Play AI", new PlayAI());
	}

	@Override
	public void draw(Graphics g) {
		B.draw(g);
	}

	@Override
	public void tick() {

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
	public void mousePressed(MouseEvent e) {
		ix = 7 - (e.getX()/72);
		iy = 7 - (e.getY()/72);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		fx = 7 - (e.getX()/72);
		fy = 7 - (e.getY()/72);
		B.moveP(iy, ix, fy, fx);
		float[][] co = chessai.feedforward(B.getBoardL());
		int y1 = (int) (co[0][0] * 8);
		int x1 = (int) (co[1][0] * 8);
		int y2 = (int) (co[2][0] * 8);
		int x2 = (int) (co[3][0] * 8);
		int c = B.moveP(y1, x1, y2, x2);
		if(c != 1) {
			System.out.println("ouch");
		}
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
}
