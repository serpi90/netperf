package netperf.server;

/*
 * #%L
 * Server
 * %%
 * Copyright (C) 2016 Julián Maestri
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

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
}