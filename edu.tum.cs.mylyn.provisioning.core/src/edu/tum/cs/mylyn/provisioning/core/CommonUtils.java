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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.context.core.IInteractionElement;

/**
 * Methods commonly used by provisioning connectors.
 * 
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class CommonUtils {

	/**
	 * Returns the list of projects which are related to the given list of
	 * interaction elements.
	 * 
	 * @param elements
	 *            list of elements
	 * @return list of projects
	 */
	public static List<IProject> getProjects(List<IInteractionElement> elements) {
		Set<IProject> projects = new HashSet<IProject>();

		for (IInteractionElement element : elements) {
			String originId = element.getHandleIdentifier();
			IProject project = getProject(originId);
			if (project != null) {
				projects.add(project);
			}
		}

		return new ArrayList<IProject>(projects);
	}

	/**
	 * Returns the containing project of a resource
	 * 
	 * @param resourceHandle
	 *            a workspace resource
	 * @return the corresponding project
	 */
	public static IProject getProject(String resourceHandle) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		try {
			Path path = new Path(resourceHandle);
			IFile fileForLocation = root.getFile(path);
			return fileForLocation.getProject();
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
