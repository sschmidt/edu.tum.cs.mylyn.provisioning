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
package edu.tum.cs.mylyn.internal.provisioning.target.ui;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetHandle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import edu.tum.cs.mylyn.provisioning.target.TargetComponent;
import edu.tum.cs.mylyn.provisioning.target.TargetPlatformReference;
import edu.tum.cs.mylyn.provisioning.ui.ResourceManager;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class TargetPlatformWizardPage extends WizardPage {

	private List<TargetPlatformReference> targetPlatforms;
	private TargetPlatformReference selectedPlatform;
	private Table table;

	public TargetPlatformWizardPage(List<TargetPlatformReference> targetPlatforms, String selected) {
		super("Activate Target Platform");
		this.targetPlatforms = targetPlatforms;
		if (selected != null) {
			selectedPlatform = TargetPlatformReference.fromIdentifier(selected);
		}
		setTitle("Activate Target Platform");
		setDescription("Select Target Platform to activate");
	}

	@Override
	public void createControl(final Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(1, false));
		table = new Table(container, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		int invalidReferences = 0;
		for (TargetPlatformReference targetPlatform : targetPlatforms) {
			if (checkTargetPlatform(targetPlatform)) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setData(targetPlatform);
				item.setText(targetPlatform.getName());
				item.setImage(ResourceManager.getPluginImage("edu.tum.cs.mylyn.provisioning.target.ui",
						"icons/target_profile_xml_obj.gif"));
				if (targetPlatform.equals(selectedPlatform)) {
					item.setChecked(true);
				}
			} else {
				invalidReferences++;
			}
		}
		if (invalidReferences > 0) {
			Text text = new Text(container, SWT.NULL);
			text.setText(invalidReferences + " target platforms couldn't be found in your workspace.");
		}
	}

	private boolean checkTargetPlatform(TargetPlatformReference targetPlatform) {
		if (getTargetHandle(targetPlatform) != null) {
			return true;
		} else {
			return false;
		}
	}

	private ITargetDefinition getTargetHandle(final TargetPlatformReference targetPlatform) {
		try {
			ITargetHandle target = TargetComponent.getTargetService().getTarget(targetPlatform.getResource());
			if (target != null) {
				return target.getTargetDefinition();
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
		}
		return null;
	}

	@Override
	public boolean canFlipToNextPage() {
		return true;
	}

	public ITargetDefinition getTarget() {
		for (TableItem item : table.getItems()) {
			if (item.getChecked()) {
				return getTargetHandle((TargetPlatformReference) item.getData());
			}
		}
		return null;
	}
}
