package com.sumon.bundleapp.installer.shell;

import android.util.Log;

import androidx.annotation.Nullable;

import com.sumon.bundleapp.installer.utils.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A long-lived shell that commands are fed into, so a shell is spawned once instead of per command
 * - a su cold start or a Shizuku process is expensive.
 * <p>
 * Commands that pipe data through stdin cannot use this, since stdin carries the command stream.
 */
class PersistentShellSession {

    private final String mTag;
    private final ProcessFactory mProcessFactory;
    private final StringBuilder mErrBuffer = new StringBuilder();
    /** Echoed after every command to mark where its output ends and carry the exit code. */
    private final String mMarker;
    private final String mStderrMarker;

    private Process mProcess;
    private Writer mIn;
    private BufferedReader mOut;
    private BufferedReader mErr;
    private Thread mErrPump;
    private volatile boolean mStderrMarkerSeen;

    interface ProcessFactory {
        Process start() throws Exception;
    }

    PersistentShellSession(String tag, ProcessFactory processFactory) {
        mTag = tag;
        mProcessFactory = processFactory;
        String uuid = UUID.randomUUID().toString().replace("-", "");
        mMarker = "__SAI_CMD_DONE_" + uuid + "__";
        mStderrMarker = "__SAI_STDERR_DONE_" + uuid + "__";
    }

    /** @param validator run once after the shell starts; the session is discarded if it fails */
    synchronized boolean ensureStarted(@Nullable Validator validator) {
        if (mProcess != null && isAlive())
            return true;

        close();

        try {
            mProcess = mProcessFactory.start();
            mIn = new OutputStreamWriter(mProcess.getOutputStream(), StandardCharsets.UTF_8);
            mOut = new BufferedReader(new InputStreamReader(mProcess.getInputStream(), StandardCharsets.UTF_8));
            mErr = new BufferedReader(new InputStreamReader(mProcess.getErrorStream(), StandardCharsets.UTF_8));
            startErrPump(mErr);
        } catch (Exception e) {
            Log.w(mTag, "Unable to start shell session", e);
            close();
            return false;
        }

        if (validator != null) {
            try {
                if (!validator.isValid(this)) {
                    close();
                    return false;
                }
            } catch (Exception e) {
                Log.w(mTag, "Shell session validation failed", e);
                close();
                return false;
            }
        }

        return true;
    }

    synchronized Shell.Result exec(Shell.Command command) throws IOException {
        return exec(command, true);
    }

    synchronized Shell.Result exec(Shell.Command command, boolean redirectStdin) throws IOException {
        if (mIn == null || mOut == null)
            throw new IOException("Shell session is not running");

        takeStderr();
        mStderrMarkerSeen = false;

        // Redirect stdin to /dev/null by default so commands can't consume the marker stream
        String cmdString = command.toString();
        if (redirectStdin && !cmdString.contains("<")) {
            cmdString = cmdString + " </dev/null";
        }

        mIn.write(cmdString);
        mIn.write("\n");
        mIn.write("echo " + mMarker + " $?\n");
        mIn.write("echo " + mStderrMarker + " >&2\n");
        mIn.flush();

        StringBuilder out = new StringBuilder();
        int exitCode = -1;
        boolean finished = false;
        String line;
        long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30);

        while ((line = mOut.readLine()) != null) {
            if (System.currentTimeMillis() > deadline) {
                close();
                throw new IOException("Command timed out after 30 seconds");
            }

            // A command whose last line lacks a trailing newline would otherwise glue itself to
            // the marker, so match anywhere in the line instead of only at its start.
            int markerIndex = line.indexOf(mMarker);
            if (markerIndex >= 0) {
                if (markerIndex > 0)
                    appendLine(out, line.substring(0, markerIndex));

                try {
                    exitCode = Integer.parseInt(line.substring(markerIndex + mMarker.length()).trim());
                } catch (NumberFormatException ignored) {
                }

                finished = true;
                break;
            }

            appendLine(out, line);
        }

        if (!finished)
            throw new IOException("Shell session closed unexpectedly");

        // Wait for stderr marker before taking stderr
        long stderrDeadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);
        while (!mStderrMarkerSeen && System.currentTimeMillis() < stderrDeadline) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return new Shell.Result(command, exitCode, out.toString().trim(), takeStderr());
    }

    /**
     * stderr is drained by a dedicated thread: leaving it unread until the command finishes lets a
     * chatty command fill the pipe buffer, block the shell and deadlock the session.
     */
    private void startErrPump(BufferedReader reader) {
        Thread pump = new Thread(() -> {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(mStderrMarker)) {
                        mStderrMarkerSeen = true;
                        continue;
                    }
                    synchronized (mErrBuffer) {
                        appendLine(mErrBuffer, line);
                    }
                }
            } catch (IOException ignored) {
            }
        }, mTag + "-stderr");
        pump.setDaemon(true);
        mErrPump = pump;
        pump.start();
    }

    private String takeStderr() {
        synchronized (mErrBuffer) {
            String err = mErrBuffer.toString().trim();
            mErrBuffer.setLength(0);
            return err;
        }
    }

    private static void appendLine(StringBuilder builder, String line) {
        if (builder.length() > 0)
            builder.append('\n');
        builder.append(line);
    }

    private boolean isAlive() {
        Process process = mProcess;
        if (process == null)
            return false;

        try {
            process.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

    synchronized void close() {
        IOUtils.closeSilently(mIn);
        IOUtils.closeSilently(mOut);
        IOUtils.closeSilently(mErr);

        if (mProcess != null)
            mProcess.destroy();

        if (mErrPump != null)
            mErrPump.interrupt();

        mIn = null;
        mOut = null;
        mErr = null;
        mErrPump = null;
        mProcess = null;

        takeStderr();
    }

    interface Validator {
        boolean isValid(PersistentShellSession session) throws Exception;
    }
}
