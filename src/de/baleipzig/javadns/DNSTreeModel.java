package de.baleipzig.javadns;

import java.util.HashMap;
import java.util.Vector;

import javax.naming.directory.Attribute;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class DNSTreeModel implements TreeModel {
	private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();
	private Object root;

	public DNSTreeModel(Object root) {
		this.root = root;
	}

	protected void fireTreeStructureChanged(Object oldRoot) {
		TreeModelEvent evt = new TreeModelEvent(this, new Object[] { oldRoot });
		for (TreeModelListener tml : treeModelListeners) {
			tml.treeStructureChanged(evt);
		}
	}

	@Override
	public void addTreeModelListener(TreeModelListener listener) {
		treeModelListeners.addElement(listener);
	}

	@Override
	public Object getChild(Object parent, int index) {
		HashMap<String, HashMap<String, Attribute>> records = DomainRecord
				.getRecords();
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		HashMap<String, HashMap<String, Attribute>> records = DomainRecord
				.getRecords();
		if (records.containsKey(parent))
			return records.get(parent).size();
		else
			return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		HashMap<String, HashMap<String, Attribute>> records = DomainRecord
				.getRecords();
		if (records.containsKey(child)) {
			
		}
			
		
		
		if (records.containsKey(parent))
			Iterator it = records.get(parent).keySet().iterator();
			
			return 0;
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public boolean isLeaf(Object node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener listener) {
		treeModelListeners.removeElement(listener);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		System.out.println("*** valueForPathChanged : " + path + " --> "
				+ newValue);
	}

}
