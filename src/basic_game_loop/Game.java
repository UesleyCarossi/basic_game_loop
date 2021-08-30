package basic_game_loop;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	
	private final int WIDTH = 160;
	private final int HEIGHT = 120;
	private final int SCALE = 3;
	private BufferedImage image;
	
	private Thread thread;
	private boolean isRunning = false;
	private int frames = 0;
	
	public Game() {
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	}
	
	private void initFrame() {
		JFrame frame = new JFrame("My Game");
		frame.add(this);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start( ) {
		thread = new Thread(this);
		try {
			thread.start();
			isRunning = true;
		} catch (IllegalThreadStateException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public synchronized void stop( ) {
		try {
			thread.join();
			isRunning = false;
		} catch (InterruptedException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void main(String ...args) {
		Game game = new Game();
		game.start();
	}
	
	@Override
	public BufferStrategy getBufferStrategy() {
		BufferStrategy bufferStrategy = super.getBufferStrategy();
		if (bufferStrategy == null) {
			createBufferStrategy(3);
			return super.getBufferStrategy();
		} else {
			return bufferStrategy;
		}
	}
	
	private void tick() {
		// TODO: update states
	}
	
	private void buildBackgroundGraphics(Graphics graphics) {
		graphics.setColor(new Color(18, 18, 18));
		graphics.fillRect(0, 0, WIDTH, HEIGHT);
	}
	
	private void buildFpsGraphics(Graphics graphics) {
		graphics.setFont(new Font("Arial", Font.BOLD, 9));
		graphics.setColor(new Color(245, 245, 245));
		graphics.drawString(String.valueOf(frames), 1, 8);
	}
	
	private void buildGraphics() {
		Graphics graphics = image.getGraphics();

		buildBackgroundGraphics(graphics);
		buildFpsGraphics(graphics);
	}
	
	private void draw() {
		BufferStrategy bufferStrategy = getBufferStrategy();
		bufferStrategy.getDrawGraphics()
			.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		bufferStrategy.show();
	}
	
	private void render() {
		buildGraphics();
		draw();
	}
	
	private boolean isTimeToUpdateFrame(double delta) {
		return delta >= 1;
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		final double limetOfFPS = 60;
		final double timeBetweenEachUpdate = 1000000000 / limetOfFPS;
		double delta = 0;
		
		int currentFrame = 0;
		double timer = System.currentTimeMillis();
		
		while(isRunning) {
			final long timeNow = System.nanoTime();
			delta += (timeNow - lastTime) / timeBetweenEachUpdate;
			lastTime = timeNow;
			
			if (isTimeToUpdateFrame(delta)) {
				tick();
				render();
				currentFrame++;
				delta--;
			}
			
			if ((System.currentTimeMillis() - timer) >= 1000) {
				frames = currentFrame;
				currentFrame = 0;
				timer += 1000;
			}
		}
		
		stop();
	}
	
}
