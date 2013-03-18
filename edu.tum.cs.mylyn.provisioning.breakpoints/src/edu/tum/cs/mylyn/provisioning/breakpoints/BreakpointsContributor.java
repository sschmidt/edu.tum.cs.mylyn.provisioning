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
package edu.tum.cs.mylyn.provisioning.breakpoints;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.mylyn.context.core.AbstractContextContributor;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextChangeEvent.ContextChangeKind;
import org.eclipse.mylyn.context.core.IInteractionContext;

import edu.tum.cs.mylyn.internal.provisioning.breakpoints.BreakpointsContextUtil;
import edu.tum.cs.mylyn.internal.provisioning.breakpoints.BreakpointsListener;
import edu.tum.cs.mylyn.internal.provisioning.core.ProvisioningCorePlugin;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class BreakpointsContributor extends AbstractContextContributor {

	public static final String CONTRIBUTOR_ID = "edu.tum.cs.mylyn.provisioning.breakpoints"; //$NON-NLS-1$ 
	public static final String CONTENT_TYPE = "breakpoint"; //$NON-NLS-1$
	public static final String BREAKPOINT_ID = "UNIQUE_ID_BREAKPOINTS"; //$NON-NLS-1$
	private BreakpointsListener breakpointsListener = null;

	@Override
	public InputStream getDataAsStream(IInteractionContext context) {
		List<IBreakpoint> contextBreakpoints = getContextBreakpoints(context);
		if (contextBreakpoints.size() == 0) {
			return null;
		}
		return BreakpointsContextUtil.exportBreakpoints(contextBreakpoints, new NullProgressMonitor());
	}

	@Override
	public String getIdentifier() {
		return CONTRIBUTOR_ID;
	}

	@Override
	public void contextChanged(ContextChangeEvent event) {
		if (event.getEventKind().equals(ContextChangeKind.ACTIVATED)) {
			breakpointsListener = new BreakpointsListener(event.getContext());
			DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(breakpointsListener);
		} else if (event.getEventKind().equals(ContextChangeKind.DEACTIVATED)) {
			if (breakpointsListener != null) {
				DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(breakpointsListener);
				breakpointsListener = null;
			}
		}
	}

	private List<IBreakpoint> getContextBreakpoints(IInteractionContext context) {
		List<IBreakpoint> result = new ArrayList<IBreakpoint>();
		ProvisioningContext provisioningContext = ProvisioningCorePlugin.getProvisioningContextManager()
				.getProvisioningContext(context);
		for (ProvisioningElement element : provisioningContext.getProvisioningElements(CONTENT_TYPE)) {
			Object object = provisioningContext.getObject(element);
			if (object != null && object instanceof IBreakpoint) {
				result.add((IBreakpoint) object);
			}
		}
		return result;
	}
}
