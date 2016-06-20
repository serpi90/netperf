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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;

/**
 * System entry point
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 */
public class NetPerfServerStarter {

	/**
	 * System entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		short port = 1990;
		if (args.length > 0) {
			try {
				port = Short.parseShort(args[0]);
			} catch (NumberFormatException e) {
			}
		}
		Log log = new SimpleLog("NetPerfServer");
		log.info("Listening at port: " + port);
		NetPerfServer server = new NetPerfServer(port);
		server.setLog(log);
		server.start();
	}

}
