package org.valdi.animedownloader.app.download;

/**
 * Enum representing the state of a download.
 */
public enum DownloadState {
    /**
     * The download is waiting to start.
     */
    WAITING(false),
    /**
     * The download has stopped since a file with that name already exists.
     */
    FILE_ALREADY_EXISTS(true),
    /**
     * The download is still active.
     */
    RUNNING(false),
    /**
     * The download has failed.
     */
    FAILED(true),
    /**
     * The download has been cancelled.
     */
    CANCELLED(true),
    /**
     * The download has ended without errors.
     */
    SUCCEEDED(true);

    private final boolean ended;

    DownloadState(boolean ended) {
        this.ended = ended;
    }

    /**
     * Indicates whether this download has ended.
     *
     * @return true if ended, false otherwise
     */
    public boolean isEnded() {
        return this.ended;
    }

}
