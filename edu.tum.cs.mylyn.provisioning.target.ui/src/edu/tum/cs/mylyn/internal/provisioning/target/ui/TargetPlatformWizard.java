/*******************************************************************************
 * Copyright (c) 2013 Technische Universitaet Muenchen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Schmidt - initial API and implementation
 *******************************************************************************/
package edu.tum.cs.mylyn.internal.provisioning.target.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.LoadTargetDefinitionJob;

import edu.tum.cs.mylyn.provisioning.target.TargetPlatformReference;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class TargetPlatformWizard extends Wizard {

	private TargetPlatformWizardPage wizardPage;

	public TargetPlatformWizard(List<TargetPlatformReference> targetPlatforms) {
		wizardPage = new TargetPlatformWizardPage(targetPlatforms, null);
		addPage(wizardPage);
	}

	public TargetPlatformWizard(List<TargetPlatformReference> targetPlatforms, List<String> selected) {
		String selectedElement = null;
		if (!selected.isEmpty()) {
			// only one target platform can be selected at a time.
			// if the user selected more, we just pick the first one.
			selectedElement = selected.iterator().next();
		}
		wizardPage = new TargetPlatformWizardPage(targetPlatforms, selectedElement);
		addPage(wizardPage);
	}

	@Override
	public boolean performFinish() {
		final ITargetDefinition target = wizardPage.getTarget();
		if (target == null) {
			return true;
		}
		try {
			getContainer().run(false, true, new IRunnableWithProgress() {
				@Override
				@SuppressWarnings("restriction")
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					LoadTargetDefinitionJob loadTargetDefinitionJob = new LoadTargetDefinitionJob(target);
					loadTargetDefinitionJob.run(monitor);
					try {
						ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
					} catch (CoreException e) {
						StatusHandler.log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
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