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

import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextContributor;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextChangeEvent.ContextChangeKind;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetHandle;
import org.eclipse.pde.core.target.ITargetPlatformService;

import edu.tum.cs.mylyn.internal.provisioning.target.Activator;
import edu.tum.cs.mylyn.internal.provisioning.target.TargetPlatformListener;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class TargetContributor extends AbstractContextContributor {

	public static final String CONTRIBUTOR_ID = "edu.tum.cs.mylyn.contributor.target"; //$NON-NLS-1$
	public static final String CONTENT_TYPE = "target-platform"; //$NON-NLS-1$

	private static final String RESOURCE = "resource"; //$NON-NLS-1$
	protected TargetPlatformListener listener;

	@Override
	public InputStream getDataAsStream(IInteractionContext context) {
		return null;
	}

	@Override
	public String getIdentifier() {
		return CONTRIBUTOR_ID;
	}

	@Override
	public void contextChanged(ContextChangeEvent event) {
		ContextChangeKind eventKind = event.getEventKind();
		if (eventKind.equals(ContextChangeKind.DEACTIVATED)) {
			if (listener != null) {
				listener.stop();
				listener = null;
			}
		} else if (eventKind.equals(ContextChangeKind.PRE_ACTIVATED)) {
			listener = new TargetPlatformListener();
			listener.start(event.getContext());
		} else if (eventKind.equals(ContextChangeKind.INTEREST_CHANGED)) {
			listener.interestChanged(event.getElements());
		}
	}

	public static TargetPlatformReference getCurrentTargetPlatformReference() {
		ITargetHandle workspaceTargetHandle;
		try {
			ITargetPlatformService agent = TargetComponent.getTargetService();
			if (agent == null) {
				return null;
			}
			workspaceTargetHandle = agent.getWorkspaceTargetHandle();
			if (workspaceTargetHandle == null) {
				return null;
			}
			String memento = workspaceTargetHandle.getMemento();
			// only store shared target platforms
			if (memento.startsWith(RESOURCE)) {
				ITargetDefinition targetDefinition = workspaceTargetHandle.getTargetDefinition();
				TargetPlatformReference reference = new TargetPlatformReference(targetDefinition.getName(), memento);
				return reference;
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
		}
		return null;
	}
}