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
package edu.tum.cs.mylyn.internal.provisioning.core.xml;

import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextElement;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class ProvisioningContextReader extends DefaultHandler {

	private ProvisioningContext provisioningContext;
	private ProvisioningElement lastElement;

	public ProvisioningContextReader(ProvisioningContext provisioningContext) {
		this.provisioningContext = provisioningContext;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (ProvisioningContextWriter.PROV_ELEMENT.equals(qName)) {
			String contentType = null;
			String identifier = null;
			boolean isRelatedToContext = false;
			for (int i = 0; i < attributes.getLength(); i++) {
				if (ProvisioningContextWriter.CONTENT_TYPE.equals(attributes.getQName(i))) {
					contentType = attributes.getValue(i);
				} else if (ProvisioningContextWriter.IDENTIFIER.equals(attributes.getQName(i))) {
					identifier = attributes.getValue(i);
				} else if (ProvisioningContextWriter.RELATED_TO_CONTEXT.equals(attributes.getQName(i))) {
					isRelatedToContext = Boolean.valueOf(attributes.getValue(i));
				}
			}
			if (contentType != null && identifier != null) {
				lastElement = new ProvisioningElement(contentType, identifier, isRelatedToContext);
				provisioningContext.addProvisioningElement(lastElement);
			}
		} else if (ProvisioningContextWriter.INTERACTION_ELEMENT.equals(qName)) {
			String contentType = null;
			String identifier = null;
			for (int i = 0; i < attributes.getLength(); i++) {
				if (ProvisioningContextWriter.CONTENT_TYPE.equals(attributes.getQName(i))) {
					contentType = attributes.getValue(i);
				} else if (ProvisioningContextWriter.IDENTIFIER.equals(attributes.getQName(i))) {
					identifier = attributes.getValue(i);
				}
			}
			if (lastElement != null && contentType != null && identifier != null) {
				IInteractionElement element = new InteractionContextElement(contentType, identifier,
						(InteractionContext) provisioningContext.getInteractionContext());
				lastElement.addRelatedInteraction(element);
			}
		}
	}
}
