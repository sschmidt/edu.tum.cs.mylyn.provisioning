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
package edu.tum.cs.mylyn.internal.provisioning.git;

import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class Activator implements BundleActivator {

	public static final String ID_PLUGIN = "edu.tum.cs.internal.context.contributor.git"; //$NON-NLS-1$
	private static BundleContext context;

	public static class GitContributorStartup implements IStartup {

		public void earlyStartup() {
			// ignore
		}
	}

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
}
