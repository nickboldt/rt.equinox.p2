package org.eclipse.equinox.internal.frameworkadmin.equinox;

import java.io.File;
import java.util.ArrayList;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.internal.provisional.frameworkadmin.LauncherData;

public class ParserUtils {
	public static File getOSGiInstallArea(LauncherData launcherData) {
		if (launcherData == null)
			return null;

		File result = getOSGiInstallArea(launcherData.getProgramArgs());
		if (result != null)
			return result;

		if (launcherData.getFwJar() != null)
			return fromOSGiJarToOSGiInstallArea(launcherData.getFwJar().getAbsolutePath());
		if (launcherData.getLauncher() != null)
			return launcherData.getLauncher().getParentFile();
		return null;
	}

	public static File getOSGiInstallArea(String[] args) {
		if (args == null)
			return null;
		String install = getValueForArgument(EquinoxConstants.OPTION_INSTALL, args);
		if (install != null)
			return new File(install);
		String startup = getValueForArgument(EquinoxConstants.OPTION_STARTUP, args);
		if (startup != null)
			return fromOSGiJarToOSGiInstallArea(startup);
		return null;
	}

	private static File fromOSGiJarToOSGiInstallArea(String path) {
		IPath parentFolder = new Path(path).removeLastSegments(1);
		if (parentFolder.lastSegment().equalsIgnoreCase("plugins")) //$NON-NLS-1$
			return parentFolder.removeLastSegments(1).toFile();
		return parentFolder.toFile();
	}

	public static boolean isArgumentSet(String arg, String[] args) {
		if (arg == null || args == null || args.length == 0)
			return false;
		for (int i = 0; i < args.length; i++) {
			if (args[i] == null)
				continue;
			if (args[i].equalsIgnoreCase(arg)) {
				return true;
			}
		}
		return false;
	}

	public static String getValueForArgument(String arg, String[] args) {
		if (arg == null || args == null || args.length == 0)
			return null;
		for (int i = 0; i < args.length; i++) {
			if (args[i] == null)
				continue;
			if (args[i].equalsIgnoreCase(arg)) {
				if (i + 1 < args.length && args[i + 1] != null && args[i + 1].charAt(1) != '-')
					return args[i + 1];
			}
		}
		return null;
	}

	public static String[] getMultiValuedArgument(String arg, String[] args) {
		if (arg == null || args == null || args.length == 0)
			return null;
		ArrayList values = null;
		for (int i = 0; i < args.length; i++) {
			if (args[i] == null)
				continue;
			if (arg.equalsIgnoreCase(args[i])) {
				values = new ArrayList();
				continue;
			}
			if (values != null && args[i].charAt(1) == '-') {
				break;
			}
			values.add(args[i].trim());
		}
		if (values != null)
			return (String[]) values.toArray(new String[values.size()]);
		return null;
	}

	public static boolean setValueForArgument(String arg, String value, String[] args) {
		if (arg == null || args == null || args.length == 0)
			return false;
		for (int i = 0; i < args.length; i++) {
			if (args[i] == null)
				continue;
			String currentArg = args[i].trim();
			if (currentArg.equalsIgnoreCase(arg)) {
				if (i + 1 < args.length && args[i + 1] != null && args[i + 1].charAt(1) != '-') {
					args[i + 1] = value;
					return true;
				}
			}
		}
		return false;
	}
}
