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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

@SuppressWarnings("serial")
public class DNSClient extends JFrame implements ActionListener {
	private static final String RESET = "RESET";
	private static final String LOOKUP = "LOOKUP";
	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");
	private static final String RB_A_LABEL = "A (IPv4)";
	private JTextField txfName;
	private JTextArea textArea;
	private JTextField txfDnsIP;
	private JComboBox cmbxOTHER;
	private final ButtonGroup btngrpRecordType = new ButtonGroup();
	private JTextField txfDnsPort;
	private JRadioButton rdbtnOTHER;

	public DNSClient() {
		setMinimumSize(new Dimension(600, 500));
		setTitle("DNS Client");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// simply ignore if it doesn't work
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0,
				Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel lblDns = new JLabel("DNS IP:");
		GridBagConstraints gbc_lblDns = new GridBagConstraints();
		gbc_lblDns.anchor = GridBagConstraints.EAST;
		gbc_lblDns.insets = new Insets(0, 0, 5, 5);
		gbc_lblDns.gridx = 0;
		gbc_lblDns.gridy = 0;
		panel.add(lblDns, gbc_lblDns);

		txfDnsIP = new JTextField();
		txfDnsIP.setText("127.0.0.1");
		GridBagConstraints gbc_txfDnsIP = new GridBagConstraints();
		gbc_txfDnsIP.gridwidth = 3;
		gbc_txfDnsIP.insets = new Insets(0, 0, 5, 0);
		gbc_txfDnsIP.fill = GridBagConstraints.HORIZONTAL;
		gbc_txfDnsIP.gridx = 1;
		gbc_txfDnsIP.gridy = 0;
		panel.add(txfDnsIP, gbc_txfDnsIP);
		txfDnsIP.setColumns(10);

		JLabel lblNewLabel = new JLabel("DNS Port:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		panel.add(lblNewLabel, gbc_lblNewLabel);

		txfDnsPort = new JTextField();
		txfDnsPort.setText("53");
		GridBagConstraints gbc_txfDnsPort = new GridBagConstraints();
		gbc_txfDnsPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_txfDnsPort.gridwidth = 3;
		gbc_txfDnsPort.insets = new Insets(0, 0, 5, 0);
		gbc_txfDnsPort.gridx = 1;
		gbc_txfDnsPort.gridy = 1;
		panel.add(txfDnsPort, gbc_txfDnsPort);
		txfDnsPort.setColumns(10);

		JLabel lblName = new JLabel("Name:");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 2;
		panel.add(lblName, gbc_lblName);

		txfName = new JTextField();
		txfName.setText("google.de");
		GridBagConstraints gbc_txfName = new GridBagConstraints();
		gbc_txfName.gridwidth = 3;
		gbc_txfName.insets = new Insets(0, 0, 5, 0);
		gbc_txfName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txfName.gridx = 1;
		gbc_txfName.gridy = 2;
		panel.add(txfName, gbc_txfName);
		txfName.setColumns(10);

		JLabel lblRequestType = new JLabel("Record Type:");
		GridBagConstraints gbc_lblRequestType = new GridBagConstraints();
		gbc_lblRequestType.anchor = GridBagConstraints.EAST;
		gbc_lblRequestType.insets = new Insets(0, 0, 5, 5);
		gbc_lblRequestType.gridx = 0;
		gbc_lblRequestType.gridy = 3;
		panel.add(lblRequestType, gbc_lblRequestType);

		JRadioButton rdbtnA = new JRadioButton(RB_A_LABEL);
		rdbtnA.setActionCommand("A");

		rdbtnA.setSelected(true);
		btngrpRecordType.add(rdbtnA);
		GridBagConstraints gbc_rdbtnA = new GridBagConstraints();
		gbc_rdbtnA.anchor = GridBagConstraints.WEST;
		gbc_rdbtnA.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnA.gridx = 1;
		gbc_rdbtnA.gridy = 3;
		panel.add(rdbtnA, gbc_rdbtnA);

		JRadioButton rdbtnNS = new JRadioButton("NS (name server)");
		rdbtnNS.setActionCommand("NS");
		btngrpRecordType.add(rdbtnNS);
		GridBagConstraints gbc_rdbtnNS = new GridBagConstraints();
		gbc_rdbtnNS.gridwidth = 2;
		gbc_rdbtnNS.anchor = GridBagConstraints.WEST;
		gbc_rdbtnNS.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnNS.gridx = 2;
		gbc_rdbtnNS.gridy = 3;
		panel.add(rdbtnNS, gbc_rdbtnNS);

		JRadioButton rdbtnAAAA = new JRadioButton("AAAA (IPv6)");
		rdbtnAAAA.setActionCommand("AAAA");
		btngrpRecordType.add(rdbtnAAAA);
		GridBagConstraints gbc_rdbtnAAAA = new GridBagConstraints();
		gbc_rdbtnAAAA.anchor = GridBagConstraints.WEST;
		gbc_rdbtnAAAA.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnAAAA.gridx = 1;
		gbc_rdbtnAAAA.gridy = 4;
		panel.add(rdbtnAAAA, gbc_rdbtnAAAA);

		JRadioButton rdbtnRP = new JRadioButton("RP (responsible person)");
		rdbtnRP.setActionCommand("RP");
		btngrpRecordType.add(rdbtnRP);
		GridBagConstraints gbc_rdbtnRP = new GridBagConstraints();
		gbc_rdbtnRP.gridwidth = 2;
		gbc_rdbtnRP.anchor = GridBagConstraints.WEST;
		gbc_rdbtnRP.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnRP.gridx = 2;
		gbc_rdbtnRP.gridy = 4;
		panel.add(rdbtnRP, gbc_rdbtnRP);

		JRadioButton rdbtnLOC = new JRadioButton("LOC (geographical location)");
		rdbtnLOC.setActionCommand("LOC");
		btngrpRecordType.add(rdbtnLOC);
		GridBagConstraints gbc_rdbtnLOC = new GridBagConstraints();
		gbc_rdbtnLOC.anchor = GridBagConstraints.WEST;
		gbc_rdbtnLOC.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnLOC.gridx = 1;
		gbc_rdbtnLOC.gridy = 5;
		panel.add(rdbtnLOC, gbc_rdbtnLOC);

		JRadioButton rdbtnTXT = new JRadioButton("TXT (text)");
		rdbtnTXT.setActionCommand("TXT");
		btngrpRecordType.add(rdbtnTXT);
		GridBagConstraints gbc_rdbtnTXT = new GridBagConstraints();
		gbc_rdbtnTXT.gridwidth = 2;
		gbc_rdbtnTXT.anchor = GridBagConstraints.WEST;
		gbc_rdbtnTXT.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnTXT.gridx = 2;
		gbc_rdbtnTXT.gridy = 5;
		panel.add(rdbtnTXT, gbc_rdbtnTXT);

		JRadioButton rdbtnMX = new JRadioButton("MX (mail exchange)");
		rdbtnMX.setActionCommand("MX");
		btngrpRecordType.add(rdbtnMX);
		GridBagConstraints gbc_rdbtnMX = new GridBagConstraints();
		gbc_rdbtnMX.anchor = GridBagConstraints.WEST;
		gbc_rdbtnMX.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnMX.gridx = 1;
		gbc_rdbtnMX.gridy = 6;
		panel.add(rdbtnMX, gbc_rdbtnMX);

		rdbtnOTHER = new JRadioButton("other:");
		rdbtnOTHER.setActionCommand("OTHER");

		btngrpRecordType.add(rdbtnOTHER);
		GridBagConstraints gbc_rdbtnOTHER = new GridBagConstraints();
		gbc_rdbtnOTHER.anchor = GridBagConstraints.WEST;
		gbc_rdbtnOTHER.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnOTHER.gridx = 2;
		gbc_rdbtnOTHER.gridy = 6;
		panel.add(rdbtnOTHER, gbc_rdbtnOTHER);

		cmbxOTHER = new JComboBox();
		cmbxOTHER.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				rdbtnOTHER.setSelected(true);
			}
		});
		cmbxOTHER.setModel(new DefaultComboBoxModel(new String[] { "AFSDB",
				"APL", "CERT", "CNAME", "DHCID", "DLV", "DNAME", "DNSKEY",
				"DS", "HIP", "IPSECKEY", "KEY", "KX", "NAPTR", "NSEC", "NSEC3",
				"NSEC3PARAM", "PTR", "RRSIG", "SIG", "SOA", "SPF", "SRV",
				"SSHFP", "TA", "TKEY", "TSIG" }));
		GridBagConstraints gbc_txfOTHER = new GridBagConstraints();
		gbc_txfOTHER.insets = new Insets(0, 0, 5, 0);
		gbc_txfOTHER.fill = GridBagConstraints.HORIZONTAL;
		gbc_txfOTHER.gridx = 3;
		gbc_txfOTHER.gridy = 6;
		panel.add(cmbxOTHER, gbc_txfOTHER);

		JButton btnStartLookup = new JButton("Start Lookup");
		btnStartLookup.setActionCommand(LOOKUP);
		btnStartLookup.addActionListener(this);

		GridBagConstraints gbc_btnStartLookup = new GridBagConstraints();
		gbc_btnStartLookup.insets = new Insets(0, 0, 5, 5);
		gbc_btnStartLookup.anchor = GridBagConstraints.WEST;
		gbc_btnStartLookup.gridx = 1;
		gbc_btnStartLookup.gridy = 7;
		panel.add(btnStartLookup, gbc_btnStartLookup);

		JButton btnResetServer = new JButton("Reset Server");
		btnResetServer.setActionCommand(RESET);
		btnResetServer.addActionListener(this);

		GridBagConstraints gbc_btnResetServer = new GridBagConstraints();
		gbc_btnResetServer.anchor = GridBagConstraints.WEST;
		gbc_btnResetServer.insets = new Insets(0, 0, 5, 5);
		gbc_btnResetServer.gridx = 1;
		gbc_btnResetServer.gridy = 8;
		panel.add(btnResetServer, gbc_btnResetServer);

		JLabel lblResponse = new JLabel("Log:");
		GridBagConstraints gbc_lblResponse = new GridBagConstraints();
		gbc_lblResponse.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblResponse.insets = new Insets(0, 0, 0, 5);
		gbc_lblResponse.gridx = 0;
		gbc_lblResponse.gridy = 9;
		panel.add(lblResponse, gbc_lblResponse);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 9;
		panel.add(scrollPane, gbc_scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		scrollPane.setViewportView(textArea);
		textArea.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));

		JLabel lblStatus = new JLabel("Some status label");
		getContentPane().add(lblStatus, BorderLayout.SOUTH);

		// center on screen
		setLocationRelativeTo(null);
		// show frame
		setVisible(true);
	}

	private void appendText(String text) {
		textArea.append(text + LINE_SEPARATOR);
		textArea.setCaretPosition(textArea.getText().length());
	}
	
	public static void main(String[] args) {
		new DNSClient();
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String dnsAddress = txfDnsIP.getText();
		int dnsPort;
		try {
			dnsPort = Integer.parseInt(txfDnsPort.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null,
					"The port must be a number!", "Input error",
					JOptionPane.ERROR_MESSAGE);
			txfDnsPort.requestFocus();
			txfDnsPort.selectAll();
			return;
		}
		
		String recordType = "";
		String lookupName = "";
		
		if (evt.getActionCommand().equals(LOOKUP)) {
			lookupName = txfName.getText();
			JRadioButton selectedButton = null;
			Enumeration<AbstractButton> elements = btngrpRecordType
					.getElements();
			while (elements.hasMoreElements()) {
				AbstractButton nextElement = elements.nextElement();
				if (nextElement instanceof JRadioButton
						&& nextElement.isSelected()) {
					selectedButton = (JRadioButton) nextElement;
				}
			}
			if (selectedButton != null) {
				recordType = selectedButton.getActionCommand();
				if (recordType.equals(rdbtnOTHER.getActionCommand())) {
					recordType = cmbxOTHER.getSelectedItem().toString();
				}
				appendText("Requesting " + recordType + " for "
						+ lookupName + " from " + dnsAddress + ":"
						+ dnsPort);
			}
		}
		else if (evt.getActionCommand().equals(RESET)) {
			appendText("Requesting the server to reset its records table.");
		}
		
		Request request = null;
		if (evt.getActionCommand().equals(LOOKUP)) {
			request = new Request(lookupName, recordType); 
		}
		else if (evt.getActionCommand().equals(RESET)) {
			request = new Request();
		}
		
		try {
			String response = sendRequest(request, dnsAddress, dnsPort);
			if (response.isEmpty())
				appendText("The result was empty or there was no result at all." + LINE_SEPARATOR);
			else
				appendText(response + LINE_SEPARATOR);
			} catch (ClassNotFoundException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "The server sent rubbish", JOptionPane.ERROR_MESSAGE);
			} catch (UnknownHostException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(),
						"Host unknown", JOptionPane.ERROR_MESSAGE);
				return;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(),
						"IO Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
	}
	
	/**
	 * Sends a String object 
	 * @param request
	 * @param targetAddress
	 * @param targetPort
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private String sendRequest(Request request, String targetAddress, int targetPort) throws UnknownHostException, IOException, ClassNotFoundException {
		Socket socket = null;
		socket = new Socket(targetAddress, targetPort);
		ObjectOutputStream oos = new ObjectOutputStream(
				socket.getOutputStream());
		// send request
		oos.writeObject(request);
		oos.flush();

		// read response
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		String response;
		response = (String) ois.readObject();
		socket.close();
		return response;
	}


}
