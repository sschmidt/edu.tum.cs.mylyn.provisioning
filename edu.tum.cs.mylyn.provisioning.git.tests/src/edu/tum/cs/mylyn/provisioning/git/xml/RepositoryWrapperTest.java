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
package edu.tum.cs.mylyn.provisioning.git.xml;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import static org.mockito.Mockito.verify;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.dfs.DfsRepositoryDescription;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.tum.cs.mylyn.internal.provisioning.git.UniqueInMemoryRepository;
import edu.tum.cs.mylyn.provisioning.git.RepositoryWrapper;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class RepositoryWrapperTest {

	@Mock
	private Repository mockRepository;

	private Repository repository;

	@Before
	public void setUp() throws IOException, ConfigInvalidException {
		MockitoAnnotations.initMocks(this);
		DfsRepositoryDescription dfsRepositoryDescription = new DfsRepositoryDescription("test");
		repository = new UniqueInMemoryRepository(dfsRepositoryDescription);
		InputStream stream = new FileInputStream(new File("fixtures/repository-config"));
		StringWriter writer = new StringWriter();
		IOUtils.copy(stream, writer);
		repository.getConfig().fromText(writer.toString());
		repository.create();
	}

	@Test
	public void testRepositoryWrapper() {
		RepositoryWrapper wrapper = new RepositoryWrapper(repository);
		assertEquals("git://git.eclipse.org/gitroot/mylyn/org.eclipse.mylyn.all", wrapper.getCloneURI().toString());
		assertEquals("git://git.eclipse.org/gitroot/mylyn/org.eclipse.mylyn.all", wrapper.toString());
		assertEquals(1, wrapper.getRemoteConfigs().size());
	}

	@Test
	public void testRepositoryWrapperDelegate() throws IOException {
		RepositoryWrapper wrapper = new RepositoryWrapper(mockRepository);
		wrapper.create(true);
		verify(mockRepository).create(true);
		wrapper.getConfig();
		verify(mockRepository).getConfig();
		wrapper.getObjectDatabase();
		verify(mockRepository).getObjectDatabase();
		wrapper.getRefDatabase();
		verify(mockRepository).getRefDatabase();
		wrapper.getReflogReader("test");
		verify(mockRepository).getReflogReader("test");
		wrapper.notifyIndexChanged();
		verify(mockRepository).notifyIndexChanged();
		wrapper.scanForRepoChanges();
		verify(mockRepository).scanForRepoChanges();
	}
}
