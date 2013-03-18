/*******************************************************************************
 * Copyright (c) 2012 Sebastian Schmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Schmidt - initial API and implementation
 *******************************************************************************/
package edu.tum.cs.mylyn.internal.provisioning.breakpoints.ui;

import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.internal.ui.views.breakpoints.BreakpointsLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import edu.tum.cs.mylyn.provisioning.breakpoints.BreakpointsContributor;
import edu.tum.cs.mylyn.provisioning.ui.AbstractProvisioningElementLabelProvider;
import edu.tum.cs.mylyn.provisioning.ui.IProvisioningElementUiRoot;
import edu.tum.cs.mylyn.provisioning.ui.ResourceManager;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class BreakpointsElementLabelProvider extends AbstractProvisioningElementLabelProvider {

	private static final String BREAKPOINTS = "Breakpoints";
	private BreakpointsLabelProvider labelProvider = new BreakpointsLabelProvider();

	public class BreakpointsUiElementRoot implements IProvisioningElementUiRoot {
		@Override
		public String getContentType() {
			return BreakpointsContributor.CONTENT_TYPE;
		}
	}

	@Override
	public IProvisioningElementUiRoot getProvisioningElementRoot() {
		return new BreakpointsUiElementRoot();
	}

	@Override
	public String getContentType() {
		return BreakpointsContributor.CONTENT_TYPE;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof BreakpointsUiElementRoot) {
			return ResourceManager.getPluginImageDescriptor("edu.tum.cs.mylyn.provisioning.breakpoints.ui",
					"icons/breakpoints.gif").createImage();
		} else if(element instanceof IBreakpoint) {
			return labelProvider.getImage(element);
		} else {
			return null;
		}
	}

	@Override
	public StyledString getStyledText(Object element) {
		if (element instanceof BreakpointsUiElementRoot) {
			return new StyledString(BREAKPOINTS);
		} else if (element instanceof IBreakpoint) {
			return new StyledString(labelProvider.getText(element));
		} else {
			return null;
		}
	}

	@Override
	public String getText(Object element) {
		if (element instanceof BreakpointsUiElementRoot) {
			return BREAKPOINTS;
		} else if (element instanceof IBreakpoint) {
			return labelProvider.getText(element);
		} else {
			return null;
		}
	}

}
