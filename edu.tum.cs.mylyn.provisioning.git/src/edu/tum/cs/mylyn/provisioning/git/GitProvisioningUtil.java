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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.SystemReader;

/**
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 */
public class GitProvisioningUtil {

	private static final Config systemConfig = SystemReader.getInstance().openSystemConfig(null, FS.DETECTED);
	private static final Config userConfig = SystemReader.getInstance().openUserConfig(systemConfig, FS.DETECTED);

	private static final String GERRIT_PUSH_REF = "refs/for/"; //$NON-NLS-1$
	private static final Object SCHEME_SSH = "ssh"; //$NON-NLS-1$

	public static List<URIish> getCloneUris(List<RepositoryWrapper> repositories) {
		List<URIish> cloneUris = new ArrayList<URIish>();
		for (RepositoryWrapper repository : repositories) {
			cloneUris.add(GitProvisioningUtil.getCloneUri(repository));
		}
		return cloneUris;
	}

	public static URIish getCloneUri(Config config, String branch) {
		String remote = config.getString(ConfigConstants.CONFIG_BRANCH_SECTION, branch,
				ConfigConstants.CONFIG_KEY_REMOTE);
		String uri = config.getString(ConfigConstants.CONFIG_REMOTE_SECTION, remote, ConfigConstants.CONFIG_KEY_URL);
		try {
			return new URIish(uri);
		} catch (URISyntaxException e) {
			return null;
		}
	}

	public static URIish getCloneUri(Repository repository) {
		try {
			return getCloneUri(repository.getConfig(), repository.getBranch());
		} catch (IOException e) {
			return null;
		}
	}

	public static String getProjectName(RepositoryWrapper project) {
		URIish uriIsh = getCloneUri(project);
		return getProjectName(uriIsh);
	}

	public static String getProjectName(URIish cloneUri) {
		if (cloneUri != null) {
			try {
				URI uri = new URI(cloneUri.toString());
				String path = uri.getPath();
				String projectName = path.substring(path.lastIndexOf('/') + 1);
				if (projectName.length() == 0) {
					path = path.substring(0, path.length() - 1);
					projectName = path.substring(path.lastIndexOf('/') + 1);
				}

				if (projectName.length() < 4) {
					return GitProvisioningUtil.getProjectNameFallback(cloneUri);
				}
				return projectName;
			} catch (URISyntaxException e) {
				return GitProvisioningUtil.getProjectNameFallback(cloneUri);
			}
		}

		return GitProvisioningUtil.getProjectNameFallback(cloneUri);
	}

	private static String getProjectNameFallback(URIish project) {
		return project.toString();
	}

	public static URIish getGerritUri(RepositoryWrapper repository) {
		for (RemoteConfig config : repository.getRemoteConfigs()) {
			for (RefSpec ref : config.getPushRefSpecs()) {
				if (ref.getDestination().startsWith(GERRIT_PUSH_REF)) {
					return config.getURIs().get(0);
				}
			}
		}

		return null;
	}

	public static boolean mightUseKeyAuth(URIish uri) {
		return SCHEME_SSH.equals(uri.getScheme());
	}

	public static boolean isValidGitRepository(File file) {
		File configFile = new File(file, "config");
		if (!file.exists() || !configFile.exists()) {
			return false;
		}

		Config config = new Config(userConfig);
		try {
			config.fromText(FileUtils.readFileToString(configFile));
		} catch (ConfigInvalidException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		return true;
	}

}
