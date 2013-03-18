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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.actions.ExportBreakpointsOperation;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;

import edu.tum.cs.mylyn.provisioning.breakpoints.BreakpointsContributor;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;

/**
 * @author Sebastian Schmidt
 */
public class BreakpointsContextUtil {

	public static InputStream exportBreakpoints(Collection<IBreakpoint> breakpoints, IProgressMonitor progressMonitor) {
		if (breakpoints.size() == 0) {
			return null;
		}

		ExportBreakpointsOperation exportBreakpointOperation = new ExportBreakpointsOperation(
				breakpoints.toArray(new IBreakpoint[0]));
		try {
			exportBreakpointOperation.run(progressMonitor);
			return new ByteArrayInputStream(exportBreakpointOperation.getBuffer().toString().getBytes("UTF-8")); //$NON-NLS-1$
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.WARNING, Activator.ID_PLUGIN,
					"Could not export context breakpoints", e));//$NON-NLS-1$
		}
		return null;
	}

	public static IBreakpoint loadBreakpoint(IInteractionContext context, ProvisioningElement handle,
			IProgressMonitor monitor) {
		InputStream stream = ContextCore.getContextManager().getAdditionalContextData(context,
				BreakpointsContributor.CONTRIBUTOR_ID);
		if (stream == null) {
			return null;
		}
		List<IBreakpoint> importBreakpoints = parseBreakpoints(stream, monitor, false);
		IBreakpoint result = null;
		for (IBreakpoint breakpoint : importBreakpoints) {
			if (handle.getIdentifier().equals(
					breakpoint.getMarker().getAttribute(BreakpointsContributor.BREAKPOINT_ID,
							null))) {
				result = breakpoint;
			}
		}
		return result;
	}

	public static List<IBreakpoint> parseBreakpoints(InputStream stream, IProgressMonitor progressMonitor,
			boolean importBreakpoints) {
		Scanner scanner = new Scanner(stream);
		scanner.useDelimiter("\\A"); //$NON-NLS-1$
		String breakpoints = scanner.next();
		scanner.close();

		ImportBreakpointsOperation importBreakpointOperation = new ImportBreakpointsOperation(new StringBuffer(
				breakpoints), true, false, importBreakpoints);
		try {
			importBreakpointOperation.run(progressMonitor);
			return new ArrayList<IBreakpoint>(Arrays.asList(importBreakpointOperation.getImportedBreakpoints()));
		} catch (InvocationTargetException e) {
			StatusHandler.log(new Status(IStatus.WARNING, Activator.ID_PLUGIN,
					"Could not import context breakpoints", e));//$NON-NLS-1$
		}
		return new ArrayList<IBreakpoint>();
	}

	public static void removeBreakpoints(Collection<IBreakpoint> breakpoints) {
		try {
			DebugPlugin.getDefault().getBreakpointManager()
					.removeBreakpoints(breakpoints.toArray(new IBreakpoint[0]), true);
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.WARNING, Activator.ID_PLUGIN,
					"Could not remove obsolete breakpoints from workspace", e)); //$NON-NLS-1$
		}
	}
}