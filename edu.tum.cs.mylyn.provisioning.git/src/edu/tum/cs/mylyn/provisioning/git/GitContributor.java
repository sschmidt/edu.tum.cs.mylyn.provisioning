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
package edu.tum.cs.mylyn.provisioning.git;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextContributor;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextChangeEvent.ContextChangeKind;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;

import edu.tum.cs.mylyn.internal.provisioning.core.ProvisioningCorePlugin;
import edu.tum.cs.mylyn.internal.provisioning.git.Activator;
import edu.tum.cs.mylyn.internal.provisioning.git.GitContextListener;
import edu.tum.cs.mylyn.internal.provisioning.git.xml.GitWriterHandler;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class GitContributor extends AbstractContextContributor {
	public static final String CONTRIBUTOR_ID = "edu.tum.cs.mylyn.provisioning.git"; //$NON-NLS-1$
	public static final String CONTENT_TYPE = "git-repository"; //$NON-NLS-1$

	private AbstractContextListener gitListener;

	@Override
	public InputStream getDataAsStream(IInteractionContext context) {
		List<RepositoryWrapper> contextRepositories = getContextRepositories(context);
		if (contextRepositories.isEmpty()) {
			return null;
		}

		return saveContextRepositories(contextRepositories);
	}

	private InputStream saveContextRepositories(List<RepositoryWrapper> contextRepositories) {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new SAXSource(new GitWriterHandler(contextRepositories), null), new StreamResult(
					output));
			return new ByteArrayInputStream(output.toByteArray());
		} catch (final Exception e) {
			StatusHandler.log(new Status(Status.WARNING, Activator.ID_PLUGIN, e.getMessage(), e));
		}
		return null;
	}

	private List<RepositoryWrapper> getContextRepositories(IInteractionContext context) {
		List<RepositoryWrapper> contextRepositories = new ArrayList<RepositoryWrapper>();
		ProvisioningContext provisioningContext = ProvisioningCorePlugin.getProvisioningContextManager()
				.getProvisioningContext(context);
		for (ProvisioningElement element : provisioningContext.getProvisioningElements(CONTENT_TYPE)) {
			Object object = provisioningContext.getObject(element);
			if (object != null && object instanceof RepositoryWrapper) {
				contextRepositories.add((RepositoryWrapper) object);
			}
		}
		return contextRepositories;
	}

	@Override
	public String getIdentifier() {
		return GitContributor.CONTRIBUTOR_ID;
	}

	@Override
	public void contextChanged(ContextChangeEvent event) {
		if (event.getEventKind().equals(ContextChangeKind.PRE_ACTIVATED)) {
			gitListener = new GitContextListener();
			ContextCore.getContextManager().addListener(gitListener);
		} else if (event.getEventKind().equals(ContextChangeKind.DEACTIVATED)) {
			ContextCore.getContextManager().removeListener(gitListener);
		}
	}
}
