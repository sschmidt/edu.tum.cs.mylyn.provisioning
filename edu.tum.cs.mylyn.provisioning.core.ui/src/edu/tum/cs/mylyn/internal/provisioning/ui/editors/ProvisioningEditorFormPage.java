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

package edu.tum.cs.mylyn.internal.provisioning.ui.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.DelayedRefreshJob;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextChangeEvent.ContextChangeKind;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.context.core.CompositeInteractionContext;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.navigator.CommonViewer;

import edu.tum.cs.mylyn.internal.provisioning.core.ProvisioningCorePlugin;
import edu.tum.cs.mylyn.internal.provisioning.ui.base.ProvisioningTaskSelector;
import edu.tum.cs.mylyn.provisioning.core.IProvisioningContextListener;
import edu.tum.cs.mylyn.provisioning.core.ProvisioningContext;
import edu.tum.cs.mylyn.provisioning.ui.ResourceManager;

/**
 * Parts of this editor page were copied from ContextEditorFormPage to imitate
 * it's look and feel.
 * 
 * @author Sebastian Schmidt
 */
public class ProvisioningEditorFormPage extends FormPage {
	public static final String CONTEXT_KEY = "Context";
	public static final String ID_VIEWER = "edu.tum.cs.mylyn.provisioning.context"; //$NON-NLS-1$
	private ScrolledForm form;
	private Composite sectionClient;
	private FormToolkit toolkit;
	private CommonViewer commonViewer;

	private ITask task;
	private IInteractionContext context;
	private ProvisioningContext provisioningContext;

	private ProvisioningEditorDelayedRefreshJob refreshJob;

	private Label deleteAllImage;
	private Label provisionAllImage;
	private Hyperlink deleteAllLink;
	private Hyperlink provisionAllLink;

	private IProvisioningContextListener provisioningContextChangeListener = new IProvisioningContextListener() {
		@Override
		public void contextChanged() {
			refresh();
		}
	};

	private AbstractContextListener contextChangeListener = new AbstractContextListener() {
		@Override
		public void contextChanged(ContextChangeEvent event) {
			if (ContextChangeKind.ACTIVATED.equals(event.getEventKind())
					|| ContextChangeKind.DEACTIVATED.equals(event.getEventKind())) {
				if (resolveCompositeContext(context).equals(resolveCompositeContext(event.getContext()))) {
					refresh();
				}
			}
		}
	};

	private class ProvisioningEditorDelayedRefreshJob extends DelayedRefreshJob {
		public ProvisioningEditorDelayedRefreshJob(StructuredViewer treeViewer, String name) {
			super(treeViewer, name);
		}

		@Override
		protected void doRefresh(Object[] items) {
			setLabelVisibilities();
			if (commonViewer != null && !commonViewer.getTree().isDisposed()) {
				commonViewer.refresh();
				if (items != null) {
					for (Object item : items) {
						updateExpansionState(item);
					}
				} else {
					updateExpansionState(null);
				}
			}
		}

		protected void updateExpansionState(Object item) {
			if (commonViewer != null && !commonViewer.getTree().isDisposed()) {
				try {
					commonViewer.getTree().setRedraw(false);
					commonViewer.expandAll();
				} finally {
					commonViewer.getTree().setRedraw(true);
				}
			}
		}

	}

	public ProvisioningEditorFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ContextCore.getContextManager().addListener(contextChangeListener);

		task = ((TaskEditorInput) getEditorInput()).getTask();
		form = managedForm.getForm();

		toolkit = managedForm.getToolkit();
		if (isActiveTask()) {
			context = ContextCore.getContextManager().getActiveContext();
		} else {
			context = ContextCorePlugin.getContextStore().loadContext(task.getHandleIdentifier());
		}
		provisioningContext = ProvisioningCorePlugin.getProvisioningContextManager().getProvisioningContext(context);
		provisioningContext.addChangeListener(provisioningContextChangeListener);

		form.getBody().setLayout(new FillLayout());
		Composite composite = new Composite(form.getBody(), SWT.NONE) {
			@Override
			public Point computeSize(int widhtHint, int heigtHint, boolean changed) {
				Rectangle clientArea = getClientArea();
				return super.computeSize(widhtHint, clientArea.height, changed);
			}
		};
		toolkit.adapt(composite);
		composite.setLayout(new GridLayout(2, false));
		createActionsSection(composite);
		createContentSection(composite);
		form.reflow(true);
	}

	@Override
	public void dispose() {
		super.dispose();
		disposeRefreshJob();
		if (provisioningContext != null) {
			provisioningContext.removeChangeListener(provisioningContextChangeListener);
		}
		ContextCore.getContextManager().removeListener(contextChangeListener);
	}

	private void createActionsSection(Composite composite) {
		Section section = toolkit
				.createSection(composite, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		section.setText("Actions");

		section.setLayout(new GridLayout());
		GridData sectionGridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		sectionGridData.widthHint = 80;
		section.setLayoutData(sectionGridData);

		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(2, false));
		sectionClient.setLayoutData(new GridData());

		deleteAllImage = toolkit.createLabel(sectionClient, "");
		deleteAllImage.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_CLEAR));
		deleteAllLink = toolkit.createHyperlink(sectionClient, "Remove all", SWT.NONE);
		deleteAllLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				provisioningContext.removeAll();
			}
		});
		provisionAllImage = toolkit.createLabel(sectionClient, "");
		provisionAllImage.setImage(ResourceManager.getPluginImageDescriptor("edu.tum.cs.mylyn.provisioning.core.ui",
				"icons/icon_prov_small.png").createImage());
		provisionAllLink = toolkit.createHyperlink(sectionClient, "Provision Elements", SWT.NONE);
		provisionAllLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				ProvisioningTaskSelector provisioner = new ProvisioningTaskSelector();
				provisioner.run(provisioningContext);
			}
		});

		section.setExpanded(true);
		setLabelVisibilities();
	}

	private void setLabelVisibilities() {
		boolean visible = isActiveTask();
		deleteAllImage.setVisible(visible);
		provisionAllImage.setVisible(visible);
		provisionAllLink.setVisible(visible);
		deleteAllLink.setVisible(visible);
	}

	private void refresh() {
		createRefreshJob();
		if (refreshJob != null) {
			refreshJob.refresh();
		}
	}

	private synchronized void createRefreshJob() {
		if (commonViewer == null) {
			return;
		}
		if (refreshJob == null) {
			refreshJob = new ProvisioningEditorDelayedRefreshJob(commonViewer, "refresh viewer"); //$NON-NLS-1$
		}
	}

	private void createContentSection(Composite composite) {
		Section section = toolkit
				.createSection(composite, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		section.setText("Elements");
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new FillLayout());
		section.setClient(sectionClient);
		createToolBar(section);

		if (commonViewer == null) {
			createViewer(sectionClient);
		}

		sectionClient.layout();

		toolkit.createLabel(composite, "  "); //$NON-NLS-1$
	}

	private void createToolBar(Section section) {
		Composite composite = toolkit.createComposite(section);
		composite.setBackground(null);
		section.setTextClient(composite);
		ToolBarManager manager = new ToolBarManager(SWT.FLAT);
		manager.add(new Action("Collapse all", CommonImages.COLLAPSE_ALL_SMALL) {
			@Override
			public void run() {
				if (commonViewer != null && commonViewer.getTree() != null && !commonViewer.getTree().isDisposed()) {
					commonViewer.collapseAll();
				}
			}
		});
		manager.add(new Action("Expand all", CommonImages.EXPAND_ALL_SMALL) {
			@Override
			public void run() {
				if (commonViewer != null && commonViewer.getTree() != null && !commonViewer.getTree().isDisposed()) {
					commonViewer.expandAll();
				}
			}
		});
		manager.createControl(composite);
	}

	private synchronized void disposeRefreshJob() {
		if (refreshJob != null) {
			refreshJob.cancel();
			refreshJob = null;
		}
	}

	private boolean isActiveTask() {
		return task.equals(TasksUi.getTaskActivityManager().getActiveTask());
	}

	private void createViewer(Composite parent) {
		commonViewer = new CommonViewer(ID_VIEWER, parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		commonViewer.setData(CONTEXT_KEY, context);
		PermissiveInterestFilter interestFilter = new PermissiveInterestFilter();
		commonViewer.addFilter(interestFilter);
		commonViewer.setUseHashlookup(true);
		try {
			commonViewer.getControl().setRedraw(false);
			hookContextMenu();
			commonViewer.setInput(getSite().getPage().getInput());
			commonViewer.expandAll();
		} finally {
			commonViewer.getControl().setRedraw(true);
		}
		refresh();
	}

	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager("#PopupMenu");
		Menu menu = menuManager.createContextMenu(commonViewer.getControl());
		commonViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, commonViewer);
	}

	public ISelection getSelection() {
		if (getSite() != null && getSite().getSelectionProvider() != null) {
			return getSite().getSelectionProvider().getSelection();
		} else {
			return StructuredSelection.EMPTY;
		}
	}

	@SuppressWarnings("restriction")
	private IInteractionContext resolveCompositeContext(IInteractionContext context) {
		if (context instanceof CompositeInteractionContext) {
			CompositeInteractionContext compositeInteractionContext = (CompositeInteractionContext) context;
			if (compositeInteractionContext.getContextMap().size() == 1) {
				return compositeInteractionContext.getContextMap().values().iterator().next();
			}
		}
		return context;
	}

}
