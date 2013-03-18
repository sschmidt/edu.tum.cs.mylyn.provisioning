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

package edu.tum.cs.mylyn.internal.provisioning.ui.editors;

import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.IFormPage;

import edu.tum.cs.mylyn.provisioning.ui.ResourceManager;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class ProvisioningEditorPageFactory extends AbstractTaskEditorPageFactory {
	public static String ID_CONTEXT_PAGE_FACTORY = "edu.tum.cs.mylyn.provisioning.core"; //$NON-NLS-1$

	@Override
	public boolean canCreatePageFor(TaskEditorInput input) {
		return true;
	}

	@Override
	public Image getPageImage() {
		return ResourceManager.getPluginImageDescriptor("edu.tum.cs.mylyn.provisioning.core",
				"icons/icon_prov_small.png").createImage();
	}

	@Override
	public String getPageText() {
		return "Provisioning"; //$NON-NLS-1$
	}

	@Override
	public IFormPage createPage(TaskEditor parentEditor) {
		return new ProvisioningEditorFormPage(parentEditor, ID_CONTEXT_PAGE_FACTORY, "Provisioning");
	}

}
