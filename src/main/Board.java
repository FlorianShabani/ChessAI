package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Board {
	public int[][] board;
	BufferedImage pieces;

	private boolean colorTurn;
	private boolean castleKW, castleKB;
	private boolean castleQW, castleQB;
	private int[] xs;
	private BufferedImage[] pieceD;

	public Board(boolean gonnaDraw) {
		colorTurn = false;
		castleKW = true;castleKB = true;castleQB = true;castleQW = true;
		/*
		 * White 1 - Pawn, 2 - Bishop, 3 - Knight, 4 - Rook, 5 - Queen, 6 - King
		 * Black 11 - Pawn 12 - Bishop 13 - 15 - Queen 16 -King
		 */
		board = new int[][] { 
				{ 14, 13, 12, 15, 16, 12, 13, 14 },
				{ 11, 11, 11, 11, 11, 11, 11, 11 },
				{ 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 1, 1, 1, 1, 1, 1, 1, 1 },
				{ 4, 3, 2, 5, 6, 2, 3, 4 } };
				
		xs = new int[] { 0, 75, 148, 218, 296, 368 };
		if(gonnaDraw) {
			this.splitP();
		}
	}

	public int moveP(int i, int j, int l, int k) {
		if (colorTurn)
			board = turnBoard(board);
		if (allowedToM(i, j, l, k, board)) {
			if(board[l][k] == 16) {
				return 2;
			}
			board[l][k] = board[i][j];
			board[i][j] = 0;
			if (colorTurn)
				board = turnBoard(board);
			if (colorTurn)
				colorTurn = false;
			else
				colorTurn = true;
			
			return 1;
		} else {
			if (colorTurn)
				board = turnBoard(board);
			return 0;
		}
	}

	public boolean allowedToM(int y1, int x1, int y2, int x2, int[][] board) {
		int p = board[y1][x1];
		int m = board[y2][x2];
		if (p > 10 || (m < 10 && m != 0) || p == 0 || (x1 == x2 && y1 == y2)) {
			return false;
		}

		// Pawn
		if (p == 1) {
			if (/* First move */ (x1 == x2 && y1 == 6 && y2 == 4
					&& board[4][x1] == 0 && board[5][x1] == 0) ||
			/* Normal move */ (x1 == x2 && y2 == y1 - 1 && board[y2][x2] == 0)
					||
					/* Take */ ((x1 == x2 + 1 || x1 == x2 - 1) && y2 == y1 - 1
							&& board[y2][x2] > 10)) {
				if(y2 == 0) {
					board[y1][x1] = 5;
				}
				return true;
			}
			else
				return false;
		}
		// Bishop
		else if (p == 2) {
			if (!(Math.abs(x1 - x2) == Math.abs(y1 - y2))) {
				return false;
			}

			if (x1 > x2) {
				if (y1 < y2) {
					for (int i = 1; i < y2 - y1; i++) {
						int j = x2 + i;
						if (board[y2 - i][j] != 0) {
							return false;
						}
					}
				} else {
					for (int i = 1; i < y1 - y2; i++) {
						int j = x1 - i;
						if (board[y1 - i][j] != 0) {
							return false;
						}
					}
				}
			} else {
				if (y1 < y2) {
					for (int i = 1; i < y2 - y1; i++) {
						int j = x1 + i;
						if (board[y1 + i][j] != 0) {
							return false;
						}
					}
				} else {
					for (int i = 1; i < y1 - y2; i++) {
						int j = x1 + i;
						if (board[y1 - i][j] != 0) {
							return false;
						}
					}
				}
			}
		}
		// Knight
		else if (p == 3) {
			if ((x1 == x2 + 2 && (y1 == y2 + 1 || y1 == y2 - 1)))
				return true;
			else if (x1 == x2 - 2 && (y1 == y2 + 1 || y1 == y2 - 1))
				return true;
			else if (y1 == y2 + 2 && (x1 == x2 + 1 || x1 == x2 - 1))
				return true;
			else if (y1 == y2 - 2 && (x1 == x2 + 1 || x1 == x2 - 1))
				return true;
			else
				return false;
		}
		// Rook
		else if (p == 4) {
			if (!(x1 == x2 || y1 == y2))
				return false;

			if (x1 > x2) {
				for (int i = 1; i < x1 - x2; i++) {
					if (board[y1][x2 + i] != 0) {
						return false;
					}
				}
			} else if (x1 < x2) {
				for (int i = 1; i < x2 - x1; i++) {
					if (board[y1][x1 + i] != 0)
						return false;
				}
			} else if (y1 > y2) {
				for (int i = 1; i < y1 - y2; i++) {
					if (board[y2 + i][x1] != 0)
						return false;
				}
			} else if (y1 < y2) {
				for (int i = 1; i < y2 - y1; i++) {
					if (board[y1 + i][x1] != 0)
						return false;
				}
			}
			// Uncheck castle;
			if (colorTurn) {
				if (castleKB && x1 == 0 && y1 == 7) {
					castleKB = false;
				} else if (castleQB && x1 == 7 && y1 == 7) {
					castleQB = false;
				}
			} else {
				if (castleKW && x1 == 7 && y1 == 7) {
					castleKW = false;
				} else if (castleQW && x1 == 0 && y1 == 7) {
					castleQW = false;
				}
			}
		}
		// Queen
		else if (p == 5) {
			// Rook-like Movement
			if (x1 == x2 || y1 == y2) {
				if (x1 > x2) {
					for (int i = 1; i < x1 - x2; i++) {
						if (board[y1][x2 + i] != 0) {
							return false;
						}
					}
				} else if (x1 < x2) {
					for (int i = 1; i < x2 - x1; i++) {
						if (board[y1][x1 + i] != 0)
							return false;
					}
				} else if (y1 > y2) {
					for (int i = 1; i < y1 - y2; i++) {
						if (board[y2 + i][x1] != 0)
							return false;
					}
				} else if (y1 < y2) {
					for (int i = 1; i < y2 - y1; i++) {
						if (board[y1 + i][x1] != 0)
							return false;
					}
				}
			}
			// Bishop-like Movement
			else if (Math.abs(x1 - x2) == Math.abs(y1 - y2)) {
				if (x1 > x2) {
					if (y1 < y2) {
						for (int i = 1; i < y2 - y1; i++) {
							int j = x2 + i;
							if (board[y2 - i][j] != 0) {
								return false;
							}
						}
					} else {
						for (int i = 1; i < y1 - y2; i++) {
							int j = x1 - i;
							if (board[y1 - i][j] != 0) {
								return false;
							}
						}
					}
				} else {
					if (y1 < y2) {
						for (int i = 1; i < y2 - y1; i++) {
							int j = x1 + i;
							if (board[y1 + i][j] != 0) {
								return false;
							}
						}
					} else {
						for (int i = 1; i < y1 - y2; i++) {
							int j = x1 + i;
							if (board[y1 - i][j] != 0) {
								return false;
							}
						}
					}
				}
			} else
				return false;
		} else if (p == 6) {
			if (colorTurn) {
				if (castleKB && x2 == 1 && y2 == 7) {
					if (board[7][1] == 0 && board[7][2] == 0) {
						board[7][0] = 0;
						board[7][1] = 0;
						board[7][2] = 4;
						board[7][3] = 6;
						castleKB = false;
						return true;
					}
				} else if (castleQB && x2 == 5 && y2 == 7) {
					if (board[7][4] == 0 && board[7][5] == 0
							&& board[7][5] == 0) {
						board[7][3] = 6;
						board[7][4] = 4;
						board[7][5] = 0;
						board[7][6] = 0;
						board[7][7] = 0;
						castleQB = false;
						return true;
					}
				}
			} else {
				if (castleKW && x2 == 6 && y2 == 7) {
					if (board[7][5] == 0 && board[7][6] == 0) {
						board[7][4] = 6;
						board[7][5] = 4;
						board[7][6] = 0;
						board[7][7] = 0;
						castleKW = false;
						return true;
					}
				} else if (castleQW && x2 == 2 && y2 == 7) {
					if (board[7][1] == 0 && board[7][2] == 0
							&& board[7][3] == 0) {
						board[7][0] = 0;
						board[7][1] = 0;
						board[7][2] = 0;
						board[7][3] = 4;
						board[7][4] = 6;
						castleQW = false;
						return true;
					}
				}

			}
			if (!(Math.abs(x1 - x2) < 2 && Math.abs(y1 - y2) < 2)) {
				return false;
			}
			if(colorTurn &&(castleQB || castleKB)) {
				castleQB = false;
				castleKB = false;
			}else if(castleQW || castleKW) {
				castleQW = false;
				castleKW = false;
			}
		}
		return true;
	}

	// Dont forget to try without it!
	public int[][] turnBoard(int[][] iboard) {
		int[][] board = copyArr(iboard);
		int[][] nboard = new int[8][8];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] > 10)
					board[i][j] = board[i][j] - 10;
				else if (board[i][j] != 0)
					board[i][j] = board[i][j] + 10;

				nboard[7 - i][7 - j] = board[i][j];
			}
		}
		return nboard;
	}

	public int[][] copyArr(int[][] oarr) {
		int[][] narr = new int[oarr.length][oarr[0].length];
		for (int i = 0; i < narr.length; i++) {
			for (int j = 0; j < narr[i].length; j++) {
				narr[i][j] = oarr[i][j];
			}
		}
		return narr;
	}

	public void printB() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				System.out.print(board[i][j]);
			}
			System.out.println();
		}
	}

	public void draw(Graphics g) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {

				if ((i - j) % 2 == 1 || (j - i) % 2 == 1) {
					g.setColor(new Color(40, 100, 50));
					g.fillRect(j * 72, i * 72, 72, 72);
				} else {
					g.setColor(new Color(255, 255, 255));
					g.fillRect(j * 72, i * 72, 72, 72);
				}

				if (board[i][j] == 1) {
					g.drawImage(pieceD[5], j * 72, i * 72, null);
				} else if (board[i][j] == 2) {
					g.drawImage(pieceD[2], j * 72, i * 72, null);
				} else if (board[i][j] == 3) {
					g.drawImage(pieceD[3], j * 72, i * 72, null);
				} else if (board[i][j] == 4) {
					g.drawImage(pieceD[4], j * 72, i * 72, null);
				} else if (board[i][j] == 5) {
					g.drawImage(pieceD[1], j * 72, i * 72, null);
				} else if (board[i][j] == 6) {
					g.drawImage(pieceD[0], j * 72, i * 72, null);
				}

				else if (board[i][j] == 11) {
					g.drawImage(pieceD[11], j * 72, i * 72, null);
				} else if (board[i][j] == 12) {
					g.drawImage(pieceD[8], j * 72, i * 72, null);
				} else if (board[i][j] == 13) {
					g.drawImage(pieceD[9], j * 72, i * 72, null);
				} else if (board[i][j] == 14) {
					g.drawImage(pieceD[10], j * 72, i * 72, null);
				} else if (board[i][j] == 15) {
					g.drawImage(pieceD[7], j * 72, i * 72, null);
				} else if (board[i][j] == 16) {
					g.drawImage(pieceD[6], j * 72, i * 72, null);
				}
			}
		}
	}

	public void splitP() {
		try {
			pieces = ImageIO
					.read(getClass().getResource("/chess-pieces-drawing.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		pieceD = new BufferedImage[12];
		for (int i = 0; i < pieceD.length; i++) {
			BufferedImage img = new BufferedImage(72, 72,
					BufferedImage.TYPE_INT_ARGB);
			pieceD[i] = img;
		}
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 6; i++) {
				Graphics g = pieceD[j * 6 + i].getGraphics();
				g.drawImage(pieces, -xs[i], -j * 75, null);
			}
		}
	}

	public float[] getBoardL() {
		float[] result = new float[64];
		for(int i = 0; i < result.length; i++) {
			result[i] = board[i/8][i%8]; 
		}
		return result;
	}
	
	public boolean isColorTurn() {
		return colorTurn;
	}
}
