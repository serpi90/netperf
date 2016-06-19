package netperf.controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.impl.SimpleLog;

import netperf.protocol.StopMessage;

public class Stopper {

	public static void main(String[] args) throws Exception {
		SimpleLog log = new SimpleLog("Controller");
		List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
		addresses.add(new InetSocketAddress("localhost", 1990));
		addresses.add(new InetSocketAddress("localhost", 1991));

		for (InetSocketAddress address : addresses) {
			try (Socket socket = new Socket()) {
				socket.connect(address);
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
				inputStream.close();
				outputStream.writeObject(new StopMessage());
				outputStream.close();
			}
		}
		log.info("End");
	}
}
