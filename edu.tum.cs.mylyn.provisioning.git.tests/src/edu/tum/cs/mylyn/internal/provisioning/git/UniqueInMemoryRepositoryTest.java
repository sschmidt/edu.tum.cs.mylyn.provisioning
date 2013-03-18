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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.storage.dfs.DfsRepositoryDescription;
import org.junit.Test;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class UniqueInMemoryRepositoryTest {

	@Test
	public void testRepository() throws ConfigInvalidException, IOException {
		DfsRepositoryDescription dfsRepositoryDescription = new DfsRepositoryDescription("test");
		UniqueInMemoryRepository repository1 = new UniqueInMemoryRepository(dfsRepositoryDescription);
		InputStream stream = new FileInputStream(new File("fixtures/repository-config"));
		StringWriter writer = new StringWriter();
		IOUtils.copy(stream, writer);
		repository1.getConfig().fromText(writer.toString());
		repository1.create();

		UniqueInMemoryRepository repository2 = new UniqueInMemoryRepository(dfsRepositoryDescription);
		repository2.getConfig().fromText(writer.toString());
		repository2.create();

		assertEquals(repository1, repository2);
	}

}
