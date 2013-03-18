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
 * A very basic approach to serialize objects using unique handles based on
 * content type and parameters.
 * 
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class HandleBuilder {

	private static final String ROOT_HANDLE = "default"; //$NON-NLS-1$

	public static String buildHandle(String contentType, String... arguments) {
		String handle = contentType + "["; //$NON-NLS-1$
		for (int i = 0; i < arguments.length; i++) {
			handle += arguments[i];
			if (i != arguments.length - 1) {
				handle += "|"; //$NON-NLS-1$
			}
		}

		if (arguments.length == 0) {
			handle += ROOT_HANDLE;
		}
		handle += "]"; //$NON-NLS-1$
		return handle;
	}

	public static boolean isContentType(String contentType, String handle) {
		return handle.startsWith(contentType + "["); //$NON-NLS-1$
	}

	public static boolean isDefaultHandle(String handle) {
		return handle.contains(new StringBuffer("[default]")); //$NON-NLS-1$
	}

	public static String[] parseArguments(String handle) {
		String strippedArguments = handle.substring(handle.indexOf("[") + 1); //$NON-NLS-1$
		strippedArguments = strippedArguments.substring(0, strippedArguments.length() - 1);
		String[] args = strippedArguments.split("\\|"); //$NON-NLS-1$
		return args;
	}
}
