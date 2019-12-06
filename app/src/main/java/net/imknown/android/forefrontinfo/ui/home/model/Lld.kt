package net.imknown.android.forefrontinfo.ui.home.model

import androidx.annotation.Keep

@Keep
data class Lld(
    val android: Androids,
    val linux: Types<BaseInfo>,
    val toybox: Types<BaseInfo>,
    val webView: WebViews
) : BaseInfo() {
    @Keep
    data class Androids(
        val securityPatchLevel: String,
        val stable: Android,
        val support: Android,
        val alpha: Android,
        val beta: Android
    ) {
        @Keep
        data class Android(
            val active: Boolean,
            val codename: String,
            val api: String
        ) : BaseInfo()
    }

    @Keep
    data class Types<T>(
        val stable: T,
        val support: T,
        val mainline: T,
        val master: T
    ) : BaseInfo() where T : BaseInfo

    @Keep
    data class WebViews(
        val stable: BaseInfo,
        val beta: BaseInfo,
        val dev: BaseInfo,
        val canary: BaseInfo
    )
}