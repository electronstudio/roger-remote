package uk.me.fantastic.remote;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

import com.esotericsoftware.minlog.Log;

public class ChatWebSocketHandler extends WebSocketHandler {

	private final Set<ChatWebSocket> webSockets = new CopyOnWriteArraySet<ChatWebSocket>();

	Keyboard keyboard;

	public WebSocket doWebSocketConnect(HttpServletRequest request,
			String protocol) {
		try {
			keyboard = new Keyboard();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ChatWebSocket();
	}

	private class ChatWebSocket implements WebSocket.OnTextMessage {

		private Connection connection;

		public void onOpen(Connection connection) {
			// Client (Browser) WebSockets has opened a connection.
			// 1) Store the opened connection
			this.connection = connection;
			// 2) Add ChatWebSocket in the global list of ChatWebSocket
			// instances
			// instance.
			webSockets.add(this);
			Log.warn("Connected to " + connection);
		}

		public void onMessage(String data) {
			Log.info(data);
			if (data.startsWith("char")) {
				String s = data.substring(5);
				try {
					keyboard.type(s);
				} catch (IllegalArgumentException e) {
					System.out.println(e);
				}
			} else if (data.startsWith("backspace")) {
				keyboard.doType(KeyEvent.VK_BACK_SPACE);
			} else if (data.startsWith("return")) {
				keyboard.doType(KeyEvent.VK_ENTER);
			} else if (data.startsWith("touch")) {
				String s[] = data.split(" ");
				double x = Double.parseDouble(s[1]);
				double y = Double.parseDouble(s[2]);
				x = x * 2;
				y = y * 2;
				if (x > 4 || x < -4) {
					x = x * 2;
				}
				if (y > 4 || y < -4) {
					y = y * 2;
				}
				keyboard.robot.mouseMove(MouseInfo.getPointerInfo()
						.getLocation().x + (int) x, MouseInfo.getPointerInfo()
						.getLocation().y + (int) y);
			} else if (data.startsWith("leftclick")) {
				keyboard.robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				keyboard.robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			} else if (data.startsWith("rightclick")) {
				keyboard.robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
				keyboard.robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
			} else if (data.startsWith("drag")) {
				String s[] = data.split(" ");
				double x = Double.parseDouble(s[1]);
				double y = Double.parseDouble(s[2]);
				// x=x*2;
				// y=y*2;
				if (x > 6 || x < -6) {
					x = x * 2;
				}
				if (y > 6 || y < -6) {
					y = y * 2;
				}
				keyboard.robot.mouseWheel((int) y);
			}

			/*
			 * if (data.startsWith("key")) { Integer keycode = new
			 * Integer(data.substring(4)); System.out.println(keycode);
			 * robot.keyPress(keycode); robot.keyRelease(keycode); }
			 */
			// robot.mouseMove(50, 50);
			// Loop for each instance of ChatWebSocket to send message server to
			// each client WebSockets.
			try {
				for (ChatWebSocket webSocket : webSockets) {
					// send a message to the current client WebSocket.
					webSocket.connection.sendMessage(data);
				}
			} catch (IOException x) {
				// Error was detected, close the ChatWebSocket client side
				this.connection.disconnect();
			}

		}

		public void onClose(int closeCode, String message) {
			// Remove ChatWebSocket in the global list of ChatWebSocket
			// instance.
			webSockets.remove(this);
			Log.warn("disconnected " + message);
		}
	}
}
