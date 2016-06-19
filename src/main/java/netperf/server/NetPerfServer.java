package netperf.server;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.NoOpLog;

/**
 * A server spawning {@link ServerThread} on incoming connections.
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 *
 */
public class NetPerfServer {

	private boolean listening;

	private int port;

	private Log log;

	/**
	 * @param port
	 *            the port to listen for incoming connections
	 */
	public NetPerfServer(int port) {
		listening = false;
		this.port = port;
		this.setLog(new NoOpLog());
	}

	/**
	 * Start listening for incoming connections, spawn a {@link ServerThread}
	 * for each incoming connection.
	 */
	public void start() {
		listening = true;
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (listening) {
				new ServerThread(serverSocket.accept(), this).start();
			}
		} catch (IOException e) {
			getLog().error("Could not listen on port " + port, e);
			System.exit(-1);
		}
	}

	/**
	 * Stop listening for incoming connections
	 */
	public void stop() {
		listening = false;
		System.exit(0);
	}

	/**
	 * @return get the log
	 */
	public Log getLog() {
		return log;
	}

	/**
	 * @param log
	 *            the log to use
	 */
	public void setLog(Log log) {
		this.log = log;
	}
}