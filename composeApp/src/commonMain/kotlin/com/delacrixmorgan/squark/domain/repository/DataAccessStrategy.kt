package com.delacrixmorgan.squark.domain.repository

enum class DataAccessStrategy {
    /**
     * This strategy doesn't fetch from remote if the local data is not expired. (default)
     * 1. If local data exists (no matter if it's expired) -> emit it
     * 2. If there is no local data or it is expired -> fetch from remote and save it locally
     *    2a. The updated local data is emitted
     */
    FromLocalOnlyIfNotExpired,

    /**
     * This strategy always fetches from both local and remote.
     * 1. If local data exists (no matter if it's expired) -> emit it
     * 2. Fetch from remote and save it locally (no matter if the local data was not expired)
     *    2a. The updated local data is emitted
     */
    FromLocalAndRemote,

    /**
     * This strategy always fetches from remote (ignoring local data).
     * 1. Fetch from remote and save it locally (no matter if the local data was not expired)
     *    1a. The updated local data is emitted
     */
    FromRemoteOnly,

    /**
     * This strategy always fetches from local.
     * 1. If local data exists (no matter if it's expired) -> emit it
     */
    FromLocalOnly,
}
