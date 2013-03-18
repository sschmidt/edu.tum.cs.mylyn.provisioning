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
package edu.tum.cs.mylyn.provisioning.ui;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;

import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;

/**
 * @author Sebastian Schmidt
 */
public interface IContextProvisioningTask {

	/**
	 * @return task name (e.g. Clone Git Repository)
	 */
	public String getName();

	/**
	 * @return some more details what this task will do
	 */
	public String getDescription();

	/**
	 * @return an unique id
	 */
	public String getId();

	/**
	 * @return an image to be displayed next to the task in the provisioning
	 *         selection dialog
	 */
	public ImageDescriptor getImage();

	/**
	 * Returns whether or not this task is required to be executed for a given
	 * provisioning context. For example, if this task provisions git
	 * repositories but the user already have all required repositories from the
	 * context, this would return false.
	 * 
	 * @param context
	 *            an provisioning context
	 * @return true|false, execution required
	 */
	public boolean executionRequired(ProvisioningContext context);

	/**
	 * Same as {@link #executionRequired(ProvisioningContext)}, but limited to a
	 * specific set of elements. For example, the user would select a git
	 * repository in the provisioning editor. This method would return true if
	 * the selected repository needs to be provisioned.
	 * 
	 * @param context
	 *            an provisioning context
	 * @param elementHandles
	 *            a list of requested elements
	 * @return true|false, execution required
	 */
	public boolean executionRequired(ProvisioningContext context, List<String> elementHandles);

	/**
	 * Returns a wizard which executes this provisioning task.
	 * 
	 * @param context
	 *            a provisioning context
	 * @param monitor
	 *            process monitor
	 * @return a provisioning wizard
	 */
	public Wizard getWizard(ProvisioningContext context, IProgressMonitor monitor);

	/**
	 * Returns a wizard executing this provisioning task, limited to the given
	 * list of interesting elements.
	 * 
	 * @param context
	 *            a provisioning context
	 * @param elementHandles
	 *            list of interesting elements
	 * @param monitor
	 *            process monitor
	 * @return a provisioning wizard
	 */
	public Wizard getWizard(ProvisioningContext context, List<String> elementHandles, IProgressMonitor monitor);

}
