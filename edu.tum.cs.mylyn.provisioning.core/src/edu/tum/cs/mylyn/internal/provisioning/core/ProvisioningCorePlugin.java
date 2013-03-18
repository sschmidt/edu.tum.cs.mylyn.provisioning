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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.commons.core.ExtensionPointReader;
import org.eclipse.mylyn.context.core.ContextCore;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import edu.tum.cs.mylyn.provisioning.core.IProvisioningElementMapper;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningContextManager;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class ProvisioningCorePlugin implements BundleActivator {

	public static final String ID_PLUGIN = "edu.tum.cs.mylyn.provisioning.core"; //$NON-NLS-1$

	private static final String EXTENSION_ID_MAPPER = "elementMapper"; //$NON-NLS-1$

	private static final String EXTENSION_ELEMENT_MAPPER = "elementMapper"; //$NON-NLS-1$

	private static BundleContext context;

	private static ProvisioningCorePlugin singleton;

	private static ProvisioningContextManager provisioningContextManager;

	private static CompositeElementMapper compositeElementMapper;

	private static Map<String, IProvisioningElementMapper> elementMapper;

	private static boolean mapperInitialized = false;

	public static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext ctx) throws Exception {
		ProvisioningCorePlugin.context = ctx;
		singleton = this;
		provisioningContextManager = new ProvisioningContextManager();
		ContextCore.getContextContributor().add(provisioningContextManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		ProvisioningCorePlugin.context = null;
		singleton = null;
		ResourcesPlugin.getWorkspace().removeSaveParticipant(ID_PLUGIN);
		ContextCore.getContextContributor().remove(provisioningContextManager);
	}

	public static ProvisioningCorePlugin getDefault() {
		return singleton;
	}

	public static ProvisioningContextManager getProvisioningContextManager() {
		return provisioningContextManager;
	}

	private static void initElementMapper() {
		if (!mapperInitialized) {
			ExtensionPointReader<IProvisioningElementMapper> extensionPointReader = new ExtensionPointReader<IProvisioningElementMapper>(
					ProvisioningCorePlugin.ID_PLUGIN, ProvisioningCorePlugin.EXTENSION_ID_MAPPER,
					ProvisioningCorePlugin.EXTENSION_ELEMENT_MAPPER, IProvisioningElementMapper.class);
			extensionPointReader.read();
			List<IProvisioningElementMapper> allMapper = extensionPointReader.getItems();
			elementMapper = new HashMap<String, IProvisioningElementMapper>();
			for (IProvisioningElementMapper mapper : allMapper) {
				elementMapper.put(mapper.getContentType(), mapper);
			}
			mapperInitialized = true;
		}
	}

	public static CompositeElementMapper getElementMapper() {
		if (compositeElementMapper == null) {
			initElementMapper();
			compositeElementMapper = new CompositeElementMapper(elementMapper);
		}
		return compositeElementMapper;
	}

}
