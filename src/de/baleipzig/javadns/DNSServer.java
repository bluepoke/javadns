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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.naming.directory.Attribute;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.tree.TreePath;

@SuppressWarnings("serial")
public class DNSServer extends JFrame {
	private static final String ALL_AVAILABLE_IP_ADDRESSES = "All available IP addresses";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static int DEFAULT_PORT = 53;
	private JTextArea logTextArea;
	private JButton btnStartServer, btnStopServer;
	private JTextField txfPort;
	private ServerWorker serverWorker;
	private JComboBox cmbxIP;
	private DNSTreeModel treeModel;
	private JTree recordTree;

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
		splitPane.setEnabled(true);
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
		
		recordTree = new JTree();
		treeModel = new DNSTreeModel("DNS Records");
		recordTree.setModel(treeModel);
		treeScrollPane.setViewportView(recordTree);
		recordTree.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel lblIP = new JLabel("IP:");
		panel.add(lblIP);
		
		cmbxIP = new JComboBox();
		cmbxIP.addItem(ALL_AVAILABLE_IP_ADDRESSES);
		panel.add(cmbxIP);
		
		// read available IPs
		try {
			for (InetAddress address : InetAddress.getAllByName(InetAddress.getLocalHost().getHostName())) {
				cmbxIP.addItem(address);
			}
		} catch (UnknownHostException e) {
		}
		
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

	/**
	 * Performs a lookup.
	 * @param hostName Host to look up.
	 * @param recordType Record to look up for this host.
	 * @return 
	 */
	protected static DomainRecordMessage lookup(String hostName, String recordType) {
		return DomainRecord.lookup(hostName, recordType);
	}
	
	private void start() {
		cmbxIP.setEnabled(false);
		btnStartServer.setEnabled(false);
		btnStopServer.setEnabled(true);
		int port = Integer.parseInt(txfPort.getText());
		InetAddress address = null;
		if (cmbxIP.getSelectedItem() instanceof InetAddress) {
			address = (InetAddress) cmbxIP.getSelectedItem();
		}
		serverWorker = new ServerWorker(address, port);
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
	
	/**
	 * This class waits for connections and passes each incoming
	 * connection on to a separate thread that handles it.
	 */
	private class ServerWorker extends SwingWorker<Object, Object> {

		private int port;
		private boolean isRunning = true;
		private InetAddress address;

		public ServerWorker(InetAddress address, int port) {
			super();
			this.port = port;
			this.address = address;
		}
		
		@Override
		protected Object doInBackground() throws Exception {
			appendText("Server listening at "+(address==null?"all available IP addresses":address.getHostAddress())+":"+port);
			ServerSocket serverSocket = new ServerSocket(port, -1, address);
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
			cmbxIP.setEnabled(true);
			return null;
		}		
		
		public void stop() {
			appendText("Stopping server..."+LINE_SEPARATOR);
			btnStopServer.setEnabled(false);
			isRunning = false;
		}
		
	}
	
	/**
	 * This class handles the requests.
	 * It reads the request from the socket, performs lookups, resets or registration
	 * and sends a message back to the requester.
	 */
	private class RequestThread extends Thread {

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
				Request request = (Request) ois.readObject();
				appendText("Request is: " + request);
				
				String response = "";
				if(request.getType() == Request.RESET) {
					appendText("Trying to reset the records table.");
					if (DomainRecord.reset()) {
						response = "Reset successful." + LINE_SEPARATOR;
						appendText("Sending: 'Reset successful'." + LINE_SEPARATOR);
						// remove selection from tree
						recordTree.setSelectionPath(null);
					}
					else {
						response = "Sending: 'Reset was not possible'." + LINE_SEPARATOR;
					}
				}
				else if (request.getType() == Request.LOOKUP){
					// perform lookup
					DomainRecordMessage responseMessage = lookup(request.getHostName(), request.getRecord());
					appendText("Lookup result is: " + LINE_SEPARATOR + responseMessage
							+ LINE_SEPARATOR);
					response = responseMessage.getDnsResult().toString();
					response = response.substring(1, response.length() - 1);
				}
				else if (request.getType() == Request.IDENTIFY) {
					HashMap<String, Attribute> result = DomainRecord.addRecord(request.getHostName(), request.getAttributes());
					if (result == null) {
						appendText("Host '" + socket.getInetAddress().getHostAddress() 
								+ "' is now identified as '" + request.getHostName() + "'" + LINE_SEPARATOR);
						response = "You are now identified as '" + request.getHostName()
								+ "'." + LINE_SEPARATOR;
					}
					else {
						appendText("Host '" + socket.getInetAddress().getHostAddress() 
								+ "' replaces identification of '" + request.getHostName() + "'" + LINE_SEPARATOR);
						response = "You are now identified as '" + request.getHostName()
								+ "' and replaced the previously held record." + LINE_SEPARATOR;
					}
				}
				
				// keep selection
				TreePath selectionPath = recordTree.getSelectionPath();
				// refresh Tree
				treeModel.fireTreeStructureChanged(treeModel.getRoot());
				// restore selection
				recordTree.setSelectionPath(selectionPath);
				// send response
				ObjectOutputStream oos = new ObjectOutputStream(
						socket.getOutputStream());
				
				oos.writeObject(response);
				oos.flush();
				oos.close();
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
