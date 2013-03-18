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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.commons.core.StatusHandler;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class BreakpointsWizard extends Wizard {

	private BreakpointsWizardPage wizardPage;

	public BreakpointsWizard(List<IBreakpoint> targetPlatforms) {
		wizardPage = new BreakpointsWizardPage(targetPlatforms, new ArrayList<String>());
		addPage(wizardPage);
	}

	public BreakpointsWizard(List<IBreakpoint> targetPlatforms, List<String> selected) {
		wizardPage = new BreakpointsWizardPage(targetPlatforms, selected);
		addPage(wizardPage);
	}

	@Override
	public boolean performFinish() {
		final List<IBreakpoint> breakpoints = wizardPage.getBreakpoints();
		if (breakpoints.size() == 0) {
			return true;
		}
		try {
			getContainer().run(false, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					for (IBreakpoint breakpoint : breakpoints) {
						IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
						try {
							breakpointManager.addBreakpoint(breakpoint);
						} catch (CoreException e) {
							StatusHandler.log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
						}
					}
				}
			});
		} catch (InvocationTargetException e) {
			StatusHandler.log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
		} catch (InterruptedException e) {
			StatusHandler.log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
		}
		return true;
	}

	@Override
	public boolean needsProgressMonitor() {
		return true;
	}
}