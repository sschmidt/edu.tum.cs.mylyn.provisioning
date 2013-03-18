/*******************************************************************************
 * Copyright (c) 2013 Technische Universitaet Muenchen
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextChangeEvent.ContextChangeKind;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class TargetContributorTest {

	private TargetContributor objectUnderTest = new TargetContributor();

	@Mock
	private IInteractionContext context;

	@Mock
	private ContextChangeEvent event;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetDataAsStream() {
		assertNull(objectUnderTest.getDataAsStream(context));
	}

	@Test
	public void testGetId() {
		assertEquals(TargetContributor.CONTRIBUTOR_ID, objectUnderTest.getIdentifier());
	}

	@Test
	public void testContextChanged() {
		when(event.getEventKind()).thenReturn(ContextChangeKind.PRE_ACTIVATED);
		objectUnderTest.contextChanged(event);
		assertNotNull(objectUnderTest.listener);
		when(event.getEventKind()).thenReturn(ContextChangeKind.DEACTIVATED);
		objectUnderTest.contextChanged(event);
		assertNull(objectUnderTest.listener);
	}
}
