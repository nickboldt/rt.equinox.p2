/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.p2.ui;

import org.eclipse.equinox.p2.metadata.ILicense;

import java.io.*;

/**
 * LicenseManager defines a service which records the licenses that have been
 * accepted in the course of installing or updating software. It can be used to determine
 * whether a particular license should be presented to a user for acceptance, and
 * to record the user's decision.
 * 
 * @since 2.0
 */
public abstract class LicenseManager {

	/**
	 * Record the acceptance of the specified license.
	 * 
	 * @param license the license to be accepted
	 * 
	 * @return <code>true</code> if the license was recorded as accepted, <code>false</code> if
	 * it was not.
	 * 
	 */
	public abstract boolean accept(ILicense license);

	/**
	 * Record the rejection of the specified license.
	 * 
	 * @param license the license to be rejected
	 * 
	 * @return <code>true</code> if the license was recorded as rejected, <code>false</code> if
	 * it was not.
	 * 
	 */
	public abstract boolean reject(ILicense license);

	/**
	 * Return a boolean indicating whether a particular license has previously
	 * been accepted.
	 * 
	 * @param license the license in question
	 * 
	 * @return <code>true</code> if the license has previously been accepted,
	 * <code>false</code> if it has not been accepted before.
	 * 
	 */
	public abstract boolean isAccepted(ILicense license);

	/**
	 * Return a boolean indicating whether any licenses have been
	 * accepted.
	 * 
	 * @return <code>true</code> if accepted licenses have been recorded,
	 * <code>false</code> if there have been no licenses accepted.

	 */
	public abstract boolean hasAcceptedLicenses();

	/**
	 * Write the accepted licenses list to the specified stream.
	 * @param stream the stream to which licenses should be written
	 * @throws IOException 
	 */
	public abstract void write(OutputStream stream) throws IOException;

	/**
	 * Read the accepted licenses list from the specified stream.
	 * @param stream the stream from which to read the licenses
	 * @throws IOException 
	 */
	public abstract void read(InputStream stream) throws IOException;

}
