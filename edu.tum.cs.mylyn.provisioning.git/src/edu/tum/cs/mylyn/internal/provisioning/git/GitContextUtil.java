/*******************************************************************************
 * Copyright (c) 2012 Sebastian Schmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Schmidt - initial API and implementation
 *******************************************************************************/
package edu.tum.cs.mylyn.internal.provisioning.git;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.egit.core.GitProvider;
import org.eclipse.egit.core.project.GitProjectData;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.team.core.RepositoryProvider;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class GitContextUtil {

	@Deprecated
	public static List<IProject> getContextProjects(List<IInteractionElement> elements) {
		Set<IProject> projects = new HashSet<IProject>();

		for (IInteractionElement element : elements) {
			String originId = element.getHandleIdentifier();
			IProject project = parseProject(originId);
			if (project != null) {
				projects.add(project);
			}
		}

		return new ArrayList<IProject>(projects);
	}

	private static IProject parseProject(String originId) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		try {
			Path path = new Path(originId);
			IFile fileForLocation = root.getFile(path);
			return fileForLocation.getProject();
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static Repository getGitRepository(IProject project) {
		RepositoryProvider provider = RepositoryProvider.getProvider(project);
		if (provider == null || !(provider instanceof GitProvider)) {
			return null;
		}

		GitProvider gitProvider = (GitProvider) provider;
		GitProjectData data = gitProvider.getData();
		return data.getRepositoryMapping(project).getRepository();
	}

	public static List<String> getRelevantProjects(Repository repositoryWrapper, IInteractionContext context) {
		List<IProject> contextProjects = getContextProjects(context.getAllElements());
		List<String> results = new ArrayList<String>();
		for (IProject project : contextProjects) {
			if (repositoryWrapper.equals(getGitRepository(project))) {
				results.add(project.toString());
			}
		}
		return results;
	}

}