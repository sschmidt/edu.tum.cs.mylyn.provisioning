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

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import edu.tum.cs.mylyn.internal.provisioning.ui.ProvisioningUIPlugin;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class ProvisioningEditorLabelProvider extends LabelProvider implements IStyledLabelProvider {

	@Override
	public String getText(Object element) {
		return ProvisioningUIPlugin.getElementUiMapper().getText(element);
	}

	@Override
	public StyledString getStyledText(Object element) {
		return ProvisioningUIPlugin.getElementUiMapper().getStyledText(element);
	}

	@Override
	public Image getImage(Object element) {
		return ProvisioningUIPlugin.getElementUiMapper().getImage(element);
	}
}
