package com.gallatinsystems.framework.gwt.portlet.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class TreeDragController extends PickupDragController {


	public TreeDragController(AbsolutePanel boundaryPanel) {
		super(boundaryPanel, false);
		setBehaviorDragProxy(true);
		setBehaviorMultipleSelection(true);
	}

	


}
