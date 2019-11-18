package net.imknown.android.forefrontinfo.ui.settings.model

import androidx.annotation.Keep

@Keep
data class GithubReleaseInfo(
    val tag_name: String,
    val name: String,
    val body: String,
    val prerelease: String,
    val created_at: String,
    val published_at: String,
    val assets: List<Asset>
) {
    @Keep
    data class Asset(
        val name: String,
        val size: Int,
        val content_type: String,
        val browser_download_url: String
    )
}