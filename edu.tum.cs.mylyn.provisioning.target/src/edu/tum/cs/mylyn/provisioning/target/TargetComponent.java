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
package edu.tum.cs.mylyn.provisioning.target;

import org.eclipse.pde.core.target.ITargetPlatformService;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class TargetComponent {

	private static ITargetPlatformService targetPlatformService;

	public static void setTargetService(ITargetPlatformService targetService) {
		TargetComponent.targetPlatformService = targetService;
	}

	public static void unsetTargetService(ITargetPlatformService agent) {
		TargetComponent.targetPlatformService = null;
	}

	public static ITargetPlatformService getTargetService() {
		return targetPlatformService;
	}
}
