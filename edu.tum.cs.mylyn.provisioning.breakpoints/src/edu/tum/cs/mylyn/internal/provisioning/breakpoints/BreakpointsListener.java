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

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.IBreakpointsListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.mylyn.context.core.IInteractionContext;

import edu.tum.cs.mylyn.internal.provisioning.core.ProvisioningCorePlugin;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;

/**
 * @author Sebastian Schmidt
 */
public class BreakpointsListener implements IBreakpointsListener {

	private final ProvisioningContext provisioningContext;

	public BreakpointsListener(final IInteractionContext context) {
		this.provisioningContext = ProvisioningCorePlugin.getProvisioningContextManager().getProvisioningContext(
				context);
	}

	public void breakpointsAdded(IBreakpoint[] breakpoints) {
		breakpointsChanged(breakpoints, new IMarkerDelta[breakpoints.length]);
	}

	public void breakpointsRemoved(IBreakpoint[] breakpoints, IMarkerDelta[] deltas) {
		for (IBreakpoint breakpoint : breakpoints) {
			ProvisioningElement element = provisioningContext.createProvisioningElement(breakpoint);
			provisioningContext.removeProvisioningElement(element);
		}
	}

	public void breakpointsChanged(IBreakpoint[] breakpoints, IMarkerDelta[] deltas) {
		for (int i = 0; i < breakpoints.length; i++) {
			IBreakpoint breakpoint = breakpoints[i];
			ProvisioningElement createProvisioningElement = provisioningContext.createProvisioningElement(breakpoint);
			if (createProvisioningElement != null) {
				provisioningContext.addProvisioningElement(createProvisioningElement);
			}
		}
	}
}