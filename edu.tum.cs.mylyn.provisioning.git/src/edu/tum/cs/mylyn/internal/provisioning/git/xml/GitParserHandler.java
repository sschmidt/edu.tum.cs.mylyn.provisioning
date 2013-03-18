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
package edu.tum.cs.mylyn.internal.provisioning.git.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.SystemReader;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.tum.cs.mylyn.internal.provisioning.git.Activator;
import edu.tum.cs.mylyn.internal.provisioning.git.UniqueInMemoryRepository;
import edu.tum.cs.mylyn.provisioning.git.GitProvisioningUtil;
import edu.tum.cs.mylyn.provisioning.git.RepositoryWrapper;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class GitParserHandler extends DefaultHandler {

	private static final String NL = "\n"; //$NON-NLS-1$

	private Config systemConfig = SystemReader.getInstance().openSystemConfig(null, FS.DETECTED);
	private Config userConfig = SystemReader.getInstance().openUserConfig(systemConfig, FS.DETECTED);

	private List<RepositoryWrapper> repositories = new ArrayList<RepositoryWrapper>();
	private StringBuilder repositoryConfig;
	private String branch;

	private boolean readRepository = false;
	private boolean readRepositoryConfig = false;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (GitWriterHandler.ELEMENT_REPOSITORY.equals(qName)) {
			readRepository = true;
			repositoryConfig = new StringBuilder();
		} else if (GitWriterHandler.ELEMENT_CONFIG.equals(qName) && readRepository) {
			readRepositoryConfig = true;
			for (int i = 0; i < attributes.getLength(); i++) {
				if (GitWriterHandler.ELEMENT_DEFAULT_BRANCH.equals(attributes.getQName(i))) {
					branch = attributes.getValue(i);
				}
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (GitWriterHandler.ELEMENT_REPOSITORY.equals(qName)) {
			Config config = new Config(userConfig);
			try {
				repositoryConfig.append(NL);
				config.fromText(repositoryConfig.toString());
				Repository repository = createRepository(config);
				RepositoryWrapper info = new RepositoryWrapper(repository);
				repositories.add(info);
			} catch (ConfigInvalidException e) {
			}
			repositoryConfig = null;
			readRepository = false;
			readRepositoryConfig = false;
		}
	}

	private Repository createRepository(Config config) {
		URIish cloneUri = GitProvisioningUtil.getCloneUri(config, branch);
		String projectName = GitProvisioningUtil.getProjectName(cloneUri);
		DfsRepositoryDescription dfsRepositoryDescription = new DfsRepositoryDescription(projectName);
		InMemoryRepository repository = new UniqueInMemoryRepository(dfsRepositoryDescription);
		try {
			repository.getConfig().fromText(config.toText());
			repository.create();
		} catch (ConfigInvalidException e) {
			StatusHandler.log(new Status(Status.WARNING, Activator.ID_PLUGIN, e.getMessage(), e));
		} catch (IOException e) {
			StatusHandler.log(new Status(Status.WARNING, Activator.ID_PLUGIN, e.getMessage(), e));
		}

		return repository;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (readRepositoryConfig) {
			repositoryConfig.append(ch, start, length);
		}
	}

	public List<RepositoryWrapper> getRepositories() {
		return repositories;
	}

}
