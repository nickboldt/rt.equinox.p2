/*******************************************************************************
 * Copyright (c) 2008 Code 9 and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Code 9 - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.p2.installer;

import org.osgi.framework.Version;

public class VersionedName {
	private String id;
	private Version version;

	/**
	 * Creates and returns a new versioned id from the given spec.  The spec should be
	 * id/version.
	 * @param spec the spec for the versioned id to create
	 * @return the parsed versioned id
	 */
	public static VersionedName parse(String spec) {
		String[] segments = InstallDescriptionParser.getArrayFromString(spec, "/"); //$NON-NLS-1$
		return new VersionedName(segments[0], segments.length == 1 ? null : segments[1]);
	}

	public VersionedName(String id, String version) {
		this.id = id;
		this.version = new Version(version == null ? "0.0.0" : version);
	}

	public VersionedName(String id, Version version) {
		this.id = id;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public Version getVersion() {
		return version;
	}

	public String toString() {
		return id + "/" + (version == null ? "0.0.0" : version.toString());
	}
}
