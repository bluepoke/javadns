/* **************************************************************************
 *                                                                          *
 *  Copyright (C)  2011  Nils Foken, Andr� Kie�lich,                        *
 *                       Peter Kossek, Hans Laser                           *
 *                                                                          *
 *  Nils Foken       <nils.foken@it2009.ba-leipzig.de>                      *
 *  Andr� Kie�lich   <andre.kiesslich@it2009.ba-leipzig.de>                 *
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

@SuppressWarnings("serial")
public class DNSServer extends JFrame {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static int DEFAULT_PORT = 53;
	private JTextArea logTextArea;
	private JTextField txfPort;
	private ServerWorker serverWorker;

	
	private void addLog(String log) {
		logTextArea.append(log);
	}

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
		logScrollPane.setViewportView(logTextArea);
		
		JScrollPane treeScrollPane = new JScrollPane();
		splitPane.setRightComponent(treeScrollPane);
		
		JTree recordTree = new JTree();
		treeScrollPane.setViewportView(recordTree);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel lblPort = new JLabel("Port:");
		panel.add(lblPort);
		
		txfPort = new JTextField();
		txfPort.setText("53");
		panel.add(txfPort);
		txfPort.setColumns(10);
		
		JButton btnStartServer = new JButton("Start Server");
		btnStartServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				start();
			}
		});
		panel.add(btnStartServer);
		
		JButton btnStopServer = new JButton("Stop Server");
		btnStopServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				if (serverWorker != null) {
					if (serverWorker.isRunning) {
						serverWorker.stop();
					}
				} 
			}
		});
		panel.add(btnStopServer);
	}

	protected void closeConnections() {
		// TODO close all open connections and sockets
		
	}

	protected static DomainRecordMessage lookup(String hostName, String recordType) {
		return DomainRecord.lookup(hostName, recordType);
	}
	
	private void start() {
		int port = Integer.parseInt(txfPort.getText());
		serverWorker = new ServerWorker(port);
		serverWorker.execute();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DNSServer server = new DNSServer("DNS Server");
		server.setLocationRelativeTo(null);
		server.setVisible(true);
		
//		System.out.println(lookup("google.de", "A"));
//		System.out.println(lookup("heise.de", "AAAA"));
//		System.out.println(lookup("google.com", "RP"));
//		System.out.println(lookup("google.com", "A"));
//		System.out.println(lookup("eveonline.com", "A"));
//		System.out.println(lookup("eveonline.com", "NS"));
//		System.out.println(lookup("hhrhere.com", "A"));
//		System.out.println(lookup("hhrhere.com", "NS"));
//		System.out.println(lookup("heise.de", "TXT"));
//		System.out.println(lookup("heise.de", "A"));
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
			logTextArea.append("Server listening at port "+port+LINE_SEPARATOR);
			ServerSocket serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(5000); // timeout 5s
			while (isRunning) {
				Socket clientSocket;
				try {
					clientSocket = serverSocket.accept();
					new RequestThread(clientSocket).run();
					
				} catch (SocketTimeoutException e) {
					//logTextArea.append("Timeout exceeded."+LINE_SEPARATOR);
				}

			}
			logTextArea.append("Server stopped."+LINE_SEPARATOR);
			return null;
		}
		
		public void stop() {
			logTextArea.append("Stopping server..."+LINE_SEPARATOR);
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
				logTextArea.append("Connection from "
						+ socket.getInetAddress().getHostAddress()
						+ LINE_SEPARATOR);
				ObjectInputStream ois = new ObjectInputStream(
						socket.getInputStream());
				String requestString = (String) ois.readObject();
				logTextArea.append("Request is: " + requestString
						+ LINE_SEPARATOR);
				// split request
				String[] split = requestString.split(REQUEST_SEPARATOR);
				String host = split[0];
				String record = split[1];
				// perform lookup
				DomainRecordMessage responseMessage = lookup(host, record);
				logTextArea.append("Lookup result is: " + responseMessage
						+ LINE_SEPARATOR);
				// send response
				ObjectOutputStream oos = new ObjectOutputStream(
						socket.getOutputStream());
				oos.writeObject(responseMessage.toString());
				oos.flush();
				oos.close();
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
