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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Button;

import edu.tum.cs.mylyn.provisioning.ui.IContextProvisioningTask;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class ProvisioningWizard extends Wizard {

	private List<IContextProvisioningTask> selectedTasks = new CopyOnWriteArrayList<IContextProvisioningTask>();
	private ProvisioningWizardWelcomePage provisioningWelcomePage;

	public ProvisioningWizard(List<IContextProvisioningTask> requiredProvisioningTasks) {
		provisioningWelcomePage = new ProvisioningWizardWelcomePage("Mylyn Provisioning", requiredProvisioningTasks);
		addPage(provisioningWelcomePage);
	}

	@Override
	public boolean performFinish() {
		List<Button> taskButtons = provisioningWelcomePage.getTaskButtons();
		for (Button button : taskButtons) {
			if (button.getSelection()) {
				selectedTasks.add((IContextProvisioningTask) button.getData());
			}
		}
		return true;
	}

	public List<IContextProvisioningTask> getSelectedTasks() {
		return selectedTasks;
	}
}
