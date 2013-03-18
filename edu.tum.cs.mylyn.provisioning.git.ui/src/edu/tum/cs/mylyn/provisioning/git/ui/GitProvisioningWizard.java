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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.core.internal.util.ProjectUtil;
import org.eclipse.egit.ui.internal.clone.ProjectRecord;
import org.eclipse.egit.ui.internal.clone.ProjectUtils;
import org.eclipse.egit.ui.internal.credentials.EGitCredentialsProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.SubmoduleUpdateCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.ui.IWorkingSet;

import edu.tum.cs.mylyn.provisioning.git.RepositoryWrapper;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
@SuppressWarnings("restriction")
public class GitProvisioningWizard extends Wizard {

	private static final String GIT_DIR = ".git"; //$NON-NLS-1$
	private List<RepositorySelectionPage> pages = new ArrayList<RepositorySelectionPage>();

	public GitProvisioningWizard(List<RepositoryWrapper> requiredProjects) {
		for (RepositoryWrapper requiredProject : requiredProjects) {
			RepositorySelectionPage currentPage = new RepositorySelectionPage(requiredProject);
			addPage(currentPage);
			pages.add(currentPage);
		}
	}

	@Override
	public boolean canFinish() {
		return getNextPage(getContainer().getCurrentPage()) == null;
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Executing GIT Provisioning Task", pages.size() * 100); //$NON-NLS-1$
					for (RepositorySelectionPage currentPage : pages) {
						if (monitor.isCanceled()) {
							break;
						}
						if (!currentPage.getExecuteTask()) {
							monitor.worked(100);
							continue;
						}
						configureRepository(currentPage.getRepository(), currentPage.getTextDestination(), currentPage
								.getImportProjects().getSelection(), monitor);

					}
					monitor.done();
				}
			});
		} catch (InvocationTargetException e) {
			StatusHandler.log(new Status(Status.WARNING, Activator.PLUGIN_ID, "git provisioning interrupted", e)); //$NON-NLS-1$
		} catch (InterruptedException e) {
			StatusHandler.log(new Status(Status.WARNING, Activator.PLUGIN_ID, "git provisioning interrupted", e)); //$NON-NLS-1$
		}

		return true;
	}

	private void configureRepository(RepositoryWrapper repositoryInfo, String destinationDirectory,
			boolean importProjects, IProgressMonitor monitor) {
		try {
			File destinationDir = new File(destinationDirectory);
			File repositoryPath = new File(destinationDir + File.separator + GIT_DIR);
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			Repository repository = builder.setGitDir(repositoryPath).readEnvironment().build();
			repository.getConfig().fromText(repositoryInfo.getConfig().toText());
			repository.create();
			monitor.worked(10);
			monitor.worked(10);
			pull(repository);
			monitor.worked(50);
			if (importProjects) {
				importProjects(repository, monitor);
			}
			monitor.worked(30);
			org.eclipse.egit.core.Activator.getDefault().getRepositoryCache().lookupRepository(repositoryPath);
		} catch (Exception e) {
			StatusHandler.log(new Status(Status.WARNING, Activator.PLUGIN_ID, "error provisioning git projects", e)); //$NON-NLS-1$
		}
	}

	private void importProjects(Repository repository, IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		List<File> files = new ArrayList<File>();
		ProjectUtil.findProjectFiles(files, repository.getWorkTree(), null, monitor);
		Set<ProjectRecord> records = new LinkedHashSet<ProjectRecord>();
		for (File file : files) {
			records.add(new ProjectRecord(file));
		}
		ProjectUtils.createProjects(records, repository, new IWorkingSet[0], monitor);
		refreshWorkspace(monitor);
	}

	private void refreshWorkspace(IProgressMonitor monitor) {
		try {
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (CoreException e) {
			StatusHandler.log(new Status(Status.WARNING, Activator.PLUGIN_ID, "error refreshing workspace", e)); //$NON-NLS-1$
		}
	}

	private void pull(Repository repository) throws IOException {
		Git git = new Git(repository);
		PullCommand pull = git.pull();
		pull.setCredentialsProvider(new EGitCredentialsProvider());
		try {
			pull.call();
		} catch (TransportException e) {
			throw new IOException(e);
		} catch (GitAPIException e) {
			throw new IOException(e);
		}

		SubmoduleUpdateCommand update = git.submoduleUpdate();
		try {
			update.call();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean needsProgressMonitor() {
		return true;
	}
}
