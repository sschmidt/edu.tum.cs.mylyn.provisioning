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

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.transport.URIish;

import edu.tum.cs.mylyn.provisioning.git.GitProvisioningUtil;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class UniqueInMemoryRepository extends InMemoryRepository {

	public UniqueInMemoryRepository(DfsRepositoryDescription repoDesc) {
		super(repoDesc);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof UniqueInMemoryRepository)) {
			return false;
		}

		URIish cloneUri = GitProvisioningUtil.getCloneUri(this);
		URIish otherCloneUri = GitProvisioningUtil.getCloneUri((Repository) obj);
		if (cloneUri == null || otherCloneUri == null) {
			return false;
		}
		return cloneUri.equals(otherCloneUri);
	}

	@Override
	public int hashCode() {
		return 37 * GitProvisioningUtil.getCloneUri(this).hashCode();
	}
}
