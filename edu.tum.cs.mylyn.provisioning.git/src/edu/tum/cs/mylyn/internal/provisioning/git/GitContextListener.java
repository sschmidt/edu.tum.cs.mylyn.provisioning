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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextChangeEvent.ContextChangeKind;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;

import edu.tum.cs.mylyn.internal.provisioning.core.ProvisioningCorePlugin;
import edu.tum.cs.mylyn.provisioning.core.CommonUtils;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;
import edu.tum.cs.mylyn.provisioning.git.GitContributor;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class GitContextListener extends AbstractContextListener {

	@Override
	public void contextChanged(ContextChangeEvent event) {
		if (event.getEventKind().equals(ContextChangeKind.ELEMENTS_DELETED)) {
			deleteRepositories(event.getContext());
		} else if (event.getEventKind().equals(ContextChangeKind.INTEREST_CHANGED)) {
			addRepositories(event.getContext(), event.getElements());
		}
	}

	private void deleteRepositories(IInteractionContext context) {
		ProvisioningContext provisioningContext = ProvisioningCorePlugin.getProvisioningContextManager()
				.getProvisioningContext(context);
		Set<ProvisioningElement> currentRepositories = provisioningContext
				.getProvisioningElements(GitContributor.CONTENT_TYPE);
		Set<ProvisioningElement> requiredRepositories = addRepositories(context, context.getAllElements());
		for (ProvisioningElement element : currentRepositories) {
			if (!requiredRepositories.contains(element)) {
				provisioningContext.removeProvisioningElement(element);
			}
		}
	}

	private Set<ProvisioningElement> addRepositories(IInteractionContext context, List<IInteractionElement> elements) {
		Set<ProvisioningElement> results = new HashSet<ProvisioningElement>();
		ProvisioningContext provisioningContext = ProvisioningCorePlugin.getProvisioningContextManager()
				.getProvisioningContext(context);

		for (IInteractionElement element : elements) {
			IProject project = CommonUtils.getProject(element.getHandleIdentifier());
			if (project != null) {
				Repository gitRepository = GitContextUtil.getGitRepository(project);
				if (gitRepository != null) {
					ProvisioningElement provisioningElement = provisioningContext.createProvisioningElement(
							GitContributor.CONTENT_TYPE, gitRepository);
					provisioningElement.addRelatedInteraction(element);
					provisioningContext.addProvisioningElement(provisioningElement);
					results.add(provisioningElement);
				}
			}
		}

		return results;
	}
}
