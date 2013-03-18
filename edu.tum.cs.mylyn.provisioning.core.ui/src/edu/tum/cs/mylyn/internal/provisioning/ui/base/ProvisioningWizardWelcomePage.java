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
package edu.tum.cs.mylyn.internal.provisioning.ui.base;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import edu.tum.cs.mylyn.provisioning.ui.IContextProvisioningTask;
import edu.tum.cs.mylyn.provisioning.ui.ResourceManager;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class ProvisioningWizardWelcomePage extends WizardPage {
	private final List<IContextProvisioningTask> provisioningTasks;
	private final List<Button> taskButtons = new ArrayList<Button>();

	protected ProvisioningWizardWelcomePage(String pageName, List<IContextProvisioningTask> provisioningTasks) {
		super(pageName);
		this.provisioningTasks = provisioningTasks;
		setTitle("Mylyn Workspace Provisioning");
		setDescription("Please choose which tasks you want to execute.");
		setImageDescriptor(ResourceManager.getPluginImageDescriptor("edu.tum.cs.mylyn.provisioning.core.ui",
				"icons/icon_prov.png"));
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);

		ScrolledComposite scrolledComposite = new ScrolledComposite(container, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBounds(10, 10, 585, 350);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		Font font = new Font(Display.getCurrent(), createFontDescriptor(SWT.BOLD, 1.25f).getFontData());
		Composite composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		int i = 0;
		for (IContextProvisioningTask provisioningTask : provisioningTasks) {
			Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
			label.setBounds(5, 75 + (75 * i), 550, 2);

			ImageDescriptor imageDescriptor = provisioningTask.getImage();
			if (imageDescriptor != null) {
				Image image = imageDescriptor.createImage();
				Label imageLabel = new Label(composite, image.type);
				imageLabel.setImage(image);
				imageLabel.setBounds(5, 3 + (75 * i), 75, 66);
			}

			Label lblTask = new Label(composite, SWT.NONE);
			lblTask.setBounds(105, 42 + (75 * i), 430, 24);
			lblTask.setText(provisioningTask.getDescription());
			lblTask.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

			Button selectButton = new Button(composite, SWT.CHECK);
			selectButton.setBounds(85, 12 + (75 * i), 200, 24);
			selectButton.setText(provisioningTask.getName());
			selectButton.setData(provisioningTask);
			selectButton.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			selectButton.setSelection(true);
			selectButton.setFont(font);
			taskButtons.add(selectButton);
			i++;
		}

		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private FontDescriptor createFontDescriptor(int style, float heightMultiplier) {
		Font baseFont = JFaceResources.getDialogFont();
		FontData[] fontData = baseFont.getFontData();
		FontData[] newFontData = new FontData[fontData.length];
		for (int i = 0; i < newFontData.length; i++) {
			newFontData[i] = new FontData(fontData[i].getName(), (int) (fontData[i].getHeight() * heightMultiplier),
					fontData[i].getStyle() | style);
		}
		return FontDescriptor.createFrom(newFontData);
	}

	public List<Button> getTaskButtons() {
		return taskButtons;
	}
}
