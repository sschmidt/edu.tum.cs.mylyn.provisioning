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

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import edu.tum.cs.mylyn.provisioning.target.TargetContributor;
import edu.tum.cs.mylyn.provisioning.target.TargetPlatformReference;
import edu.tum.cs.mylyn.provisioning.ui.AbstractProvisioningElementLabelProvider;
import edu.tum.cs.mylyn.provisioning.ui.IProvisioningElementUiRoot;
import edu.tum.cs.mylyn.provisioning.ui.ResourceManager;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class TargetElementLabelProvider extends AbstractProvisioningElementLabelProvider {

	private static final String TARGET_PLATFORMS_ROOT = "Target Platforms";

	private class TargetProvisioningElementRoot implements IProvisioningElementUiRoot {
		@Override
		public String getContentType() {
			return TargetContributor.CONTENT_TYPE;
		}
	}

	@Override
	public IProvisioningElementUiRoot getProvisioningElementRoot() {
		return new TargetProvisioningElementRoot();
	}

	@Override
	public String getContentType() {
		return TargetContributor.CONTENT_TYPE;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof TargetPlatformReference) {
			return element.toString();
		} else if (element instanceof TargetProvisioningElementRoot) {
			return TARGET_PLATFORMS_ROOT;
		}

		return null;
	}

	@Override
	public StyledString getStyledText(Object element) {
		if (element instanceof TargetProvisioningElementRoot) {
			return new StyledString(TARGET_PLATFORMS_ROOT); //$NON-NLS-1$
		} else if (element instanceof TargetPlatformReference) {
			TargetPlatformReference reference = (TargetPlatformReference) element;
			StyledString string = new StyledString();
			string.append(reference.getName());
			string.append(' ');
			string.append('[', StyledString.DECORATIONS_STYLER);
			string.append(reference.getResource(), StyledString.DECORATIONS_STYLER);
			string.append(']', StyledString.DECORATIONS_STYLER);
			return string;
		}

		return null;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof TargetProvisioningElementRoot) {
			return ResourceManager.getPluginImageDescriptor("edu.tum.cs.mylyn.provisioning.target.ui",
					"icons/eclipse.gif").createImage();
		} else if (element instanceof TargetPlatformReference) {
			return ResourceManager.getPluginImageDescriptor("edu.tum.cs.mylyn.provisioning.target.ui",
					"icons/target_profile_xml_obj.gif").createImage();
		}

		return null;
	}
}