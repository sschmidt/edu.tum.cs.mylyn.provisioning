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
package edu.tum.cs.mylyn.internal.provisioning.core;

import java.util.Map;

import org.eclipse.mylyn.context.core.IInteractionContext;

import edu.tum.cs.mylyn.provisioning.core.IProvisioningElementMapper;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningElement;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class CompositeElementMapper {

	private final Map<String, IProvisioningElementMapper> elementMapper;

	public CompositeElementMapper(Map<String, IProvisioningElementMapper> elementMapper) {
		this.elementMapper = elementMapper;
	}

	public Object getObject(ProvisioningElement element, IInteractionContext context) {
		IProvisioningElementMapper mapper = elementMapper.get(element.getContentType());
		if (mapper != null) {
			return mapper.getObject(context, element);
		}
		return null;
	}

	public ProvisioningElement createProvisioningElement(String contentType, Object object) {
		IProvisioningElementMapper mapper = elementMapper.get(contentType);
		if (mapper != null) {
			return mapper.createProvisioningElement(object);
		}

		return null;
	}

	public ProvisioningElement createProvisioningElement(Object object) {
		for (IProvisioningElementMapper mapper : elementMapper.values()) {
			ProvisioningElement element = mapper.createProvisioningElement(object);
			if (element != null) {
				return element;
			}
		}

		return null;
	}
}
