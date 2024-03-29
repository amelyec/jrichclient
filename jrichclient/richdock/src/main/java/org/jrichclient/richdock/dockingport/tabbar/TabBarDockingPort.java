/*
 * JRichClient -- Java libraries for rich client applications.
 * Copyright (C) 2007 CompuLink, Ltd. 409 Vandiver Drive #4-200,
 * Columbia, Missouri 65202-1562, All Rights Reserved.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jrichclient.richdock.dockingport.tabbar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.dnd.DropTargetDropEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;
import javax.swing.event.SwingPropertyChangeSupport;

import org.jrichclient.richdock.Dockable;
import org.jrichclient.richdock.DockingPort;
import org.jrichclient.richdock.helper.DropHelper;
import org.jrichclient.richdock.helper.IndexedLocationDockingPortHelper;
import org.jrichclient.richdock.utils.XMLUtils;

public class TabBarDockingPort implements DockingPort<Integer> {
// Bound property names ********************************************************
	
	public static final String PROPERTYNAME_SELECTED_DOCKABLE = "selectedDockable";
	
// Private instance variables **************************************************
	
	private final PropertyChangeSupport pcs;
	private final Rotation rotation;
	private final Box box;
	private final TabBarDockingPortHelper helper;
	private final TabBarDropHelper dropHelper;
	private final List<TabBarComponent> tabList;
	private Dockable selectedDockable;

// Constructor *****************************************************************
	
	public TabBarDockingPort() {
		this(Rotation.ROTATION_0);
	}
		
	public TabBarDockingPort(Rotation rotation) {
		pcs = new SwingPropertyChangeSupport(this);
		this.rotation = rotation;
		box = new Box(getAxis(rotation));
		
		this.helper = new TabBarDockingPortHelper();
		this.dropHelper = new TabBarDropHelper(box);
		dropHelper.setDropable(true);
		
		tabList = new ArrayList<TabBarComponent>();
	}
	
	private static int getAxis(Rotation rotation) {
		if (rotation == Rotation.ROTATION_0 || rotation == Rotation.ROTATION_180)
			return BoxLayout.X_AXIS;
		
		return BoxLayout.Y_AXIS;
	}
	
// Clone ***********************************************************************
	
	@Override
	public TabBarDockingPort clone() throws CloneNotSupportedException {
		return (TabBarDockingPort)XMLUtils.duplicate(this, false);
	}
	
// PropertyChangeBroadcaster ***************************************************
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(propertyName, listener);
	}

	public PropertyChangeListener[] getPropertyChangeListeners() {
		return pcs.getPropertyChangeListeners();
	}

	public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
		return pcs.getPropertyChangeListeners(propertyName);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(propertyName, listener);
	}
	
// Rotation ********************************************************************
	
	public Rotation getRotation() {
		return rotation; 
	}
		
// Selected dockable ***********************************************************
	
	public Dockable getSelectedDockable() {
		return selectedDockable;
	}
	
	public void setSelectedDockable(Dockable selectedDockable) {
		Dockable oldSelectedDockable = getSelectedDockable();
		if (oldSelectedDockable != null)
			oldSelectedDockable.removePropertyChangeListener(helper.getDockableListener());
		
		for (TabBarComponent comp : tabList)
			comp.setSelected(comp.getDockable() == selectedDockable);
		this.selectedDockable = selectedDockable;
		
		if (selectedDockable != null) {
			setTitle(selectedDockable.getTitle());
			setIconFile(selectedDockable.getIconFile());
			setToolTipText(selectedDockable.getToolTipText());
			setPopupMenu(selectedDockable.getPopupMenu());
			selectedDockable.addPropertyChangeListener(helper.getDockableListener());
		} else {
			setTitle("");
			setIconFile(null);
			setToolTipText(null);
			setPopupMenu(null);
		}
		
		pcs.firePropertyChange(PROPERTYNAME_SELECTED_DOCKABLE, 
			oldSelectedDockable, getSelectedDockable());
	}
					
// Title ***********************************************************************
	
	public String getTitle() {
		return helper.getTitle();
	}

	public void setTitle(String title) {
		helper.setTitle(title);
	}
	
// IconFile ********************************************************************

	public String getIconFile() {
		return helper.getIconFile();
	}
	
	public void setIconFile(String fileName) {
		helper.setIconFile(fileName);
	}

// ToolTipText *****************************************************************
	
	public String getToolTipText() {
		return helper.getToolTipText();
	}

	public void setToolTipText(String toolTipText) {
		box.setToolTipText(toolTipText);
		helper.setToolTipText(toolTipText);
	}

// PopupMenu *******************************************************************
	
	public JPopupMenu getPopupMenu() {
		return helper.getPopupMenu();
	}

	public void setPopupMenu(JPopupMenu popupMenu) {
		helper.setPopupMenu(popupMenu);
	}
	
// Dragable ********************************************************************
	
	public boolean isDragable() {
		return helper.isDragable();
	}
	
	public void setDragable(boolean dragable) {
		helper.setDragable(dragable);
	}
	
// Floatable *******************************************************************
	
	public boolean isFloatable() {
		return helper.isFloatable();
	}

	public void setFloatable(boolean floatable) {
		helper.setFloatable(floatable);
	}

// Dropable ********************************************************************
	
	public boolean isDropable() {
		return dropHelper.isDropable();
	}

	public void setDropable(boolean dropable) {
		dropHelper.setDropable(dropable);
	}
	
// DockingPort *****************************************************************

	public DockingPort<?> getDockingPort() {
		return helper.getDockingPort();
	}

	public void setDockingPort(DockingPort<?> dockingPort) {
		helper.setDockingPort(dockingPort);
	}
	
// Component *******************************************************************
	
	public Box getComponent() {
		return box;
	}
	
// Dock/Undock *****************************************************************

	public void dock(Dockable dockable, Integer location) {
		helper.dock(dockable, location);
	}

	public void undock(Dockable dockable, boolean disposeOnEmpty) {
		helper.undock(dockable, disposeOnEmpty);
	}
			
// Lookups *********************************************************************

	public int getDockableCount() {
		return helper.getDockableCount();
	}

	public Dockable getDockable(Integer location) {
		return helper.getDockable(location);
	}

	public Integer getLocation(Dockable dockable) {
		return helper.getLocation(dockable);
	}

	public Iterator<Dockable> iterator() {
		return helper.iterator();
	}

// BoxHelper *******************************************************************
	
	private class TabBarDockingPortHelper extends IndexedLocationDockingPortHelper {

		protected TabBarDockingPortHelper() {
			super(TabBarDockingPort.this);
		}

		@Override
		protected void install(Dockable dockable, Integer location) {
			TabBarComponent comp = new TabBarComponent(
				TabBarDockingPort.this, dockable, getRotation());
			tabList.add(location.intValue(), comp);
			updateLayout();
			box.revalidate();
			box.repaint();
		}

		@Override
		protected void uninstall(Dockable dockable, Integer location) {
			tabList.get(location.intValue()).dispose();
			tabList.remove(location.intValue());
			
			if (dockable == getSelectedDockable())
				setSelectedDockable(null);
			
			updateLayout();
			box.revalidate();
			box.repaint();
		}
		
		private void updateLayout() {
			while (box.getComponentCount() > 0)
				box.remove(box.getComponent(0));

			int count = tabList.size();
			Dimension gapSize = new Dimension(5, 5);
			for (int index = 0; index < count; index++) {
				box.add(tabList.get(index));
				if (index < count - 1)
					box.add(Box.createRigidArea(gapSize));
			}
			
			if (box.getComponentCount() == 0)
				box.add(Box.createRigidArea(gapSize));
		}

		@Override
		protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
			pcs.firePropertyChange(propertyName, oldValue, newValue);
		}
	}
	
// BoxDropHelper ***************************************************************
	
	private class TabBarDropHelper extends DropHelper {

		public TabBarDropHelper(Component component) {
			super(component);
		}

		@Override
		protected boolean dropDockable(Dockable dockable, DropTargetDropEvent event) {
			Integer dropLocation = getDropLocation(event.getLocation());
			
			Integer oldLocation = getLocation(dockable);
			if (dropLocation != oldLocation)
				if (oldLocation == null || oldLocation > dropLocation)
					dock(dockable, dropLocation);
				else dock(dockable, dropLocation - 1);

			return true;
		}

		private Integer getDropLocation(Point point) {
			int count = getDockableCount();
			Component comp = box.getComponentAt(point);
			
			for (int index = 0; index < count; index++) {
				if (comp == tabList.get(index))
					return index;
			}
			
			return count;
		}

		@Override
		protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
			pcs.firePropertyChange(propertyName, oldValue, newValue);
		}
	}
		
// CanClose ********************************************************************
	
	public boolean canClose() {
		return helper.canClose();
	}

// DisposeOnEmpty **************************************************************
	
	public boolean getDisposeOnEmpty() {
		return helper.getDisposeOnEmpty();
	}
	
	public void setDisposeOnEmpty(boolean disposeOnEmpty) {
		helper.setDisposeOnEmpty(disposeOnEmpty);
	}
	
// Dispose *********************************************************************
	
	public boolean isDisposed() {
		return helper.isDisposed();
	}

	public void dispose() {
		helper.dispose();
	}

}
