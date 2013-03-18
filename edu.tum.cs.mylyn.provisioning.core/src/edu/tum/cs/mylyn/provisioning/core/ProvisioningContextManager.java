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
package edu.tum.cs.mylyn.provisioning.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextContributor;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextChangeEvent.ContextChangeKind;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.context.core.CompositeInteractionContext;

import edu.tum.cs.mylyn.internal.provisioning.core.ProvisioningCorePlugin;
import edu.tum.cs.mylyn.internal.provisioning.core.xml.ProvisioningContextWriter;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class ProvisioningContextManager extends AbstractContextContributor {
	public static final String CONTRIBUTOR_ID = "edu.tum.cs.mylyn.provisioning.manager"; //$NON-NLS-1$
	public final Map<IInteractionContext, ProvisioningContext> provisioningContexts = new HashMap<IInteractionContext, ProvisioningContext>();

	public ProvisioningContext getProvisioningContext(IInteractionContext context) {
		context = resolveCompositeContext(context);
		if (!provisioningContexts.containsKey(context)) {
			ProvisioningContext provisioningContext = new ProvisioningContext(context);
			provisioningContext.load();
			provisioningContexts.put(context, provisioningContext);
		}
		return provisioningContexts.get(context);
	}

	// TODO: why does this happen?
	@SuppressWarnings("restriction")
	private IInteractionContext resolveCompositeContext(IInteractionContext context) {
		if (context instanceof CompositeInteractionContext) {
			CompositeInteractionContext compositeInteractionContext = (CompositeInteractionContext) context;
			if (compositeInteractionContext.getContextMap().size() == 1) {
				return compositeInteractionContext.getContextMap().values().iterator().next();
			}
		}
		return context;
	}

	@Override
	public void contextChanged(ContextChangeEvent event) {
		if (event.getEventKind().equals(ContextChangeKind.ACTIVATED)) {
			getProvisioningContext(event.getContext());
		} else if (event.getEventKind().equals(ContextChangeKind.DEACTIVATED)) {
			provisioningContexts.remove(resolveCompositeContext(event.getContext()));
		}
	}

	@Override
	public InputStream getDataAsStream(IInteractionContext context) {
		Map<String, Set<ProvisioningElement>> provisioningElements = getProvisioningContext(context)
				.getProvisioningElements();
		if (provisioningElements.isEmpty()) {
			return null;
		}
		return saveCurrentState(provisioningElements.values());
	}

	private InputStream saveCurrentState(Collection<Set<ProvisioningElement>> elements) {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new SAXSource(new ProvisioningContextWriter(elements), null),
					new StreamResult(output));
			return new ByteArrayInputStream(output.toByteArray());
		} catch (final Exception e) {
			StatusHandler.log(new Status(Status.WARNING, ProvisioningCorePlugin.ID_PLUGIN, e.getMessage(), e));
		}
		return null;
	}

	@Override
	public String getIdentifier() {
		return CONTRIBUTOR_ID;
	}
}
