/* **************************************************************************
 *                                                                          *
 *  Copyright (C)  2011  Nils Foken, André Kießlich,                        *
 *                       Peter Kossek, Hans Laser                           *
 *                                                                          *
 *  Nils Foken       <nils.foken@it2009.ba-leipzig.de>                      *
 *  André Kießlich   <andre.kiesslich@it2009.ba-leipzig.de>                 *
 *  Peter Kossek     <peter.kossek@it2009.ba-leipzig.de>                    *
 *  Hans Laser       <hans.laser@it2009.ba-leipzig.de>                      *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  This file is part of 'javadns'.                                         *
 *                                                                          *
 *  This project is free software: you can redistribute it and/or modify    *
 *  it under the terms of the GNU General Public License as published by    *
 *  the Free Software Foundation, either version 3 of the License, or       *
 *  any later version.                                                      *
 *                                                                          *
 *  This project is distributed in the hope that it will be useful,         *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 *  GNU General Public License for more details.                            *
 *                                                                          *
 *  You should have received a copy of the GNU General Public License       *
 *  along with this project. If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                          *
 ****************************************************************************/

package de.baleipzig.javadns;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class DNSServer extends JFrame {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static int DEFAULT_PORT = 53;
	private JTextArea logTextArea;
	private JButton btnStartServer, btnStopServer;
	private JTextField txfPort;
	private ServerWorker serverWorker;

	public DNSServer(String title) {
		setMinimumSize(new Dimension(600, 500));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
		    public void windowClosing(WindowEvent winEvt) {
		        closeConnections();
		        System.exit(0); 
		    }
		});
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// simply ignore if it doesn't work
		}
		
		setTitle(title);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setEnabled(false);
		splitPane.setResizeWeight(0.75);
		getContentPane().add(splitPane);
		
		JScrollPane logScrollPane = new JScrollPane();
		splitPane.setLeftComponent(logScrollPane);
		
		logTextArea = new JTextArea();
		logTextArea.setEditable(false);
		logTextArea.setLineWrap(true);
		logTextArea.setWrapStyleWord(true);
		logScrollPane.setViewportView(logTextArea);
		logTextArea.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		
		JScrollPane treeScrollPane = new JScrollPane();
		splitPane.setRightComponent(treeScrollPane);
		
		JTree recordTree = new JTree();
		treeScrollPane.setViewportView(recordTree);
		recordTree.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel lblPort = new JLabel("Port:");
		panel.add(lblPort);
		
		txfPort = new JTextField();
		txfPort.setText(String.valueOf(DEFAULT_PORT));
		panel.add(txfPort);
		txfPort.setColumns(10);
		
		btnStartServer = new JButton("Start Server");
		btnStartServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (serverWorker != null) {
					if (serverWorker.isRunning) {
						serverWorker.stop();
					}
					else if (!serverWorker.isDone()) {
						appendText("Still stopping server..."+LINE_SEPARATOR);
					}
					else {
						start();
					}
				}
				else start();
			}
		});
		panel.add(btnStartServer);
		
		btnStopServer = new JButton("Stop Server");
		btnStopServer.setEnabled(false);
		btnStopServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				if (serverWorker != null) {
					if (serverWorker.isRunning) {
						serverWorker.stop();
					}
					else if (!serverWorker.isDone()) {
						appendText("Still stopping server..."+LINE_SEPARATOR);
					}
					else {
						serverWorker = null;
					}
				} 
			}
		});
		panel.add(btnStopServer);
	}

	protected void closeConnections() {
		if (serverWorker != null) {
			if (serverWorker.isRunning) {
				serverWorker.stop();
			}
			while(!serverWorker.isDone()) {
				// wait for it
			}
		}
	}

	protected static DomainRecordMessage lookup(String hostName, String recordType) {
		return DomainRecord.lookup(hostName, recordType);
	}
	
	private void start() {
		btnStartServer.setEnabled(false);
		btnStopServer.setEnabled(true);
		int port = Integer.parseInt(txfPort.getText());
		serverWorker = new ServerWorker(port);
		serverWorker.execute();
	}
	
	private void appendText(String text) {
		logTextArea.append(text + LINE_SEPARATOR);
		logTextArea.setCaretPosition(logTextArea.getText().length());
	}

	public static void main(String[] args) {
		DNSServer server = new DNSServer("DNS Server");
		server.setLocationRelativeTo(null);
		server.setVisible(true);
	}
	
	private class ServerWorker extends SwingWorker<Object, Object> {

		private int port;
		private boolean isRunning = true;

		public ServerWorker(int port) {
			super();
			this.port = port;
			isRunning = true;
		}
		
		@Override
		protected Object doInBackground() throws Exception {
			appendText("Server listening at port "+port+LINE_SEPARATOR);
			ServerSocket serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(5000); // timeout 5s
			while (isRunning) {
				Socket clientSocket;
				try {
					clientSocket = serverSocket.accept();
					new RequestThread(clientSocket).run();
					
				} catch (SocketTimeoutException e) {
					//appendText("Timeout exceeded."+LINE_SEPARATOR);
				}

			}
			serverSocket.close();
			appendText("Server stopped."+LINE_SEPARATOR);
			btnStartServer.setEnabled(true);
			return null;
		}		
		
		public void stop() {
			appendText("Stopping server..."+LINE_SEPARATOR);
			btnStopServer.setEnabled(false);
			isRunning = false;
		}
		
	}
	
	private class RequestThread extends Thread {

		private static final String REQUEST_SEPARATOR = ",";
		private Socket socket;
		
		public RequestThread(Socket socket) {
			super();
			this.socket = socket;
		}
		
		@Override
		public void run() {
			try {
				// read request
				appendText("Connection from " + socket.getInetAddress().getHostAddress());
				ObjectInputStream ois = new ObjectInputStream(
						socket.getInputStream());
				String requestString = (String) ois.readObject();
				appendText("Request is: " + requestString);
				// split request
				String[] split = requestString.split(REQUEST_SEPARATOR);
				String host = split[0];
				String record = split[1];
				// perform lookup
				DomainRecordMessage responseMessage = lookup(host, record);
				appendText("Lookup result is: " + LINE_SEPARATOR + responseMessage
						+ LINE_SEPARATOR);
				// send response
				ObjectOutputStream oos = new ObjectOutputStream(
						socket.getOutputStream());
				String response = responseMessage.getDnsResult().toString();
				oos.writeObject(response.substring(1, response.length() - 1));
				oos.flush();
				oos.close();
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
