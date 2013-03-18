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

package edu.tum.cs.mylyn.internal.provisioning.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.tum.cs.mylyn.provisioning.ui.actions.DeleteElementAction;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class DeleteElementCommandHandler extends AbstractHandler {

	private final DeleteElementAction action = new DeleteElementAction();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		action.selectionChanged(null, HandlerUtil.getCurrentSelection(event));
		action.run(null);
		return null;
	}
}
