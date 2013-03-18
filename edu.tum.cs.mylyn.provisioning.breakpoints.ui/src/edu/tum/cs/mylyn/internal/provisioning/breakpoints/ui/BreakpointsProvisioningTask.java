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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;

import edu.tum.cs.mylyn.provisioning.breakpoints.BreakpointsContributor;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;
import edu.tum.cs.mylyn.provisioning.ui.IContextProvisioningTask;
import edu.tum.cs.mylyn.provisioning.ui.ResourceManager;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class BreakpointsProvisioningTask implements IContextProvisioningTask {

	@Override
	public String getName() {
		return "Breakpoints";
	}

	@Override
	public String getDescription() {
		return "Import Breakpoints";
	}

	@Override
	public String getId() {
		return BreakpointsContributor.CONTRIBUTOR_ID;
	}

	@Override
	public ImageDescriptor getImage() {
		return ResourceManager.getPluginImageDescriptor("edu.tum.cs.mylyn.provisioning.breakpoints.ui",
				"icons/breakpoints.gif");
	}

	@Override
	public boolean executionRequired(ProvisioningContext context) {
		return getRequiredBreakpoints(context).size() > 0;
	}

	@Override
	public boolean executionRequired(ProvisioningContext context, List<String> elementHandles) {
		for (IBreakpoint breakpoint : getRequiredBreakpoints(context)) {
			if (elementHandles
					.contains(breakpoint.getMarker().getAttribute(BreakpointsContributor.BREAKPOINT_ID, null))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Wizard getWizard(ProvisioningContext context, IProgressMonitor monitor) {
		return new BreakpointsWizard(getRequiredBreakpoints(context));
	}

	@Override
	public Wizard getWizard(ProvisioningContext context, List<String> elementHandles, IProgressMonitor monitor) {
		return new BreakpointsWizard(getRequiredBreakpoints(context), elementHandles);
	}

	private List<IBreakpoint> getRequiredBreakpoints(ProvisioningContext context) {
		List<IBreakpoint> result = new ArrayList<IBreakpoint>();
		List<IBreakpoint> contextBreakpoints = getContextBreakpoints(context);
		IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		List<IBreakpoint> platformBreakpoints = Arrays.asList(breakpointManager.getBreakpoints());
		for (IBreakpoint contextBreakpoint : contextBreakpoints) {
			if (!platformBreakpoints.contains(contextBreakpoint)) {
				result.add(contextBreakpoint);
			}
		}
		return result;
	}

	private List<IBreakpoint> getContextBreakpoints(ProvisioningContext context) {
		List<IBreakpoint> result = new ArrayList<IBreakpoint>();
		for (ProvisioningElement provisioningElement : context
				.getProvisioningElements(BreakpointsContributor.CONTENT_TYPE)) {
			Object object = context.getObject(provisioningElement);
			if (object instanceof IBreakpoint) {
				result.add((IBreakpoint) object);
			}
		}
		return result;
	}
}
