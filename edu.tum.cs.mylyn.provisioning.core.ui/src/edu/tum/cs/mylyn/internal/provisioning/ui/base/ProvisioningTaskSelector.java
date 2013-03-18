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
package edu.tum.cs.mylyn.internal.provisioning.ui.base;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import edu.tum.cs.mylyn.internal.provisioning.core.ProvisioningCorePlugin;
import edu.tum.cs.mylyn.internal.provisioning.ui.ProvisioningUIPlugin;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;
import edu.tum.cs.mylyn.provisioning.ui.IContextProvisioningTask;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class ProvisioningTaskSelector {
	private List<IContextProvisioningTask> selectedTasks = new ArrayList<IContextProvisioningTask>();

	private List<String> previousSelectedTasks;

	public void run(final ProvisioningContext context) {
		List<IContextProvisioningTask> requiredTasks = getRequiredTasks(ProvisioningUIPlugin.getDefault()
				.getProvisioningTasks(), context, null);
		if (requiredTasks.size() == 0) {
			showFinishedMessage();
			return;
		}

		if (previousSelectedTasks != null) {
			selectedTasks = restoreTasks(requiredTasks);
		} else {
			selectedTasks = userTaskSelection(requiredTasks);
		}

		executeSelectedTasks(context, null);
	}

	private void executeSelectedTasks(ProvisioningContext context, List<String> elementHandles) {
		final Iterator<IContextProvisioningTask> iterator = selectedTasks.iterator();
		while (iterator.hasNext()) {
			final IContextProvisioningTask task = iterator.next();
			executeTask(task, context, elementHandles);
			iterator.remove();
			if (PlatformUI.getWorkbench().isClosing()) {
				break;
			}
		}
	}

	public void run(ProvisioningContext context, List<String> elementHandles) {
		selectedTasks = getRequiredTasks(ProvisioningUIPlugin.getDefault().getProvisioningTasks(), context,
				elementHandles);
		if (selectedTasks.size() == 0) {
			showFinishedMessage();
			return;
		}
		executeSelectedTasks(context, elementHandles);
	}

	private void showFinishedMessage() {
		MessageBox mb = new MessageBox(Display.getCurrent().getActiveShell(), SWT.OK);
		mb.setText("Mylyn Workspace Provisioning");
		mb.setMessage("No relevant Provisioning Tasks found.");
		mb.open();
	}

	private List<IContextProvisioningTask> restoreTasks(List<IContextProvisioningTask> tasks) {
		List<IContextProvisioningTask> selectedTasks = new CopyOnWriteArrayList<IContextProvisioningTask>();
		for (IContextProvisioningTask task : tasks) {
			if (previousSelectedTasks.contains(task.getId())) {
				selectedTasks.add(task);
			}
		}
		previousSelectedTasks = null;
		return selectedTasks;
	}

	private List<IContextProvisioningTask> userTaskSelection(List<IContextProvisioningTask> requiredTasks) {
		ProvisioningWizard wizard = new ProvisioningWizard(requiredTasks);
		WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
		dialog.setBlockOnOpen(true);
		dialog.open();
		return wizard.getSelectedTasks();
	}

	private List<IContextProvisioningTask> getRequiredTasks(List<IContextProvisioningTask> contextProvisioningTasks,
			final ProvisioningContext context, final List<String> elementHandles) {
		List<IContextProvisioningTask> provisioningTasks = new ArrayList<IContextProvisioningTask>();
		for (IContextProvisioningTask task : contextProvisioningTasks) {
			if (elementHandles == null) {
				if (task.executionRequired(context)) {
					provisioningTasks.add(task);
				}
			} else {
				if (task.executionRequired(context, elementHandles)) {
					provisioningTasks.add(task);
				}
			}
		}

		return provisioningTasks;
	}

	private void executeTask(final IContextProvisioningTask task, final ProvisioningContext context,
			final List<String> elementHandles) {
		IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		try {
			progressService.run(false, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					Wizard wizard;
					if (elementHandles == null) {
						wizard = task.getWizard(context, monitor);
					} else {
						wizard = task.getWizard(context, elementHandles, monitor);
					}
					WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
					dialog.setBlockOnOpen(true);
					dialog.open();
				}
			});
		} catch (Exception e) {
			StatusHandler.log(new Status(Status.WARNING, ProvisioningCorePlugin.ID_PLUGIN, e.getMessage(), e));
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
		}
	}

	public boolean isActive() {
		return selectedTasks != null && selectedTasks.size() > 0;
	}

	public List<IContextProvisioningTask> getSelectedTasks() {
		return selectedTasks;
	}

	public void setPreviousSelectedTasks(List<String> previousSelectedTasks) {
		this.previousSelectedTasks = previousSelectedTasks;
	}
}
