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
package edu.tum.cs.mylyn.internal.provisioning.target;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.pde.internal.core.PDECore;

import edu.tum.cs.mylyn.internal.provisioning.core.ProvisioningCorePlugin;
import edu.tum.cs.mylyn.provisioning.core.CommonUtils;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;
import edu.tum.cs.mylyn.provisioning.target.TargetContributor;
import edu.tum.cs.mylyn.provisioning.target.TargetPlatformReference;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
@SuppressWarnings("restriction")
public class TargetPlatformListener {

	private static final String PLUGIN_PROJECT_NATURE = "org.eclipse.pde.PluginNature"; //$NON-NLS-1$
	private static final String WORKSPACE_TARGET_HANDLE = "workspace_target_handle"; //$NON-NLS-1$
	private boolean storedTargetPlatform;
	private IInteractionContext context;

	private final IPreferenceChangeListener listener = new IPreferenceChangeListener() {
		@Override
		public void preferenceChange(PreferenceChangeEvent event) {
			if (WORKSPACE_TARGET_HANDLE.equals(event.getKey())) {
				storeTargetPlatform();
			}
		}
	};

	public void start(IInteractionContext context) {
		this.context = context;
		PDECore.getDefault().getPreferencesManager().addPreferenceChangeListener(listener);
	}

	public void stop() {
		PDECore.getDefault().getPreferencesManager().removePreferenceChangeListener(listener);
	}

	public void interestChanged(List<IInteractionElement> elements) {
		if (!storedTargetPlatform) {
			for (IProject project : CommonUtils.getProjects(elements)) {
				try {
					if (project.hasNature(PLUGIN_PROJECT_NATURE)) {
						storeTargetPlatform();
						storedTargetPlatform = true;
					}
				} catch (CoreException e) {
					// honestly, we don't care
				}
			}
		}
	}

	private void storeTargetPlatform() {
		try {
			TargetPlatformReference reference = TargetContributor.getCurrentTargetPlatformReference();
			if (reference == null) {
				return;
			}

			ProvisioningContext provisioningContext = ProvisioningCorePlugin.getProvisioningContextManager()
					.getProvisioningContext(context);
			ProvisioningElement element = provisioningContext.createProvisioningElement(TargetContributor.CONTENT_TYPE,
					reference);
			provisioningContext.addProvisioningElement(element);
		} catch (Exception e) {
			StatusHandler.log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
		}
	}

}
