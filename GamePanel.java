import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import java.io.File;

public class GamePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	static final int WIDTH = 500;
	static final int HEIGHT = 500;
	static final int UNIT_SIZE = 20;
	static final int NUMBER_OF_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);

	// hold x and y coordinates for body parts of the snake
	final int x[] = new int[NUMBER_OF_UNITS];
	final int y[] = new int[NUMBER_OF_UNITS];

	// initial length of the snake
	int length = 5;
	int foodEaten;
	int foodX;
	int foodY;
	char direction = 'D';
	boolean running = false;
	Random random;
	Timer timer;

	GamePanel() {
		random = new Random();
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setBackground(Color.DARK_GRAY);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		play();
	}

	public void play() {
		addFood();
		running = true;

		timer = new Timer(80, this);
		timer.start();
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		draw(graphics);
	}

	public void move() {
		for (int i = length; i > 0; i--) {
			// shift the snake one unit to the desired direction to create a move
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}

		if (direction == 'L') {
			x[0] = x[0] - UNIT_SIZE;
		} else if (direction == 'R') {
			x[0] = x[0] + UNIT_SIZE;
		} else if (direction == 'U') {
			y[0] = y[0] - UNIT_SIZE;
		} else {
			y[0] = y[0] + UNIT_SIZE;
		}
	}

	public void checkFood() {
		if (x[0] == foodX && y[0] == foodY) {
			length++;
			foodEaten++;
			addFood();
			playEatSound();

		}
	}

	public void draw(Graphics graphics) {
		if (running) {
			graphics.setColor(Color.YELLOW); // Changed food color to yellow
			graphics.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

			for (int i = 0; i < length; i++) {
				if (i == 0) {
					graphics.setColor(Color.GREEN); // Changed head color to green
				} else {
					graphics.setColor(Color.BLUE); // Changed body color to blue
				}
				graphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
			}

			graphics.setColor(Color.WHITE);
			graphics.setFont(new Font("Sans serif", Font.BOLD, 25));
			FontMetrics metrics = getFontMetrics(graphics.getFont());
			graphics.drawString("Score: " + foodEaten, (WIDTH - metrics.stringWidth("Score: " + foodEaten)) / 2,
					graphics.getFont().getSize());
		} else {
			gameOver(graphics);
		}
	}

	public void addFood() {
		foodX = random.nextInt((int) (WIDTH / UNIT_SIZE)) * UNIT_SIZE;
		foodY = random.nextInt((int) (HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
	}

	public void checkHit() {
		// check if head run into its body
		for (int i = length; i > 0; i--) {
			if (x[0] == x[i] && y[0] == y[i]) {
				running = false;
			}
		}

		// check if head run into walls
		if (x[0] < 0 || x[0] > WIDTH || y[0] < 0 || y[0] > HEIGHT) {
			running = false;
		}

		if (!running) {
			timer.stop();
		}
	}

	public void gameOver(Graphics graphics) {
		graphics.setColor(Color.red);
		graphics.setFont(new Font("Sans serif", Font.ROMAN_BASELINE, 50));
		FontMetrics metrics = getFontMetrics(graphics.getFont());
		graphics.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 2);
		graphics.drawString("Press Space to Restart!", (WIDTH - metrics.stringWidth("Press Space to Restart!")) / 3,
				HEIGHT / 4);

		graphics.setColor(Color.white);
		graphics.setFont(new Font("Sans serif", Font.ROMAN_BASELINE, 25));
		metrics = getFontMetrics(graphics.getFont());
		graphics.drawString("Score: " + foodEaten, (WIDTH - metrics.stringWidth("Score: " + foodEaten)) / 2,
				graphics.getFont().getSize());

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (running) {
			move();
			checkFood();
			checkHit();
		}
		repaint();
	}

	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					if (direction != 'R') {
						direction = 'L';
					}
					break;

				case KeyEvent.VK_RIGHT:
					if (direction != 'L') {
						direction = 'R';
					}
					break;

				case KeyEvent.VK_UP:
					if (direction != 'D') {
						direction = 'U';
					}
					break;

				case KeyEvent.VK_DOWN:
					if (direction != 'U') {
						direction = 'D';
					}
				case KeyEvent.VK_SPACE:
					if (!running) {
						resetGame();
					}
					break;
			}
		}
	}

	public void resetGame() {
		length = 5;
		foodEaten = 0;
		direction = 'D';
		for (int i = 0; i < length; i++) {
			x[i] = 0;
			y[i] = 0;
		}
		play();
	}

	public void playEatSound() {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("./sounds/eatSound.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
		} catch (Exception e) {
			System.out.println("Error with playing sound.");
			e.printStackTrace();
		}
	}
}
