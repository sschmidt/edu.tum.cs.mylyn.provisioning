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
package edu.tum.cs.mylyn.internal.provisioning.target;

import org.eclipse.mylyn.context.core.IInteractionContext;

import edu.tum.cs.mylyn.provisioning.core.AbstractProvisioningElementMapper;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;
import edu.tum.cs.mylyn.provisioning.target.TargetContributor;
import edu.tum.cs.mylyn.provisioning.target.TargetPlatformReference;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class TargetElementMapper extends AbstractProvisioningElementMapper {

	@Override
	public Object getObject(IInteractionContext context, ProvisioningElement handle) {
		return TargetPlatformReference.fromIdentifier(handle.getIdentifier());
	}

	@Override
	public ProvisioningElement createProvisioningElement(Object object) {
		if (object instanceof TargetPlatformReference) {
			TargetPlatformReference targetPlatformReference = (TargetPlatformReference) object;
			return new ProvisioningElement(TargetContributor.CONTENT_TYPE, targetPlatformReference.toIdentifier());
		}

		return null;
	}

	@Override
	public String getContentType() {
		return TargetContributor.CONTENT_TYPE;
	}

}
