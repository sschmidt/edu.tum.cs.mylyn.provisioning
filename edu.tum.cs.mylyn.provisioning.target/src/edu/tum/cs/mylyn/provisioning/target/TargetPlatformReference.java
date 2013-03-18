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

import edu.tum.cs.mylyn.provisioning.core.HandleBuilder;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class TargetPlatformReference {

	private final String resource;
	private final String name;

	public TargetPlatformReference(String name, String resource) {
		this.name = name;
		this.resource = resource;
	}

	public String getName() {
		return name;
	}

	public String getResource() {
		return resource;
	}

	public static TargetPlatformReference fromIdentifier(String handleIdentifier) {
		String[] parseArguments = HandleBuilder.parseArguments(handleIdentifier);
		if (parseArguments.length == 2) {
			return new TargetPlatformReference(parseArguments[0], parseArguments[1]);
		}
		return null;
	}

	public String toIdentifier() {
		return HandleBuilder.buildHandle(TargetContributor.CONTENT_TYPE, name, resource);
	}

	@Override
	public String toString() {
		return name + "[" + resource + "]"; //$NON-NLS-1$
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TargetPlatformReference other = (TargetPlatformReference) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		return true;
	}
}
