/*******************************************************************************
 * Copyright (c) 2012 Sebastian Schmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Schmidt - initial API and implementation
 *******************************************************************************/
package edu.tum.cs.mylyn.internal.provisioning.breakpoints.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import edu.tum.cs.mylyn.provisioning.breakpoints.BreakpointsContributor;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class BreakpointsWizardPage extends WizardPage {

	private Table table;
	private List<String> selected;
	private List<IBreakpoint> breakpoints;
	private BreakpointsElementLabelProvider mapper = new BreakpointsElementLabelProvider();

	public BreakpointsWizardPage(List<IBreakpoint> breakpoints, List<String> selected) {
		super("Import Breakpoints");
		this.breakpoints = breakpoints;
		this.selected = selected;
		setTitle("Import Breakpoints");
		setDescription("Select Breakpoints to Import");
	}

	@Override
	public void createControl(final Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(1, false));
		table = new Table(container, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		for (IBreakpoint breakpoint : breakpoints) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setData(breakpoint);
			item.setText(mapper.getText(breakpoint));
			item.setImage(mapper.getImage(breakpoint));
			if (selected.size() == 0
					|| selected.contains(breakpoint.getMarker()
							.getAttribute(BreakpointsContributor.BREAKPOINT_ID, null))) {
				item.setChecked(true);
			}

		}

	}

	@Override
	public boolean canFlipToNextPage() {
		return true;
	}

	public List<IBreakpoint> getBreakpoints() {
		List<IBreakpoint> breakpoints = new ArrayList<IBreakpoint>();
		for (TableItem item : table.getItems()) {
			if (item.getChecked()) {
				breakpoints.add((IBreakpoint) item.getData());
			}
		}

		return breakpoints;
	}
}
