package com.gallatinsystems.survey.device.exception;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.os.Environment;
import android.util.Log;

import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.FileUtil;

/**
 * This exception handler will log all exceptions it handles to the filesystem
 * so they can be processed later. This sets the default uncaught exception
 * handler. It will delegate processing to the previously installed uncaught
 * exception handler (if there is one) to preserve normal system operation. This
 * class is a singleton to preserve the chain of exception handlers.
 * 
 * @author Christopher Fagiani
 * 
 */
public class PersistentUncaughtExceptionHandler implements
		UncaughtExceptionHandler {

	private static final String TAG = "UNCAUGHT_EXCEPTION_HANDLER";
	private static PersistentUncaughtExceptionHandler instance;

	private UncaughtExceptionHandler oldHandler;

	public static final PersistentUncaughtExceptionHandler getInstance() {
		if (instance == null) {
			instance = new PersistentUncaughtExceptionHandler();
		}
		return instance;
	}

	/**
	 * installs the old uncaught exception handler in a member variable so it
	 * can be invoked later
	 */
	private PersistentUncaughtExceptionHandler() {
		if (Thread.getDefaultUncaughtExceptionHandler() != null
				&& !(Thread.getDefaultUncaughtExceptionHandler() instanceof PersistentUncaughtExceptionHandler)) {
			oldHandler = Thread.getDefaultUncaughtExceptionHandler();
		}

	}

	/**
	 * saves the exception to the filesystem. Processing will then be delegated
	 * to the previously installed uncaught exception handler
	 */
	@Override
	public void uncaughtException(Thread sourceThread, Throwable exception) {

		recordException(exception);
		// Still process the exception with the default handler so we don't
		// change system behavior
		if (oldHandler != null) {
			oldHandler.uncaughtException(sourceThread, exception);
		}
	}

	/**
	 * saves the exception to the filesystem. this can be used to save otherwise
	 * handled exceptions so they can be reported to the server.
	 * 
	 * @param exception
	 */
	public static void recordException(Throwable exception) {
		// save the error
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		exception.printStackTrace(printWriter);

		try {
			FileOutputStream out = null;
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				out = FileUtil.getFileOutputStream(
						ConstantUtil.STACKTRACE_FILENAME
								+ Long.toString(System.currentTimeMillis())
								+ ConstantUtil.STACKTRACE_SUFFIX,
						ConstantUtil.STACKTRACE_DIR, "false", null);
			} else {
				out = FileUtil.getFileOutputStream(
						ConstantUtil.STACKTRACE_FILENAME
								+ Long.toString(System.currentTimeMillis())
								+ ConstantUtil.STACKTRACE_SUFFIX,
						ConstantUtil.STACKTRACE_DIR, "false", null);
			}

			FileUtil.writeStringToFile(result.toString(), out);
		} catch (IOException e) {
			Log.e(TAG, "Couldn't save trace file", e);
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (IOException e) {
					Log.w(TAG, "Can't close print writer object", e);
				}
			}
		}
	}

}
