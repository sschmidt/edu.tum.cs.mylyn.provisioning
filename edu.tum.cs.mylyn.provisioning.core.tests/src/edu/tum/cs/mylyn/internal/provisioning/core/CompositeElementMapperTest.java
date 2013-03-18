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

package edu.tum.cs.mylyn.internal.provisioning.core;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.context.core.IInteractionContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.tum.cs.mylyn.internal.provisioning.core.CompositeElementMapper;
import edu.tum.cs.mylyn.provisioning.core.IProvisioningElementMapper;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class CompositeElementMapperTest {
	private static final String TEST_CONTENT_TYPE = "_test"; //$NON-NLS-1$

	@Mock
	private IProvisioningElementMapper mapperMock;

	@Mock
	private IInteractionContext context;

	private ProvisioningElement provisioningElement = new ProvisioningElement(TEST_CONTENT_TYPE, "iDontCare");
	private ProvisioningElement unknownProvisioningElement = new ProvisioningElement("sthElse", "iDontCare");

	private CompositeElementMapper objectUnderTest;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Map<String, IProvisioningElementMapper> mapper = new HashMap<String, IProvisioningElementMapper>();
		mapper.put(TEST_CONTENT_TYPE, mapperMock);
		objectUnderTest = new CompositeElementMapper(mapper);
	}

	@Test
	public void testGetObject() {
		objectUnderTest.getObject(provisioningElement, context);
		verify(mapperMock).getObject(context, provisioningElement);
		objectUnderTest.getObject(unknownProvisioningElement, context);
		verify(mapperMock, never()).getObject(context, unknownProvisioningElement);
	}

	@Test
	public void testCreateProvisioningElement1() {
		Object obj1 = new Object();
		objectUnderTest.createProvisioningElement(TEST_CONTENT_TYPE, obj1);
		verify(mapperMock).createProvisioningElement(obj1);
		Object obj2 = new Object();
		objectUnderTest.createProvisioningElement("sthElse", obj2);
		verify(mapperMock, never()).createProvisioningElement(obj2);
	}

	@Test
	public void testCreateProvisioningElement2() {
		objectUnderTest.createProvisioningElement(null);
		verify(mapperMock).createProvisioningElement(null);
	}
}
