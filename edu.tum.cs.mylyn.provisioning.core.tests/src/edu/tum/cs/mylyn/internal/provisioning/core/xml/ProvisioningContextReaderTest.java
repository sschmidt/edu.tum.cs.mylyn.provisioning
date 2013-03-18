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
package edu.tum.cs.mylyn.internal.provisioning.core.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.mylyn.context.core.IInteractionContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class ProvisioningContextReaderTest {

	private static final String FIXTURES_CONTENT_TYPE = "breakpoint"; //$NON-NLS-1$
	private static final String VALID_FIXTURE_ID = "8388a063-d725-4ed1-af18-3e547f2c0187";

	@Mock
	private IInteractionContext context;

	private ProvisioningContext provisioningContext;
	private SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	private ProvisioningContextReader reader;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		provisioningContext = new ProvisioningContext(context);
		reader = new ProvisioningContextReader(provisioningContext);
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

		Map<String, Set<ProvisioningElement>> provisioningElements = provisioningContext.getProvisioningElements();
		assertEquals(1, provisioningElements.size());
		Set<ProvisioningElement> object = provisioningElements.get(FIXTURES_CONTENT_TYPE);
		assertNotNull(object);
		assertEquals(2, object.size());
		ProvisioningElement element = new ProvisioningElement(FIXTURES_CONTENT_TYPE, VALID_FIXTURE_ID);
		assertTrue(object.contains(element));
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
