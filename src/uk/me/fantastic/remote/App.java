package uk.me.fantastic.remote;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import com.esotericsoftware.minlog.Log;

public class App {

	private JFrame frame;
	private JTextArea textArea;
	private JPanel panel;
	private JButton btnMapViewer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App window = new App();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public App() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 583, 424);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		textArea = new JTextArea();
		frame.getContentPane().add(textArea, BorderLayout.CENTER);

		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);

		JButton btnStartServer = new JButton("start server");
		panel.add(btnStartServer);

		btnStartServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				start();
			}
		});
	}

	protected void start() {
		Log.set(Log.LEVEL_WARN);
		Log.setLogger(new Log.Logger() {
			protected void print(String message) {
				System.out.println(message);
				int offset = getTextArea().getText().length();
				try {
					getTextArea().getDocument().insertString(offset,
							message + "\n", SimpleAttributeSet.EMPTY);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		try {
			ChatWebSocketServer.start();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public JTextArea getTextArea() {
		return textArea;
	}
}
