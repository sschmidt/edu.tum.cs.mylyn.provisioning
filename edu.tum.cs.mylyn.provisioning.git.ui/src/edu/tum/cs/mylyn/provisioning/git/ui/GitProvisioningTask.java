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
package edu.tum.cs.mylyn.provisioning.git.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.core.RepositoryCache;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.URIish;

import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;
import edu.tum.cs.mylyn.provisioning.git.GitContributor;
import edu.tum.cs.mylyn.provisioning.git.GitProvisioningUtil;
import edu.tum.cs.mylyn.provisioning.git.RepositoryWrapper;
import edu.tum.cs.mylyn.provisioning.ui.IContextProvisioningTask;
import edu.tum.cs.mylyn.provisioning.ui.ResourceManager;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class GitProvisioningTask implements IContextProvisioningTask {

	@Override
	public boolean executionRequired(ProvisioningContext context) {
		return !getRequiredRepositories(context).isEmpty();
	}

	@Override
	public boolean executionRequired(ProvisioningContext context, List<String> elementHandles) {
		return getSelectedRepositories(getRequiredRepositories(context), elementHandles).size() > 0;
	}

	@Override
	public Wizard getWizard(ProvisioningContext context, IProgressMonitor monitor) {
		List<RepositoryWrapper> requiredProjects = getRequiredRepositories(context);
		return new GitProvisioningWizard(requiredProjects);
	}

	@Override
	public Wizard getWizard(ProvisioningContext context, List<String> elementHandles, IProgressMonitor monitor) {
		return new GitProvisioningWizard(getSelectedRepositories(getRequiredRepositories(context), elementHandles));
	}

	@Override
	public String getName() {
		return "GIT"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return "Clone GIT repositories"; //$NON-NLS-1$
	}

	@Override
	public String getId() {
		return "edu.tum.cs.mylyn.provisioning.git"; //$NON-NLS-1$
	}

	@Override
	public ImageDescriptor getImage() {
		return ResourceManager.getPluginImageDescriptor("edu.tum.cs.mylyn.provisioning.git.ui", "icons/import_wiz.png");
	}

	private List<RepositoryWrapper> getSelectedRepositories(List<RepositoryWrapper> repositories, List<String> selection) {
		List<RepositoryWrapper> selectedRepositories = new ArrayList<RepositoryWrapper>();
		for (RepositoryWrapper repository : repositories) {
			for (String cloneUri : selection) {
				if (repository.getCloneURI().toString().equals(cloneUri)) {
					selectedRepositories.add(repository);
				}
			}
		}
		return selectedRepositories;
	}

	private List<RepositoryWrapper> getRequiredRepositories(ProvisioningContext context) {
		List<RepositoryWrapper> results = new ArrayList<RepositoryWrapper>();
		RepositoryCache repositoryCache = org.eclipse.egit.core.Activator.getDefault().getRepositoryCache();
		for (ProvisioningElement element : context.getProvisioningElements(GitContributor.CONTENT_TYPE)) {
			Object object = context.getObject(element);
			if (object instanceof RepositoryWrapper) {
				RepositoryWrapper repository = (RepositoryWrapper) object;
				boolean required = true;
				for (Repository knownRepository : repositoryCache.getAllRepositories()) {
					URIish knownRepositoryUri = GitProvisioningUtil.getCloneUri(knownRepository);
					URIish cloneURI = repository.getCloneURI();
					if (knownRepositoryUri.equals(cloneURI)) {
						required = false;
						break;
					}
				}
				if (required) {
					results.add(repository);
				}
			}
		}
		return results;
	}
}
