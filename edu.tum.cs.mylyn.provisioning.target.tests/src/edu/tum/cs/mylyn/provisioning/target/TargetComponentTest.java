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
import static org.junit.Assert.assertNull;

import org.eclipse.pde.core.target.ITargetPlatformService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class TargetComponentTest {

	@Mock
	private ITargetPlatformService targetService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testTargetComponent() {
		TargetComponent.unsetTargetService(targetService); // reset
		assertNull(TargetComponent.getTargetService());
		TargetComponent.setTargetService(targetService);
		assertEquals(targetService, TargetComponent.getTargetService());
		TargetComponent.unsetTargetService(targetService);
		assertNull(TargetComponent.getTargetService());
	}
}
