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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;

import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;
import edu.tum.cs.mylyn.provisioning.target.TargetContributor;
import edu.tum.cs.mylyn.provisioning.target.TargetPlatformReference;
import edu.tum.cs.mylyn.provisioning.ui.IContextProvisioningTask;
import edu.tum.cs.mylyn.provisioning.ui.ResourceManager;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class TargetProvisioningTask implements IContextProvisioningTask {

	@Override
	public String getName() {
		return "Target Platform";
	}

	@Override
	public String getDescription() {
		return "Activate Target Platform";
	}

	@Override
	public String getId() {
		return TargetContributor.CONTRIBUTOR_ID;
	}

	@Override
	public ImageDescriptor getImage() {
		return ResourceManager.getPluginImageDescriptor("edu.tum.cs.mylyn.provisioning.target.ui", "icons/eclipse.jpg");
	}

	@Override
	public boolean executionRequired(ProvisioningContext context) {
		List<TargetPlatformReference> targetPlatforms = getTargetPlatform(context);
		if (targetPlatforms.size() == 0) {
			return false;
		}
		if (targetPlatforms.contains(TargetContributor.getCurrentTargetPlatformReference())) {
			return false;
		}
		return true;
	}

	@Override
	public boolean executionRequired(ProvisioningContext context, List<String> elementHandles) {
		TargetPlatformReference currentTargetPlatform = TargetContributor.getCurrentTargetPlatformReference();
		for(ProvisioningElement element : context.getProvisioningElements(TargetContributor.CONTENT_TYPE)) {
			if(elementHandles.contains(element.getIdentifier())) {
				if (!context.getObject(element).equals(currentTargetPlatform)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Wizard getWizard(ProvisioningContext context, IProgressMonitor monitor) {
		return new TargetPlatformWizard(getTargetPlatform(context));
	}

	@Override
	public Wizard getWizard(ProvisioningContext context, List<String> elementHandles, IProgressMonitor monitor) {
		return new TargetPlatformWizard(getTargetPlatform(context), elementHandles);
	}

	public List<TargetPlatformReference> getTargetPlatform(ProvisioningContext context) {
		List<TargetPlatformReference> result = new ArrayList<TargetPlatformReference>();
		for (ProvisioningElement provisioningElement : context.getProvisioningElements(TargetContributor.CONTENT_TYPE)) {
			Object object = context.getObject(provisioningElement);
			if (object instanceof TargetPlatformReference) {
				result.add((TargetPlatformReference) object);
			}
		}
		return result;
	}
}
