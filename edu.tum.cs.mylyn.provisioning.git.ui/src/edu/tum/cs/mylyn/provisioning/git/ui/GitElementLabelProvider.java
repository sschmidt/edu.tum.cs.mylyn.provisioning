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

import java.io.IOException;

import org.eclipse.egit.ui.internal.repository.RepositoriesViewLabelProvider;
import org.eclipse.egit.ui.internal.repository.tree.RepositoryNode;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.swt.graphics.Image;

import edu.tum.cs.mylyn.provisioning.git.GitContributor;
import edu.tum.cs.mylyn.provisioning.git.GitProvisioningUtil;
import edu.tum.cs.mylyn.provisioning.ui.AbstractProvisioningElementLabelProvider;
import edu.tum.cs.mylyn.provisioning.ui.IProvisioningElementUiRoot;
import edu.tum.cs.mylyn.provisioning.ui.ResourceManager;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
@SuppressWarnings("restriction")
public class GitElementLabelProvider extends AbstractProvisioningElementLabelProvider {

	private static final String GIT_REPOSITORIES = "Git Repositories";

	private final RepositoriesViewLabelProvider parentProvider = new RepositoriesViewLabelProvider();

	private class GitProvisioningElementRoot implements IProvisioningElementUiRoot {
		@Override
		public String getContentType() {
			return GitContributor.CONTENT_TYPE;
		}
	}

	@Override
	public IProvisioningElementUiRoot getProvisioningElementRoot() {
		return new GitProvisioningElementRoot();
	}

	@Override
	public String getContentType() {
		return GitContributor.CONTENT_TYPE;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Repository) {
			return GitProvisioningUtil.getCloneUri((Repository) element).toString();
		} else if (element instanceof GitProvisioningElementRoot) {
			return GIT_REPOSITORIES;
		}
		return null;
	}

	@Override
	public StyledString getStyledText(Object element) {
		String text = getText(element);
		if (text != null) {
			StyledString string = new StyledString(text);
			if (element instanceof Repository) {
				try {
					string.append(' '); //$NON-NLS-1$
					string.append('[', StyledString.DECORATIONS_STYLER); //$NON-NLS-1$
					string.append(((Repository) element).getBranch(), StyledString.DECORATIONS_STYLER);
					string.append(']', StyledString.DECORATIONS_STYLER); //$NON-NLS-1$
				} catch (IOException e) {
					// ignore
				}
			}
			return string;
		}
		return null;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof Repository) {
			RepositoryNode repositoryNode = new RepositoryNode(null, (Repository) element);
			return parentProvider.getImage(repositoryNode);
		} else if (element instanceof GitProvisioningElementRoot) {
			return ResourceManager.getPluginImageDescriptor("edu.tum.cs.mylyn.provisioning.git.ui",
					"icons/repo_rep.gif").createImage(); //$NON-NLS-1$
		}
		return null;
	}
}