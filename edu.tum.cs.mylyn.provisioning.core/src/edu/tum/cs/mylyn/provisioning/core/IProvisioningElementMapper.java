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
package edu.tum.cs.mylyn.provisioning.core;

import org.eclipse.mylyn.context.core.IInteractionContext;

/**
 * Maps between provisioning elements and platform elements.
 * 
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public interface IProvisioningElementMapper {
	/**
	 * Returns the platform element related to the given provisioning element.
	 * May use the context to obtain more information (e.g. from context
	 * contributors).
	 * 
	 * @param context
	 *            interaction context
	 * @param handle
	 *            provisioning element
	 * @return platform element
	 */
	public Object getObject(IInteractionContext context, ProvisioningElement handle);

	/**
	 * Creates a provisioning element representing the given platform element.
	 * 
	 * @param object
	 *            platform element
	 * @return provisioning element representing the given object
	 */
	public ProvisioningElement createProvisioningElement(Object object);

	/**
	 * @return content type of this mapper.
	 */
	public String getContentType();
}
