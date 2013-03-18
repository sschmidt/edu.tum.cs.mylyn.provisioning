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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;

import edu.tum.cs.mylyn.internal.provisioning.core.ProvisioningCorePlugin;
import edu.tum.cs.mylyn.internal.provisioning.core.xml.ProvisioningContextReader;

/**
 * A provisioning context manages provisioning elements related to an
 * interaction context. An interaction context stores user interactions with the
 * IDE (i.e. interactions with resources). In contrast, a provisioning context
 * aims to store metadata related or not related to these interactions (e.g. git
 * repository of edited files).It can be managed by the provisiong editor
 * attached to mylyn's task editor.
 * 
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class ProvisioningContext {

	private final Map<String, Set<ProvisioningElement>> provisioningElements = new HashMap<String, Set<ProvisioningElement>>();
	private final IInteractionContext context;
	private final List<IProvisioningContextListener> listeners = new ArrayList<IProvisioningContextListener>();

	public ProvisioningContext(IInteractionContext context) {
		this.context = context;
	}

	public void addProvisioningElement(ProvisioningElement element) {
		boolean result = getProvisioningElements(element.getContentType()).add(element);
		if (!result && element.isRelatedToInteractionContext()) {
			ProvisioningElement currentElement = findCurrentElement(element);
			currentElement.addRelatedInteractions(element.getRelatedInteractions());
		}

		if (result) {
			notifyListener();
		}
	}

	private ProvisioningElement findCurrentElement(ProvisioningElement element) {
		for (ProvisioningElement currentElement : provisioningElements.get(element.getContentType())) {
			if (currentElement.equals(element)) {
				return currentElement;
			}
		}

		return null;
	}

	public void removeAll() {
		provisioningElements.clear();
		notifyListener();
	}

	public void removeProvisioningElement(ProvisioningElement element) {
		removeProvisioningElement(element, false);
	}

	public void removeProvisioningElement(ProvisioningElement element, boolean force) {
		if (!force && element.isRelatedToInteractionContext()
				&& getProvisioningElements(element.getContentType()).contains(element)) {
			ProvisioningElement currentElement = findCurrentElement(element);
			currentElement.removeRelatedInteractions(element.getRelatedInteractions());
			if (currentElement.getRelatedInteractions().size() == 0) {
				getProvisioningElements(element.getContentType()).remove(element);
			}
			notifyListener();
		} else {
			boolean result = getProvisioningElements(element.getContentType()).remove(element);
			if (result) {
				notifyListener();
			}
		}

	}

	public Map<String, Set<ProvisioningElement>> getProvisioningElements() {
		return provisioningElements;
	}

	public Set<ProvisioningElement> getProvisioningElements(String contentType) {
		if (!provisioningElements.containsKey(contentType)) {
			Set<ProvisioningElement> elements = new HashSet<ProvisioningElement>();
			provisioningElements.put(contentType, elements);
		}

		return provisioningElements.get(contentType);
	}

	public void addChangeListener(IProvisioningContextListener listener) {
		listeners.add(listener);
	}

	public void removeChangeListener(IProvisioningContextListener listener) {
		listeners.remove(listener);
	}

	private void notifyListener() {
		for (IProvisioningContextListener listener : listeners) {
			listener.contextChanged();
		}
	}

	public void load() {
		InputStream additionalContextData = ContextCore.getContextManager().getAdditionalContextData(context,
				ProvisioningContextManager.CONTRIBUTOR_ID);
		if (additionalContextData != null) {
			restorePreviousState(additionalContextData);
		}
		fillCaches();
	}

	private void fillCaches() {
		for (Set<ProvisioningElement> elements : getProvisioningElements().values()) {
			for (ProvisioningElement element : elements) {
				getObject(element);
			}
		}
	}

	private void restorePreviousState(InputStream additionalContextData) {
		final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		final ProvisioningContextReader reader = new ProvisioningContextReader(this);
		try {
			final SAXParser parser = parserFactory.newSAXParser();
			parser.parse(additionalContextData, reader);
		} catch (final Exception e) {
			StatusHandler.log(new Status(Status.WARNING, ProvisioningCorePlugin.ID_PLUGIN, e.getMessage(), e));
		}
	}

	public Object getObject(ProvisioningElement element) {
		return ProvisioningCorePlugin.getElementMapper().getObject(element, context);
	}

	public ProvisioningElement createProvisioningElement(String contentType, Object object) {
		return ProvisioningCorePlugin.getElementMapper().createProvisioningElement(contentType, object);
	}

	public ProvisioningElement createProvisioningElement(Object object) {
		return ProvisioningCorePlugin.getElementMapper().createProvisioningElement(object);
	}

	public IInteractionContext getInteractionContext() {
		return context;
	}

}
