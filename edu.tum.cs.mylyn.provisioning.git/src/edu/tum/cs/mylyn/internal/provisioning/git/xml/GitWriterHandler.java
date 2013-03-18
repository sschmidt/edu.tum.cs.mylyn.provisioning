/*******************************************************************************
 * Copyright (c) 2012 Technische Universitaet Muenchen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Schmidt - initial API and implementation
 *******************************************************************************/
package edu.tum.cs.mylyn.internal.provisioning.git.xml;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
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

import edu.tum.cs.mylyn.internal.provisioning.git.Activator;
import edu.tum.cs.mylyn.provisioning.git.RepositoryWrapper;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class GitWriterHandler implements XMLReader {

	public static final String ELEMENT_CONFIG = "config"; //$NON-NLS-1$
	public static final String ELEMENT_DEFAULT_BRANCH = "default-branch"; //$NON-NLS-1$
	public static final String ELEMENT_PROJECT = "project"; //$NON-NLS-1$
	public static final String ELEMENT_NAME = "name"; //$NON-NLS-1$
	public static final String ELEMENT_PROJECTS = "projects"; //$NON-NLS-1$
	public static final String ELEMENT_REPOSITORY = "repository"; //$NON-NLS-1$
	public static final String EMPTY = ""; //$NON-NLS-1$
	public static final String ELEMENT_REPOSITORIES = "repositories"; //$NON-NLS-1$

	private ContentHandler contentHandler;
	private ErrorHandler errorHandler;
	private List<RepositoryWrapper> repositories;

	public GitWriterHandler(List<RepositoryWrapper> repositories) {
		this.repositories = repositories;
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
		contentHandler.startElement(EMPTY, ELEMENT_REPOSITORIES, ELEMENT_REPOSITORIES, null);
		for (RepositoryWrapper repository : repositories) {
			contentHandler.startElement(EMPTY, ELEMENT_REPOSITORY, ELEMENT_REPOSITORY, new AttributesImpl());
			addGitConfig(repository);
			contentHandler.endElement(EMPTY, ELEMENT_REPOSITORY, ELEMENT_REPOSITORY);
		}
		contentHandler.endElement(EMPTY, ELEMENT_REPOSITORIES, ELEMENT_REPOSITORIES);
		contentHandler.endDocument();
	}

	private void addGitConfig(RepositoryWrapper repository) throws SAXException {
		AttributesImpl configAttributes = new AttributesImpl();
		try {
			configAttributes.addAttribute(EMPTY, ELEMENT_DEFAULT_BRANCH, ELEMENT_DEFAULT_BRANCH, EMPTY,
					repository.getBranch());
		} catch (IOException e) {
			StatusHandler.log(new Status(Status.WARNING, Activator.ID_PLUGIN, e.getMessage(), e));
		}
		contentHandler.startElement(EMPTY, ELEMENT_CONFIG, ELEMENT_CONFIG, configAttributes);
		String config = repository.getConfig().toText();
		String configWithoutCredentials = removeCredentials(config);
		char[] repositoryConfig = configWithoutCredentials.toCharArray();
		contentHandler.characters(repositoryConfig, 0, repositoryConfig.length - 1);
		contentHandler.endElement(EMPTY, ELEMENT_CONFIG, ELEMENT_CONFIG);
	}

	private String removeCredentials(String config) {
		String configWithoutCredentials = config.replaceAll("://(.[^:/]*:)?.[^@/]*@", "://");
		return configWithoutCredentials;
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
