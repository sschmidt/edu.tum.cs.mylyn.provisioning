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
package edu.tum.cs.mylyn.internal.provisioning.ui;

import java.util.HashMap;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import edu.tum.cs.mylyn.provisioning.ui.AbstractProvisioningElementLabelProvider;
import edu.tum.cs.mylyn.provisioning.ui.IProvisioningElementUiRoot;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class CompositeUiElementMapper {

	private final HashMap<String, AbstractProvisioningElementLabelProvider> elementMapper;

	public CompositeUiElementMapper(HashMap<String, AbstractProvisioningElementLabelProvider> elementMapper) {
		this.elementMapper = elementMapper;
	}

	public IProvisioningElementUiRoot getProvisioningElementRoot(final String contentType) {
		final AbstractProvisioningElementLabelProvider mapper = elementMapper.get(contentType);
		if (mapper != null) {
			return mapper.getProvisioningElementRoot();
		}
		return null;
	}

	public StyledString getStyledText(final Object element) {
		for (final AbstractProvisioningElementLabelProvider mapper : elementMapper.values()) {
			final StyledString string = mapper.getStyledText(element);
			if (string != null) {
				return string;
			}
		}
		return null;
	}

	public String getText(final Object element) {
		for (final AbstractProvisioningElementLabelProvider mapper : elementMapper.values()) {
			final String text = mapper.getText(element);
			if (text != null) {
				return text;
			}
		}
		return null;
	}

	public Image getImage(final Object element) {
		for (final AbstractProvisioningElementLabelProvider mapper : elementMapper.values()) {
			final Image image = mapper.getImage(element);
			if (image != null) {
				return image;
			}
		}
		return null;
	}
}
