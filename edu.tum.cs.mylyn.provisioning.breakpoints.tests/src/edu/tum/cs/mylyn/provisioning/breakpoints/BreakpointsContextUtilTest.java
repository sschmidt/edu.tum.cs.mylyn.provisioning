/*******************************************************************************
 * Copyright (c) 2012 Sebastian Schmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Schmidt - initial API and implementation
 *******************************************************************************/
package edu.tum.cs.mylyn.provisioning.breakpoints;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IBreakpoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.tum.cs.mylyn.internal.provisioning.breakpoints.BreakpointsContextUtil;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class BreakpointsContextUtilTest {

	@Mock
	private IProgressMonitor progressMonitor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testParseBreakpoint() throws FileNotFoundException, CoreException {
		InputStream stream = new FileInputStream(new File("fixtures/saved-state.xml"));
		List<IBreakpoint> parseBreakpoints = BreakpointsContextUtil.parseBreakpoints(stream, progressMonitor, false);
		assertEquals(1, parseBreakpoints.size());
		String breakpointId = (String) parseBreakpoints.get(0).getMarker()
				.getAttribute(BreakpointsContributor.BREAKPOINT_ID);
		assertEquals("8388a063-d725-4ed1-af18-3e547f2c0187", breakpointId);
	}
}
