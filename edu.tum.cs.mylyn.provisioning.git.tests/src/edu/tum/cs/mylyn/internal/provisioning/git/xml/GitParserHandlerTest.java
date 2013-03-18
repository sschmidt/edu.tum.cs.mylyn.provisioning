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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.mylyn.context.core.IInteractionContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;
import edu.tum.cs.mylyn.provisioning.git.RepositoryWrapper;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class GitParserHandlerTest {
	@Mock
	private IInteractionContext context;

	private SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	private GitParserHandler reader;
	private ProvisioningContext provisioningContext;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		provisioningContext = new ProvisioningContext(context);
		reader = new GitParserHandler();
	}

	@Test
	public void parseXmlTest() {
		provisioningContext.removeAll(); // cleanup
		try {
			InputStream additionalContextData = new FileInputStream(new File("fixtures/saved-state.xml"));
			final SAXParser parser = parserFactory.newSAXParser();
			parser.parse(additionalContextData, reader);
		} catch (final Exception e) {
			fail();
		}
		List<RepositoryWrapper> repositories = reader.getRepositories();
		assertEquals(1, repositories.size());
		assertEquals("git://git.eclipse.org/gitroot/mylyn/org.eclipse.mylyn.all", repositories.get(0).getCloneURI()
				.toString());
	}

	@Test
	public void parseInvalidXmlTest() {
		provisioningContext.removeAll(); // cleanup
		try {
			InputStream additionalContextData = new FileInputStream(new File("invalid-file"));
			final SAXParser parser = parserFactory.newSAXParser();
			parser.parse(additionalContextData, reader);
		} catch (final Exception e) {
			assertEquals(0, provisioningContext.getProvisioningElements().size());
		}
	}

}
