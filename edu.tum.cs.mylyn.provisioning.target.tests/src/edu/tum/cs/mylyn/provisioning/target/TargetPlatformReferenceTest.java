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
package edu.tum.cs.mylyn.provisioning.target;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class TargetPlatformReferenceTest {

	@Test
	public void testFromIdentifier() {
		String handleIdentifier = "target-platform[name|resource]";
		TargetPlatformReference reference = TargetPlatformReference.fromIdentifier(handleIdentifier);
		assertEquals("name", reference.getName());
		assertEquals("resource", reference.getResource());
		assertEquals("name[resource]", reference.toString());
		assertEquals(handleIdentifier, reference.toIdentifier());
	}

	@Test
	public void testEquals() {
		String handleIdentifier = "target-platform[name|resource]";
		TargetPlatformReference reference1 = TargetPlatformReference.fromIdentifier(handleIdentifier);
		TargetPlatformReference reference2 = TargetPlatformReference.fromIdentifier(handleIdentifier);
		assertEquals(reference1, reference2);
	}
}
