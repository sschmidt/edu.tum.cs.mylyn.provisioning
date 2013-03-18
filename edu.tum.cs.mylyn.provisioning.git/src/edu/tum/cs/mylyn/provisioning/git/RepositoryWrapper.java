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
package edu.tum.cs.mylyn.provisioning.git;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.jgit.lib.BaseRepositoryBuilder;
import org.eclipse.jgit.lib.ObjectDatabase;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.ReflogReader;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.mylyn.commons.core.StatusHandler;

import edu.tum.cs.mylyn.internal.provisioning.git.Activator;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class RepositoryWrapper extends Repository {

	private Repository wrappedRepository;

	@SuppressWarnings("rawtypes")
	public RepositoryWrapper(Repository repository) {
		super(new BaseRepositoryBuilder<BaseRepositoryBuilder, Repository>());
		this.wrappedRepository = repository;
	}

	@Deprecated
	public static RepositoryWrapper initWithRepository(Repository repository) {
		return new RepositoryWrapper(repository);
	}

	public URIish getCloneURI() {
		try {
			return GitProvisioningUtil.getCloneUri(wrappedRepository.getConfig(), wrappedRepository.getBranch());
		} catch (IOException e) {
			StatusHandler.log(new Status(Status.WARNING, Activator.ID_PLUGIN, e.getMessage(), e));
			return null;
		}
	}

	public List<RemoteConfig> getRemoteConfigs() {
		try {
			return RemoteConfig.getAllRemoteConfigs(wrappedRepository.getConfig());
		} catch (URISyntaxException e) {
			return new ArrayList<RemoteConfig>();
		}
	}

	@Override
	public void create(boolean arg0) throws IOException {
		wrappedRepository.create(arg0);
	}

	@Override
	public StoredConfig getConfig() {
		return wrappedRepository.getConfig();
	}

	@Override
	public ObjectDatabase getObjectDatabase() {
		return wrappedRepository.getObjectDatabase();
	}

	@Override
	public RefDatabase getRefDatabase() {
		return wrappedRepository.getRefDatabase();
	}

	@Override
	public ReflogReader getReflogReader(String arg0) throws IOException {
		return wrappedRepository.getReflogReader(arg0);
	}

	@Override
	public void notifyIndexChanged() {
		wrappedRepository.notifyIndexChanged();
	}

	@Override
	public void scanForRepoChanges() throws IOException {
		wrappedRepository.scanForRepoChanges();
	}

	@Override
	public String toString() {
		URIish cloneUri = getCloneURI();
		if (cloneUri != null) {
			return cloneUri.toString();
		}

		return "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((wrappedRepository == null) ? 0 : wrappedRepository.getConfig().toText().hashCode());
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
		RepositoryWrapper other = (RepositoryWrapper) obj;
		if (wrappedRepository == null) {
			if (other.wrappedRepository != null)
				return false;
		} else if (!wrappedRepository.getConfig().toText().equals(other.wrappedRepository.getConfig().toText()))
			return false;
		return true;
	}

}
