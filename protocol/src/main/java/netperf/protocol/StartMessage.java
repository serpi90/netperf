package netperf.protocol;

/*
 * #%L
 * Protocol
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

import java.net.InetSocketAddress;
import java.util.List;

/**
 * A message indicating the receiver to start measuring the transfer speed
 * against the provided addresses
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 */
public class StartMessage implements Message {
	private List<InetSocketAddress> addresses;
	private TransferCommand command;

	/**
	 * @param addresses
	 *            the addresses to measure against
	 * @param command
	 *            the command to use during measurement
	 */
	public StartMessage(List<InetSocketAddress> addresses, TransferCommand command) {
		this.addresses = addresses;
		this.command = command;
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.acceptStartMessage(this);
	}

	/**
	 * @return the addresses to measure speed against
	 */
	public List<InetSocketAddress> getAddresses() {
		return addresses;
	}

	/**
	 * @return the command to execute when measuring speed
	 */
	public TransferCommand getCommand() {
		return command;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Start Message to measure against ");
		for (InetSocketAddress address : addresses) {
			builder.append(address.toString());
			builder.append(" ");
		}
		builder.append("requesting to ");
		builder.append(command);
		return builder.toString();
	}
}
