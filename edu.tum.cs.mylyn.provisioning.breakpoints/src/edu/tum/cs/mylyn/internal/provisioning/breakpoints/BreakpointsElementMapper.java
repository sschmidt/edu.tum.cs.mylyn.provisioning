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
package edu.tum.cs.mylyn.internal.provisioning.breakpoints;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.IInteractionContext;

import edu.tum.cs.mylyn.provisioning.breakpoints.BreakpointsContributor;
import edu.tum.cs.mylyn.provisioning.core.AbstractProvisioningElementMapper;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class BreakpointsElementMapper extends AbstractProvisioningElementMapper {

	private final IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
	private final Set<IBreakpoint> breakpointCache = new HashSet<IBreakpoint>();

	@Override
	public Object getObject(IInteractionContext context, ProvisioningElement handle) {
		IBreakpoint breakpoint = getBreakpointFromManager(handle);
		if (breakpoint != null) {
			return breakpoint;
		}
		breakpoint = getBreakpointFromCache(handle);
		if (breakpoint != null) {
			return breakpoint;
		}
		return loadBreakpointFromContext(context, handle);
	}

	private Object loadBreakpointFromContext(IInteractionContext context, ProvisioningElement handle) {
		IBreakpoint breakpoint = BreakpointsContextUtil.loadBreakpoint(context, handle, new NullProgressMonitor());
		if (breakpoint != null) {
			breakpointCache.add(breakpoint);
			return breakpoint;
		}
		return null;
	}

	private IBreakpoint getBreakpointFromCache(ProvisioningElement handle) {
		for (IBreakpoint breakpoint : breakpointCache) {
			if (handle.getIdentifier().equals(
					breakpoint.getMarker().getAttribute(BreakpointsContributor.BREAKPOINT_ID, null))) {
				return breakpoint;
			}
		}
		return null;
	}

	private IBreakpoint getBreakpointFromManager(ProvisioningElement handle) {
		for (IBreakpoint currentBreakpoint : breakpointManager.getBreakpoints()) {
			createUniqueId(currentBreakpoint);
			if (handle.getIdentifier().equals(
					currentBreakpoint.getMarker().getAttribute(BreakpointsContributor.BREAKPOINT_ID, null))) {
				return currentBreakpoint;
			}
		}
		return null;
	}

	@Override
	public ProvisioningElement createProvisioningElement(Object object) {
		if (object instanceof IBreakpoint) {
			IBreakpoint breakpoint = (IBreakpoint) object;
			createUniqueId(breakpoint);
			return new ProvisioningElement(getContentType(), breakpoint.getMarker().getAttribute(
					BreakpointsContributor.BREAKPOINT_ID, null));
		}
		return null;
	}

	private void createUniqueId(IBreakpoint breakpoint) {
		if (breakpoint.getMarker().getAttribute(BreakpointsContributor.BREAKPOINT_ID, null) == null) {
			try {
				breakpoint.getMarker().setAttribute(BreakpointsContributor.BREAKPOINT_ID, UUID.randomUUID().toString());
			} catch (CoreException e) {
				StatusHandler.log(new Status(Status.WARNING, Activator.ID_PLUGIN, e.getMessage(), e));
			}
		}
	}

	@Override
	public String getContentType() {
		return BreakpointsContributor.CONTENT_TYPE;
	}
}
