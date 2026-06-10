package com.mrkefish.kspw_api.data

import kotlinx.serialization.Serializable


@Serializable
data class WebHook(
    val url: String
)