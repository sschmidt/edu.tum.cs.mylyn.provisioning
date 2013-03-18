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
package edu.tum.cs.mylyn.provisioning.ui.actions;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import edu.tum.cs.mylyn.internal.provisioning.core.ProvisioningCorePlugin;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class DeleteElementAction implements IViewActionDelegate, IWorkbenchWindowActionDelegate {
	protected IViewPart view;

	protected IWorkbenchWindow window;

	private ISelection selection;

	@Override
	public void run(IAction action) {
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			@SuppressWarnings("rawtypes")
			Iterator iterator = structuredSelection.iterator();
			IInteractionContext activeContext = ContextCore.getContextManager().getActiveContext();
			ProvisioningContext provisioningContext = ProvisioningCorePlugin.getProvisioningContextManager()
					.getProvisioningContext(activeContext);

			while (iterator.hasNext()) {
				Object object = iterator.next();
				ProvisioningElement provisioningElement = provisioningContext.createProvisioningElement(object);
				if (provisioningElement != null) {
					provisioningContext.removeProvisioningElement(provisioningElement, true);
				}
			}
		}
	}

	public void dispose() {
		// ignore
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void init(IViewPart view) {
		this.view = view;
	}
}
