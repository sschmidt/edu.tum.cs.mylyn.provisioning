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

/**
 * A listener for changes to a provisioning context.
 * 
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public interface IProvisioningContextListener {

	/**
	 * invoked when provisioning context changed
	 */
	public void contextChanged();
}
