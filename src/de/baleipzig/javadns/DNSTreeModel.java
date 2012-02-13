package de.baleipzig.javadns;

import java.util.Arrays;
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
	
	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public boolean isLeaf(Object node) {
		// root and hosts are no leaves
		if (node.equals(root) || DomainRecord.getRecords().containsKey(node)) {
			return false;
		}
		// attributes are leaves
		return true;
		
	}
	
	@Override
	public int getChildCount(Object parent) {
		HashMap<String, HashMap<String, Attribute>> records = DomainRecord
				.getRecords();
		// children of root = hosts
		if (parent.equals(root)) {
			return records.size(); 
		}
		// children of host = attributes
		return records.get(parent).size();
	}
	
	@Override
	public Object getChild(Object parent, int index) {
		HashMap<String, HashMap<String, Attribute>> records = DomainRecord
				.getRecords();
		// parent is root --> child is host
		if (parent.equals(root)) {
			Object[] hostArray = records.keySet().toArray();
			Arrays.sort(hostArray);
			return hostArray[index];
		}
		// parent is host --> child is attribute
		else {
			Object[] attributeKeys = records.get(parent).keySet().toArray();
			return records.get(parent).get(attributeKeys[index]);
		}
	}
	
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		HashMap<String, HashMap<String, Attribute>> records = DomainRecord
				.getRecords();
		// parent is root --> child is host
		if (parent.equals(root)) {
			Object[] hostArray = records.keySet().toArray();
			Arrays.sort(hostArray);
			for (int i=0; i<hostArray.length; i++) {
				if (child.equals(hostArray[i])) {
					return i;
				}
			}
		}
		// parent is host --> child is attribute
		else {
			Object[] attributeKeys = records.get(parent).keySet().toArray();
			for (int i=0; i<attributeKeys.length; i++) {
				if (records.get(parent).get(attributeKeys[i]).equals(child)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	@Override
	public void addTreeModelListener(TreeModelListener listener) {
		treeModelListeners.addElement(listener);
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
	
	protected void fireTreeStructureChanged(Object nodeDown) {
		TreeModelEvent evt = new TreeModelEvent(this, new Object[] { nodeDown });
		for (TreeModelListener tml : treeModelListeners) {
			tml.treeStructureChanged(evt);
		}
	}

}
