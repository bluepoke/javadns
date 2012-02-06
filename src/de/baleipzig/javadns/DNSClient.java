package de.baleipzig.javadns;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class DNSClient extends JFrame {
	private JTextField txfName;
	private JTextArea textArea;
	public DNSClient() {
		setMinimumSize(new Dimension(400, 400));
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
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblName = new JLabel("Name:");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		panel.add(lblName, gbc_lblName);
		
		txfName = new JTextField();
		txfName.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_txfName = new GridBagConstraints();
		gbc_txfName.insets = new Insets(0, 0, 5, 0);
		gbc_txfName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txfName.gridx = 1;
		gbc_txfName.gridy = 0;
		panel.add(txfName, gbc_txfName);
		txfName.setColumns(10);
		
		JButton btnStartLookup = new JButton("Start Lookup");
		btnStartLookup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textArea.setText("");
				String name = txfName.getText();
				textArea.append("Looking up "+name);
				// TODO: Perform Lookup
			}
		});
		GridBagConstraints gbc_btnStartLookup = new GridBagConstraints();
		gbc_btnStartLookup.insets = new Insets(0, 0, 5, 0);
		gbc_btnStartLookup.anchor = GridBagConstraints.EAST;
		gbc_btnStartLookup.gridx = 1;
		gbc_btnStartLookup.gridy = 1;
		panel.add(btnStartLookup, gbc_btnStartLookup);
		
		JLabel lblResponse = new JLabel("Response:");
		GridBagConstraints gbc_lblResponse = new GridBagConstraints();
		gbc_lblResponse.anchor = GridBagConstraints.NORTH;
		gbc_lblResponse.insets = new Insets(0, 0, 0, 5);
		gbc_lblResponse.gridx = 0;
		gbc_lblResponse.gridy = 2;
		panel.add(lblResponse, gbc_lblResponse);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 1;
		gbc_textArea.gridy = 2;
		panel.add(textArea, gbc_textArea);
		
		JLabel lblStatus = new JLabel("Some status label");
		getContentPane().add(lblStatus, BorderLayout.SOUTH);
		
		//center on screen
		setLocationRelativeTo(null);
		// show frame
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new DNSClient();
	}

}
