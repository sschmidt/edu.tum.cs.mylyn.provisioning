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
package edu.tum.cs.mylyn.provisioning.git.ui;

import java.io.File;
import java.sql.Timestamp;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.tum.cs.mylyn.provisioning.git.GitProvisioningUtil;
import edu.tum.cs.mylyn.provisioning.git.RepositoryWrapper;
import edu.tum.cs.mylyn.provisioning.ui.ResourceManager;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class RepositorySelectionPage extends WizardPage {

	private RepositoryWrapper repository;
	private Text textSource;
	private Text textDestination;
	private Button btnSelectDestination;
	private Button btnExecuteTask;
	private Button btnImportProjects;

	public RepositorySelectionPage(RepositoryWrapper repositoryInfo) {
		super("Git Provisioning");
		setTitle("GIT Provisioning");
		setDescription("Please review the suggested GIT clone operation.");
		setImageDescriptor(ResourceManager.getPluginImageDescriptor("edu.tum.cs.mylyn.provisioning.git",
				"icons/import_wiz.png"));
		this.repository = repositoryInfo;
	}

	public void createControl(final Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 10;
		layout.marginWidth = layout.marginHeight = 16;
		container.setLayout(layout);
		setControl(container);
		addDestination(container);
		addExecuteTask(container);
	}

	private void addExecuteTask(final Composite grpRepo) {
		Label lblTo = new Label(grpRepo, SWT.NONE);
		lblTo.setText("Repository");
		lblTo.setBounds(0, 0, 100, 50);

		textSource = new Text(grpRepo, SWT.BORDER);
		URIish uri = GitProvisioningUtil.getCloneUri(repository);
		textSource.insert(uri.toString());

		Label lblBlank = new Label(grpRepo, SWT.NONE);
		lblBlank.setText("");

		Label lblImport = new Label(grpRepo, SWT.NONE);
		lblImport.setText("Import projects from repository");

		btnImportProjects = new Button(grpRepo, SWT.CHECK);
		btnImportProjects.setSelection(true);

		Label lblSpacer = new Label(grpRepo, SWT.NONE);
		lblSpacer.setText("");

		Label lblExecute = new Label(grpRepo, SWT.NONE);
		lblExecute.setText("Execute this Job");

		btnExecuteTask = new Button(grpRepo, SWT.CHECK);
		btnExecuteTask.setSelection(true);
		btnExecuteTask.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (btnExecuteTask.getSelection()) {
					textDestination.setEnabled(true);
					btnSelectDestination.setEnabled(true);
					textSource.setEnabled(true);
					btnImportProjects.setEnabled(true);
				} else {
					textDestination.setEnabled(false);
					btnSelectDestination.setEnabled(false);
					textSource.setEnabled(false);
					btnImportProjects.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		Label lblBlank2 = new Label(grpRepo, SWT.NONE);
		lblBlank2.setText("");

	}

	private void addDestination(final Composite grpRepo) {
		Label lblTo = new Label(grpRepo, SWT.NONE);
		lblTo.setText("Local Directory");

		textDestination = new Text(grpRepo, SWT.BORDER | SWT.PUSH);
		textDestination.setText(getDefaultDestination());

		btnSelectDestination = new Button(grpRepo, SWT.NONE);
		btnSelectDestination.setText("Select Directory");
		btnSelectDestination.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dlg = new DirectoryDialog(grpRepo.getShell());
				dlg.setFilterPath(textDestination.getText());
				dlg.setText("Select Directory");
				dlg.setMessage("Select a directory");
				String currentDirectory = dlg.open();
				if (currentDirectory != null) {
					textDestination.setText(currentDirectory);
				}
			}
		});
	}

	private String getDefaultDestination() {
		File workspaceRoot = getWorkspaceRoot();
		File file = new File(workspaceRoot, "git" + File.separator + GitProvisioningUtil.getProjectName(repository));
		if (GitProvisioningUtil.isValidGitRepository(new File(file, ".git"))) {
			Timestamp tstamp = new Timestamp(System.currentTimeMillis());
			file = new File(workspaceRoot, "git" + File.separator + GitProvisioningUtil.getProjectName(repository)
					+ "." + tstamp.getTime());
		}

		return file.toString();
	}

	@Override
	public boolean isPageComplete() {
		return true;
	}

	private File getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
	}

	public String getTextSource() {
		return textSource.getText();
	}

	public String getTextDestination() {
		return textDestination.getText();
	}

	public boolean getExecuteTask() {
		return btnExecuteTask.getSelection();
	}

	public RepositoryWrapper getRepository() {
		return repository;
	}

	public Button getImportProjects() {
		return btnImportProjects;
	}
}
