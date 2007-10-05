/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation Stefan Liebig -
 * random tweaks
 ******************************************************************************/
package org.eclipse.equinox.internal.p2.artifact.repository;

import java.io.IOException;
import java.io.OutputStream;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.filetransfer.*;
import org.eclipse.ecf.filetransfer.events.*;
import org.eclipse.ecf.filetransfer.identity.FileCreateException;
import org.eclipse.ecf.filetransfer.identity.FileIDFactory;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransferFactory;
import org.eclipse.equinox.p2.artifact.repository.processing.ProcessingStep;
import org.eclipse.equinox.p2.core.helpers.LogHelper;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A transport implementation that uses ECF file transfer API.
 */
public class ECFTransport extends Transport {

	/**
	 * The singleton transport instance.
	 */
	private static ECFTransport instance;

	private final ServiceTracker retrievalFactoryTracker;

	/**
	 * Returns an initialized instance of ECFTransport
	 */
	public static synchronized ECFTransport getInstance() {
		if (instance == null) {
			instance = new ECFTransport();
		}
		return instance;
	}

	/**
	 * Private to avoid client instantiation.
	 */
	private ECFTransport() {
		retrievalFactoryTracker = new ServiceTracker(Activator.getContext(), IRetrieveFileTransferFactory.class.getName(), null);
		retrievalFactoryTracker.open();
	}

	protected IStatus convertToStatus(Exception failure) {
		if (failure == null)
			return Status.OK_STATUS;
		if (failure instanceof UserCancelledException)
			return new Status(IStatus.CANCEL, Activator.ID, failure.getMessage(), failure);
		return new Status(IStatus.ERROR, Activator.ID, "error during transfer", failure);
	}

	public IStatus download(String toDownload, OutputStream target, IProgressMonitor monitor) {
		IRetrieveFileTransferFactory factory = (IRetrieveFileTransferFactory) retrievalFactoryTracker.getService();
		if (factory == null)
			return new Status(IStatus.ERROR, Activator.ID, "ECF Transfer manager not available");
		return transfer(factory.newInstance(), toDownload, target, monitor);
	}

	private IStatus transfer(final IRetrieveFileTransferContainerAdapter retrievalContainer, final String toDownload, final OutputStream target, final IProgressMonitor monitor) {
		final IStatus[] result = new IStatus[1];
		IFileTransferListener listener = new IFileTransferListener() {
			public void handleTransferEvent(IFileTransferEvent event) {
				if (event instanceof IIncomingFileTransferReceiveStartEvent) {
					IIncomingFileTransferReceiveStartEvent rse = (IIncomingFileTransferReceiveStartEvent) event;
					try {
						if (target != null) {
							rse.receive(target);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (event instanceof IIncomingFileTransferReceiveDataEvent) {
					IIncomingFileTransfer source = ((IIncomingFileTransferReceiveDataEvent) event).getSource();
					monitor.subTask("Transferring " + source.getID().getName() + " (" + (int) (source.getPercentComplete() * 100) + "% complete)");
					if (monitor.isCanceled())
						source.cancel();
				}
				if (event instanceof IIncomingFileTransferReceiveDoneEvent) {
					IStatus status = convertToStatus(((IIncomingFileTransferReceiveDoneEvent) event).getException());
					synchronized (result) {
						result[0] = status;;
						result.notify();
					}
				}
			}
		};

		try {
			retrievalContainer.sendRetrieveRequest(FileIDFactory.getDefault().createFileID(retrievalContainer.getRetrieveNamespace(), toDownload), listener, null);
		} catch (IncomingFileTransferException e) {
			return new Status(IStatus.ERROR, Activator.ID, "error during transfer", e);
		} catch (FileCreateException e) {
			return new Status(IStatus.ERROR, Activator.ID, "error during transfer - could not create file", e);
		}
		synchronized (result) {
			while (result[0] == null) {
				boolean logged = false;
				try {
					result.wait();
				} catch (InterruptedException e) {
					if (!logged)
						LogHelper.log(new Status(IStatus.WARNING, Activator.ID, "Unexpected interrupt while waiting on ECF transfer", e)); //$NON-NLS-1$
				}
			}
		}

		if (!(target instanceof ProcessingStep))
			return result[0];
		IStatus stepStatus = ((ProcessingStep) target).getStatus(true);
		IStatus[] combinedList = new IStatus[stepStatus.getChildren().length + 1];
		combinedList[0] = result[0];
		System.arraycopy(stepStatus.getChildren(), 0, combinedList, 1, stepStatus.getChildren().length);
		int code = result[0].getCode() > stepStatus.getCode() ? result[0].getCode() : stepStatus.getCode();
		return new MultiStatus(Activator.ID, code, combinedList, "Problems occurred while downloading " + toDownload, null);
	}
}
