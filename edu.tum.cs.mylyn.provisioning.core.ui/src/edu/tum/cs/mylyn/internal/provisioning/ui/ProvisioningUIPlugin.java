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
package edu.tum.cs.mylyn.internal.provisioning.ui;

import java.util.HashMap;
import java.util.List;

import org.eclipse.mylyn.commons.core.ExtensionPointReader;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.tum.cs.mylyn.provisioning.ui.IContextProvisioningTask;
import edu.tum.cs.mylyn.provisioning.ui.AbstractProvisioningElementLabelProvider;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class ProvisioningUIPlugin extends AbstractUIPlugin {

	private static boolean mapperInitialized = false;
	private boolean taskInitialized = false;

	public static final String ID_PLUGIN = "edu.tum.cs.mylyn.provisioning.core.ui"; //$NON-NLS-1$

	private static final String EXTENSION_ELEMENT_TASK = "contextProvisioningTask"; //$NON-NLS-1$
	private static final String EXTENSION_ID_TASK = "provisioningTasks"; //$NON-NLS-1$

	private static final String EXTENSION_ID_MAPPER = "elementLabelProvider";
	private static final String EXTENSION_ELEMENT_MAPPER = "elementLabelProvider";

	private List<IContextProvisioningTask> provisiongTasks;
	private static BundleContext context;

	private static HashMap<String, AbstractProvisioningElementLabelProvider> elementMapper;

	private static CompositeUiElementMapper compositeElementMapper;
	private static ProvisioningUIPlugin plugin;

	static BundleContext getContext() {
		return context;
	}

	public static class ProvisioningUiStartup implements IStartup {
		public void earlyStartup() {
			// ignore
		}
	}

	public void start(BundleContext bundleContext) throws Exception {
		ProvisioningUIPlugin.context = bundleContext;
		plugin = this;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		ProvisioningUIPlugin.context = null;
		provisiongTasks = null;
		taskInitialized = false;
		plugin = null;
	}

	public List<IContextProvisioningTask> getProvisioningTasks() {
		initProvisioningTasks();
		return provisiongTasks;
	}

	private void initProvisioningTasks() {
		if (!taskInitialized) {
			ExtensionPointReader<IContextProvisioningTask> extensionPointReader = new ExtensionPointReader<IContextProvisioningTask>(
					ProvisioningUIPlugin.ID_PLUGIN, ProvisioningUIPlugin.EXTENSION_ID_TASK,
					ProvisioningUIPlugin.EXTENSION_ELEMENT_TASK, IContextProvisioningTask.class);
			extensionPointReader.read();
			provisiongTasks = extensionPointReader.getItems();
			taskInitialized = true;
		}
	}

	private static void initElementMapper() {
		if (!mapperInitialized) {
			ExtensionPointReader<AbstractProvisioningElementLabelProvider> extensionPointReader = new ExtensionPointReader<AbstractProvisioningElementLabelProvider>(
					ProvisioningUIPlugin.ID_PLUGIN, ProvisioningUIPlugin.EXTENSION_ID_MAPPER,
					ProvisioningUIPlugin.EXTENSION_ELEMENT_MAPPER, AbstractProvisioningElementLabelProvider.class);
			extensionPointReader.read();
			List<AbstractProvisioningElementLabelProvider> allMapper = extensionPointReader.getItems();
			elementMapper = new HashMap<String, AbstractProvisioningElementLabelProvider>();
			for (AbstractProvisioningElementLabelProvider mapper : allMapper) {
				elementMapper.put(mapper.getContentType(), mapper);
			}
			mapperInitialized = true;
		}
	}

	public static CompositeUiElementMapper getElementUiMapper() {
		if (compositeElementMapper == null) {
			initElementMapper();
			compositeElementMapper = new CompositeUiElementMapper(elementMapper);
		}
		return compositeElementMapper;
	}

	public static ProvisioningUIPlugin getDefault() {
		return plugin;
	}

}
