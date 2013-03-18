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
package edu.tum.cs.mylyn.provisioning.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.tum.cs.mylyn.provisioning.core.HandleBuilder;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class HandleBuilderTest {

	@Test
	public void testBuildHandle() {
		String buildHandle = HandleBuilder.buildHandle("ct", "1", "2", "3");
		assertEquals("ct[1|2|3]", buildHandle);
	}

	@Test
	public void testIsDefaultHandle() {
		assertTrue(HandleBuilder.isDefaultHandle("[default]"));
		assertFalse(HandleBuilder.isDefaultHandle("[default123]"));
	}

	@Test
	public void testIsContentType() {
		assertTrue(HandleBuilder.isContentType("ct", "ct[1|2|3]"));
		assertFalse(HandleBuilder.isContentType("ct", "ct1[1|2|3]"));
	}

	@Test
	public void testParseArguments() {
		String[] parseArguments = HandleBuilder.parseArguments("ct[1|2|3]");
		assertEquals(3, parseArguments.length);
		assertEquals("1", parseArguments[0]);
		assertEquals("2", parseArguments[1]);
		assertEquals("3", parseArguments[2]);
	}

}
