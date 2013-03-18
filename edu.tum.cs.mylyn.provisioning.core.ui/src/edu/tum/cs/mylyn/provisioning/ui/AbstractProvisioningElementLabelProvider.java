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

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public abstract class AbstractProvisioningElementLabelProvider extends LabelProvider implements IStyledLabelProvider {

	/**
	 * @return the root element for provisioning elements of this content type
	 */
	public abstract IProvisioningElementUiRoot getProvisioningElementRoot();

	/**
	 * @return content type of provisioning element which are accepted by this
	 *         label provider.
	 */
	public abstract String getContentType();
}
