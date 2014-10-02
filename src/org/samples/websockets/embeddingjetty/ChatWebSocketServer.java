package org.samples.websockets.embeddingjetty;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class ChatWebSocketServer {

	public static void main(String[] args) {
		try {
			System.out.println("\n********************************");
			System.out.println("On your remote device, browse to");
			// 1) Create a Jetty server with the 8091 port.
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface current = interfaces.nextElement();
				if (!current.isUp() || current.isLoopback()
						|| current.isVirtual())
					continue;
				Enumeration<InetAddress> addresses = current.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress current_addr = addresses.nextElement();
					if (current_addr.isLoopbackAddress())
						continue;
					System.out
							.println("http://"
									+ current_addr.getHostAddress() + ":8081");
				}
			}
			System.out.println("********************************\n\n");
			Server server = new Server(8081);
			// 2) Register ChatWebSocketHandler in the Jetty server instance.
			ChatWebSocketHandler chatWebSocketHandler = new ChatWebSocketHandler();

			ResourceHandler resource_handler = new ResourceHandler();
			resource_handler.setDirectoriesListed(true);
			resource_handler.setWelcomeFiles(new String[] { "chat.html" });

		//	new java.net.URL(
			
		//	URL url	=	ChatWebSocketServer.class.getResource("chat.html");
		//			System.out.println("RB "+url.getPath());
		//	resource_handler.setResourceBase(url.getPath());
			resource_handler.setResourceBase(".");

			// HandlerList handlers = new HandlerList();
			// handlers.setHandlers(new Handler[] { resource_handler, new
			// DefaultHandler() });
			// server.setHandler(handlers);

			// chatWebSocketHandler.setHandler(new DefaultHandler());
			chatWebSocketHandler.setHandler(resource_handler);
			server.setHandler(chatWebSocketHandler);
			// 2) Start the Jetty server.
			server.start();
			// Jetty server is stopped when the Thread is interruped.
			server.join();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
