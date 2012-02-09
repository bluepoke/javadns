package de.baleipzig.javadns;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class RegisterDialog extends JDialog {
		
	private HashMap<JCheckBox, JTextField> map = new HashMap<JCheckBox, JTextField>();
	
	private static final String[] recordTypes = new String[] { 
		"A", "AAAA", "LOC", "MX", "NS", "RP", "TXT", "AFSDB",
		"APL", "CERT", "CNAME", "DHCID", "DLV", "DNAME", "DNSKEY",
		"DS", "HIP", "IPSECKEY", "KEY", "KX", "NAPTR", "NSEC", "NSEC3",
		"NSEC3PARAM", "PTR", "RRSIG", "SIG", "SOA", "SPF", "SRV",
		"SSHFP", "TA", "TKEY", "TSIG" };

	public static final int OK = 0;
	public static final int CANCEL = 1;
	
	private JPanel pnlInputPanel;
	private int buttonClicked = CANCEL;
	private JTextField txfHostName;
	
	public RegisterDialog() {
		setMinimumSize(new Dimension(400, 400));
		setPreferredSize(new Dimension(400, 400));
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Register");
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		
		pnlInputPanel = new JPanel();
		//getContentPane().add(pnlInputPanel, BorderLayout.CENTER);
		GridBagLayout gbl_pnlInputPanel = new GridBagLayout();
		gbl_pnlInputPanel.columnWidths = new int[]{34, 86, 0};
		gbl_pnlInputPanel.rowHeights = new int[]{14, 23, 0};
		gbl_pnlInputPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_pnlInputPanel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		pnlInputPanel.setLayout(gbl_pnlInputPanel);

		//content
		initializeContents();
		
		JScrollPane scrollPane = new JScrollPane(pnlInputPanel);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JPanel pnlButtonPanel = new JPanel();
		getContentPane().add(pnlButtonPanel, BorderLayout.SOUTH);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonClicked = OK;
				setVisible(false);
			}
		});
		pnlButtonPanel.add(btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked = CANCEL;
				setVisible(false);
			}
		});
		pnlButtonPanel.add(btnCancel);
		
		setLocationRelativeTo(getOwner());
	}

	private void initializeContents() {
		
		// header
		int row = 0;
		
		JLabel lblHeaderRecord = new JLabel("Record");
		GridBagConstraints gbc_lblHeaderRecord = new GridBagConstraints();
		gbc_lblHeaderRecord.insets = new Insets(0, 0, 5, 5);
		gbc_lblHeaderRecord.gridx = 0;
		gbc_lblHeaderRecord.gridy = row;
		pnlInputPanel.add(lblHeaderRecord, gbc_lblHeaderRecord);

		JLabel lblHeaderValue = new JLabel("Value");
		GridBagConstraints gbc_lblHeaderValue = new GridBagConstraints();
		gbc_lblHeaderValue.insets = new Insets(0, 0, 5, 0);
		gbc_lblHeaderValue.gridx = 1;
		gbc_lblHeaderValue.gridy = row;
		pnlInputPanel.add(lblHeaderValue, gbc_lblHeaderValue);
		
		row++;
		
		JLabel lblHostName = new JLabel("Hostname");
		GridBagConstraints gbc_lblHostName = new GridBagConstraints();
		gbc_lblHostName.insets = new Insets(0, 0, 5, 5);
		gbc_lblHostName.gridx = 0;
		gbc_lblHostName.gridy = row;
		pnlInputPanel.add(lblHostName, gbc_lblHostName);

		txfHostName = new JTextField();
		GridBagConstraints gbc_txfHostName = new GridBagConstraints();
		gbc_txfHostName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txfHostName.gridx = 1;
		gbc_txfHostName.gridy = row;
		gbc_txfHostName.weightx = 1;
		pnlInputPanel.add(txfHostName, gbc_txfHostName);
		txfHostName.setColumns(10);
		
		row++;
		
		for (int i=0; i<recordTypes.length; i++) {
			String recordType = recordTypes[i];
			
			JCheckBox box = new JCheckBox(recordType);
			GridBagConstraints boxConstraints = new GridBagConstraints();
			boxConstraints.insets = new Insets(0, 0, 0, 5);
			boxConstraints.gridx = 0;
			boxConstraints.gridy = row;
			boxConstraints.anchor = GridBagConstraints.WEST;
			pnlInputPanel.add(box, boxConstraints);
			
			JTextField textField = new JTextField();
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.gridx = 1;
			gbc_textField.gridy = row;
			gbc_textField.weightx = 1;
			pnlInputPanel.add(textField, gbc_textField);
			textField.setColumns(10);

			map.put(box, textField);
			row++;
		}		
	}
	
	public int getButtonClicked() {
		return buttonClicked;
	}
	
	public String getHostName() {
		return txfHostName.getText();
	}
	
	public HashMap<String, Attribute> getAttributes() {
		HashMap<String, Attribute> attributeMap = new HashMap<String, Attribute>();
		for (JCheckBox cbx : map.keySet()) {
			if (cbx.isSelected()) {
				BasicAttribute attribute = new BasicAttribute(cbx.getText(), ((JTextField)map.get(cbx)).getText());
				attributeMap.put(cbx.getText(), attribute);
			}
		}
		return attributeMap;
	}
}
