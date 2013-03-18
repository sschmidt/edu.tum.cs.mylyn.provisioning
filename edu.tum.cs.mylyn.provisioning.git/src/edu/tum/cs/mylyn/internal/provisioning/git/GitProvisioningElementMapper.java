/*******************************************************************************
 * Copyright (c) 2012 Technische Universitaet Muenchen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Schmidt - initial API and implementation
 *******************************************************************************/
package edu.tum.cs.mylyn.internal.provisioning.git;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.Status;
import org.eclipse.egit.core.RepositoryCache;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;

import edu.tum.cs.mylyn.internal.provisioning.git.xml.GitParserHandler;
import edu.tum.cs.mylyn.provisioning.core.AbstractProvisioningElementMapper;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;
import edu.tum.cs.mylyn.provisioning.git.GitContributor;
import edu.tum.cs.mylyn.provisioning.git.GitProvisioningUtil;
import edu.tum.cs.mylyn.provisioning.git.RepositoryWrapper;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class GitProvisioningElementMapper extends AbstractProvisioningElementMapper {
	private final Set<RepositoryWrapper> repositoryCache = new HashSet<RepositoryWrapper>();

	@Override
	public Object getObject(IInteractionContext context, ProvisioningElement handle) {
		RepositoryWrapper repository = getRepositoryFromEgit(context, handle);
		if (repository != null) {
			return repository;
		}
		repository = getRepositoryFromCache(handle);
		if (repository != null) {
			return repository;
		}
		return loadRepository(context, handle);
	}

	private RepositoryWrapper getRepositoryFromCache(ProvisioningElement handle) {
		for (RepositoryWrapper repository : repositoryCache) {
			if (repository.getCloneURI().toString().equals(handle.getIdentifier())) {
				return repository;
			}
		}
		return null;
	}

	private RepositoryWrapper getRepositoryFromEgit(IInteractionContext context, ProvisioningElement handle) {
		RepositoryCache repositoryCache = org.eclipse.egit.core.Activator.getDefault().getRepositoryCache();
		for (Repository repository : repositoryCache.getAllRepositories()) {
			URIish cloneUri = GitProvisioningUtil.getCloneUri(repository);
			if (handle.getIdentifier().equals(cloneUri.toString())) {
				return new RepositoryWrapper(repository);
			}
		}
		return null;
	}

	@Override
	public ProvisioningElement createProvisioningElement(Object object) {
		if (object instanceof Repository) {
			Repository repository = (Repository) object;
			URIish cloneUri = GitProvisioningUtil.getCloneUri(repository);
			if (cloneUri != null) {
				return new ProvisioningElement(GitContributor.CONTENT_TYPE, cloneUri.toString(), true);
			}
		}
		return null;
	}

	@Override
	public String getContentType() {
		return GitContributor.CONTENT_TYPE;
	}

	private RepositoryWrapper loadRepository(IInteractionContext context, ProvisioningElement handle) {
		final InputStream additionalContextData = ContextCore.getContextManager().getAdditionalContextData(context,
				GitContributor.CONTRIBUTOR_ID);
		if (additionalContextData == null) {
			return null;
		}
		final GitParserHandler gitParser = parseRepositories(additionalContextData);
		for (final RepositoryWrapper repositoryInfo : gitParser.getRepositories()) {
			if (repositoryInfo.getCloneURI().toString().equals(handle.getIdentifier())) {
				repositoryCache.add(repositoryInfo);
				return repositoryInfo;
			}
		}
		return null;
	}

	private GitParserHandler parseRepositories(final InputStream additionalContextData) {
		final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		final GitParserHandler gitParser = new GitParserHandler();
		try {
			final SAXParser parser = parserFactory.newSAXParser();
			parser.parse(additionalContextData, gitParser);
		} catch (final Exception e) {
			StatusHandler.log(new Status(Status.WARNING, Activator.ID_PLUGIN, e.getMessage(), e));
		}
		return gitParser;
	}
}
