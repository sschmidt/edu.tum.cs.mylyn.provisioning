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
package edu.tum.cs.mylyn.internal.provisioning.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.context.core.IInteractionContext;

import edu.tum.cs.mylyn.internal.provisioning.core.ProvisioningCorePlugin;
import edu.tum.cs.mylyn.internal.provisioning.ui.ProvisioningUIPlugin;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningContextManager;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;
import edu.tum.cs.mylyn.provisioning.ui.IProvisioningElementUiRoot;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class ProvisioningEditorContentProvider implements ITreeContentProvider {
	private final ProvisioningContextManager provisioningContextManager = ProvisioningCorePlugin
			.getProvisioningContextManager();
	private ProvisioningContext provisioningContext;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		Object data = viewer.getData(ProvisioningEditorFormPage.CONTEXT_KEY);
		if (data instanceof IInteractionContext) {
			IInteractionContext context = (IInteractionContext) data;
			provisioningContext = provisioningContextManager.getProvisioningContext(context);
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IWorkspaceRoot) {
			return getRootElements();
		} else if (parentElement instanceof IProvisioningElementUiRoot) {
			return getChildElements((IProvisioningElementUiRoot) parentElement);
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IWorkspaceRoot) {
			Collection<Set<ProvisioningElement>> values = provisioningContext.getProvisioningElements().values();
			for (Set<ProvisioningElement> value : values) {
				if (!value.isEmpty()) {
					return true;
				}
			}
		} else if (element instanceof IProvisioningElementUiRoot) {
			return getChildElements((IProvisioningElementUiRoot) element).length > 0;
		}

		return false;
	}

	private Object[] getRootElements() {
		List<IProvisioningElementUiRoot> results = new ArrayList<IProvisioningElementUiRoot>();
		Collection<Set<ProvisioningElement>> values = provisioningContext.getProvisioningElements().values();
		for (Set<ProvisioningElement> elements : values) {
			if (!elements.isEmpty()) {
				ProvisioningElement random = elements.iterator().next();
				IProvisioningElementUiRoot root = ProvisioningUIPlugin.getElementUiMapper().getProvisioningElementRoot(
						random.getContentType());
				if (root != null) {
					results.add(root);
				}
			}
		}
		return results.toArray();
	}

	private Object[] getChildElements(final IProvisioningElementUiRoot parentElement) {
		List<Object> results = new ArrayList<Object>();
		for (ProvisioningElement element : provisioningContext.getProvisioningElements(parentElement.getContentType())) {
			Object object = provisioningContext.getObject(element);
			if (object != null) {
				results.add(object);
			}
		}
		return results.toArray();
	}
}
