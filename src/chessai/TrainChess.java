package chessai;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import main.Board;
import matrixmath.Matrix;
import neuralnetwork.MNeuralNetwork;
import setup.Manager;
import setup.Window;

public class TrainChess implements Manager {

	MNeuralNetwork[] nn;
	private static int moves = 150;
	int[] fitness;
	int matchPS;
	double mut;
	int points;
	int[] ppp = { 1, 3, 5, 9, 50 };
	public static boolean stop = false;
	BufferedImage waitP;
	static int count = 0;
	public static boolean done = false;

	public TrainChess(int sub, int layers, int[] nnodes, int matchPS,
			double mut, int points) {

		this.points = points;
		nn = new MNeuralNetwork[sub];
		fitness = new int[sub];
		this.matchPS = matchPS;
		this.mut = mut;
		for (int i = 0; i < sub; i++) {
			nn[i] = new MNeuralNetwork(layers, nnodes);
		}
	}

	public TrainChess() {
		try {
			waitP = ImageIO.read(getClass().getResource("/wking.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void train() {
		MNeuralNetwork[] newGen = new MNeuralNetwork[nn.length];
		test();
		for (int i = 0; i < nn.length; i++) {
			MNeuralNetwork n1 = nn[TrainChess.getPool(fitness)];
			MNeuralNetwork n2 = nn[TrainChess.getPool(fitness)];
			MNeuralNetwork b = MNeuralNetwork.fuse(n1, n2, mut);
			newGen[i] = b;
		}
		nn = newGen;
	}

	public void test() {
		int[] plays = new int[nn.length];
		fitness = new int[nn.length];
		LinkedList<Integer> indexes = new LinkedList<Integer>();
		for (int i = 0; i < nn.length; i++) {
			indexes.add(i);
		}

		for (int i = 0; i < nn.length; i++) {
			if (plays[i] >= matchPS) {
				continue;
			}

			int pi = plays[i];

			for (int j = 0; j < matchPS - pi; j++) {
				int i2;
				do {
					i2 = (int) (Math.random() * indexes.size());
				} while (i2 == i && indexes.size() > 1);

				boolean won1 = matchf(i, i2);
				boolean won2 = matchf(i2, i);

				if (won1)
					fitness[i] += points;
				else
					fitness[indexes.get(i2)] += points;

				if (won2)
					fitness[indexes.get(i2)] += points;
				else
					fitness[i] += points;

				plays[indexes.get(i2)]++;

				if (plays[indexes.get(i2)] >= matchPS)
					indexes.remove(i2);
			}
			indexes = Matrix.removeByValue(indexes, i);

		}
	}

	public boolean matchf(int i1, int i2) {
		int[][] history = new int[moves * 2][4];

		Board B = new Board(false);

		MNeuralNetwork n1 = nn[i1];
		MNeuralNetwork n2 = nn[i2];

		int r = 1;
		for (int i = 0; i < moves; i++) {
			float[][] co = n1.feedforward(B.getBoardL());
			int y1 = (int) (co[0][0] * 8);
			int x1 = (int) (co[1][0] * 8);
			int y2 = (int) (co[2][0] * 8);
			int x2 = (int) (co[3][0] * 8);
			int po = addP(B, x2, y2, i1);
			r = B.moveP(y1, x1, y2, x2);

			if (r == 1 | r == 2) {
				fitness[i] += po * points;
			}

			if (r == 0) {
				return false;
			} else if (r == 2) {
				return true;
			}

			history[i * 2] = new int[] { y1, x1, y2, x2 };

			if (i > 10) {
				System.out.println(i);
			}

			co = n2.feedforward(B.getBoardL());
			y1 = (int) (co[0][0] * 8);
			x1 = (int) (co[1][0] * 8);
			y2 = (int) (co[2][0] * 8);
			x2 = (int) (co[3][0] * 8);
			po = addP(B, x2, y2, i2);
			r = B.moveP(y1, x1, y2, x2);
			if (r == 1 | r == 2) {
				fitness[i2] += po * points / 2;
			}
			if (r == 0) {
				return true;
			} else if (r == 2) {
				return false;
			}
			history[i * 2 + 1] = new int[] { y1, x1, y2, x2 };
			if (i > 5 && checkRepetition(history, i * 2 + 2)) {
				return false;
			}
		}
		return true;
	}

	public boolean checkRepetition(int[][] hist, int c) {
		int[] move11 = hist[c - 1];
		int[] move12 = hist[c - 3];
		int[] move21 = hist[c - 2];
		int[] move22 = hist[c - 4];
		int i = 5;
		while (Arrays.equals(move11, hist[c - i])
				&& Arrays.equals(move21, hist[c - (i + 1)])
				&& Arrays.equals(move12, hist[c - (i + 2)])
				&& Arrays.equals(move22, hist[c - (i + 3)])) {
			i += 4;
			if (i > 5) {
				return true;
			}
		}
		return false;
	}

	public static int max(float[] inp) {
		int r = 0;
		float max = 0;
		for (int i = 0; i < inp.length; i++) {
			if (inp[i] > max) {
				max = inp[i];
				r = i;
			}
		}
		return r;
	}

	public static int getPool(int[] fit) {
		double total = 0;
		double at = 0;
		double num = Math.random();
		for (int i : fit) {
			total += i;
		}
		for (int i = 0; i < fit.length; i++) {
			at += Matrix.map(fit[i], 0, total, 0, 1);
			if (at >= num) {
				return i;
			}
		}
		return 0;
	}

	public int addP(Board b, int x2, int y2, int i) {
		int p = b.board[y2][x2];
		int po = 0;
		switch (p) {
		case 11:
			po = ppp[0];
			break;
		case 12:
			po = ppp[1];
			break;
		case 13:
			po = ppp[1];
			break;
		case 14:
			po = ppp[2];
			break;
		case 15:
			po = ppp[3];
			break;
		case 16:
			po = ppp[4];
			break;
		}
		return po;
	}

	public void createFile(String filen) {
		try {
			File f = new File(System.getProperty("user.home") + "/Desktop" + "/"
					+ filen + ".txt");
			f.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MNeuralNetwork max() {
		int index = 0;
		MNeuralNetwork n;
		// Write here
		int max = 0;
		int imax = 0;
		for (int i = 0; i < nn.length; i++) {
			if (fitness[i] > max) {
				max = fitness[i];
				imax = i;
			}
		}
		index = imax;
		n = nn[index];
		return n;
	}

	public static void trainN(int subjects, int layers, int[] nnodes,
			int matchPS, double mutations, int points, int gen, String filen) {

		TrainChess t = new TrainChess(subjects, layers, nnodes, matchPS,
				mutations, points);

		for (int i = 0; i < gen; i++) {
			if (stop) {
				break;
			}
			count = i;
			t.train();
		}

		MNeuralNetwork.writeToFAMNN(layers, nnodes, filen, t.nn, t.fitness);
		done = true;
	}

	public static void trainO(String filen, int matchPS, double mutations,
			int points, int gen) {
		MNeuralNetwork[] nn = MNeuralNetwork.readAMNN(filen);
		TrainChess t = new TrainChess(nn.length, nn[0].getLayers(),
				nn[0].getNnodes(), matchPS, mutations, points);
		t.setNn(nn);
		for (int i = 0; i < gen; i++) {
			if (stop) {
				break;
			}
			count = i;
			t.train();
		}
		MNeuralNetwork.writeToFAMNN(nn[0].getLayers(), nn[0].getNnodes(), filen,
				t.nn, t.fitness);
		done = true;
	}

	public int[] getFitness() {
		return fitness;
	}

	public void setFitness(int[] fitness) {
		this.fitness = fitness;
	}

	public MNeuralNetwork[] getNn() {
		return nn;
	}

	public void setNn(MNeuralNetwork[] nn) {
		this.nn = nn;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Window wind = new Window(300, 325, 1, 1, "Train AI", new TrainChess());

		int subjects = 500;
		int layers = 5;
		int[] nnodes = { 64, 20, 20, 20, 4 };
		int matchPS = 20;
		double mutations = 0.1;
		int points = 1;
		int gen = 100000;

		String filen = "chessAI6";

		//TrainChess.trainN(subjects, layers, nnodes, matchPS, mutations, points,
			//gen, filen);
		TrainChess.trainO(filen, matchPS, mutations, points, gen);
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(waitP, 0, 0, null);
		g.setColor(Color.BLACK);
		g.drawString(Integer.toString(count), 143, 30);
		if (done) {
			g.drawString("Done", 20, 20);
		}
	}

	@Override
	public void keyPressed(int e) {
		if (e == 106) {
			stop = true;
		}
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

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void keyReleased(int e) {

	}

	@Override
	public void keyTyped(int e) {

	}
}
