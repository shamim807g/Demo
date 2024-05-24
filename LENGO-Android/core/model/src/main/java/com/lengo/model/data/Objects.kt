package com.lengo.model.data

import javax.annotation.concurrent.Immutable

@Immutable
data class Objects(
    val func: String,
    val lec: Long,
    val obj: Long,
    val owner: Long,
    val pck: Long,
    val value: Map<String, Any>? //Map<String,Any>
)