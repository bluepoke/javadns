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
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class DNSServer extends JFrame {
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
		
		JTextArea logTextArea = new JTextArea();
		logTextArea.setEditable(false);
		logScrollPane.setViewportView(logTextArea);
		
		JScrollPane treeScrollPane = new JScrollPane();
		splitPane.setRightComponent(treeScrollPane);
		
		JTree recordTree = new JTree();
		treeScrollPane.setViewportView(recordTree);
	}

	protected void closeConnections() {
		// TODO close all open connections and sockets
		
	}

	protected static DomainRecordMessage lookup(String hostName, String recordType) {
		return DomainRecord.lookup(hostName, recordType);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DNSServer server = new DNSServer("DNS Server");
		server.setLocationRelativeTo(null);
		server.setVisible(true);
		
		System.out.println(lookup("google.de", "A"));
		System.out.println(lookup("heise.de", "AAAA"));
		System.out.println(lookup("google.com", "RP"));
		System.out.println(lookup("google.com", "A"));
		System.out.println(lookup("eveonline.com", "A"));
		System.out.println(lookup("eveonline.com", "NS"));
		System.out.println(lookup("hhrhere.com", "A"));
		System.out.println(lookup("hhrhere.com", "NS"));
		System.out.println(lookup("heise.de", "TXT"));
		System.out.println(lookup("heise.de", "A"));
	}	
}
