package org.valdi.animedownloader.app.download;

/**
 * Enum representing the state of a download.
 */
public enum DownloadState {
    /**
     * The download is waiting to start.
     */
    WAITING,
    /**
     * The download has stopped since a file with that name already exists.
     */
    FILE_ALREADY_EXISTS,
    /**
     * The download is still active.
     */
    RUNNING,
    /**
     * The download has failed.
     */
    FAILED,
    /**
     * The download has been cancelled.
     */
    CANCELLED,
    /**
     * The download has ended without errors.
     */
    SUCCEEDED,
}
