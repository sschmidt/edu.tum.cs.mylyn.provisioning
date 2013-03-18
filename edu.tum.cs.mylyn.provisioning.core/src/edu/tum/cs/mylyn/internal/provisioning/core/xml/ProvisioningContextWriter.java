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

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.eclipse.mylyn.context.core.IInteractionElement;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class ProvisioningContextWriter implements XMLReader {
	public static final String EMPTY = ""; //$NON-NLS-1$
	public static final String PROV_ELEMENTS = "elements"; //$NON-NLS-1$
	public static final String IDENTIFIER = "identifier"; //$NON-NLS-1$
	public static final String CONTENT_TYPE = "content-type"; //$NON-NLS-1$
	public static final String PROV_ELEMENT = "element"; //$NON-NLS-1$
	public static final String RELATED_TO_CONTEXT = "relatedToContext"; //$NON-NLS-1$
	public static final String INTERACTION_ELEMENT = "interactionElement"; //$NON-NLS-1$

	private ContentHandler contentHandler;
	private ErrorHandler errorHandler;
	private Collection<Set<ProvisioningElement>> elements;

	public ProvisioningContextWriter(Collection<Set<ProvisioningElement>> elements) {
		this.elements = elements;
	}

	@Override
	public ContentHandler getContentHandler() {
		return contentHandler;
	}

	@Override
	public DTDHandler getDTDHandler() {
		return null;
	}

	@Override
	public EntityResolver getEntityResolver() {
		return null;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	@Override
	public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		return false;
	}

	@Override
	public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		return null;
	}

	@Override
	public void parse(InputSource input) throws IOException, SAXException {
		contentHandler.startDocument();
		contentHandler.startElement(EMPTY, PROV_ELEMENTS, PROV_ELEMENTS, null);
		for (Collection<ProvisioningElement> current : elements) {
			for (ProvisioningElement element : current) {
				addProvisioningElement(element);
			}
		}
		contentHandler.endElement(EMPTY, PROV_ELEMENTS, PROV_ELEMENTS);
		contentHandler.endDocument();
	}

	private void addProvisioningElement(ProvisioningElement element) throws SAXException {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute(EMPTY, IDENTIFIER, IDENTIFIER, EMPTY, element.getIdentifier());
		attributes.addAttribute(EMPTY, CONTENT_TYPE, CONTENT_TYPE, EMPTY, element.getContentType());
		attributes.addAttribute(EMPTY, RELATED_TO_CONTEXT, RELATED_TO_CONTEXT, EMPTY,
				String.valueOf(element.isRelatedToInteractionContext()));
		contentHandler.startElement(EMPTY, PROV_ELEMENT, PROV_ELEMENT, attributes);
		for (IInteractionElement interaction : element.getRelatedInteractions()) {
			AttributesImpl interactionAttributes = new AttributesImpl();
			interactionAttributes.addAttribute(EMPTY, IDENTIFIER, IDENTIFIER, EMPTY, interaction.getHandleIdentifier());
			interactionAttributes.addAttribute(EMPTY, CONTENT_TYPE, CONTENT_TYPE, EMPTY, interaction.getContentType());
			contentHandler.startElement(EMPTY, INTERACTION_ELEMENT, INTERACTION_ELEMENT, interactionAttributes);
			contentHandler.endElement(EMPTY, INTERACTION_ELEMENT, INTERACTION_ELEMENT);
		}
		contentHandler.endElement(EMPTY, PROV_ELEMENT, PROV_ELEMENT);
	}

	@Override
	public void parse(String systemId) throws IOException, SAXException {
		parse(new InputSource());
	}

	@Override
	public void setContentHandler(ContentHandler handler) {
		this.contentHandler = handler;
	}

	@Override
	public void setDTDHandler(DTDHandler handler) {
	}

	@Override
	public void setEntityResolver(EntityResolver resolver) {
	}

	@Override
	public void setErrorHandler(ErrorHandler handler) {
		this.errorHandler = handler;
	}

	@Override
	public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
	}

	@Override
	public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
	}
}
