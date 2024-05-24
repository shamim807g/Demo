package com.lengo.modalsheet

/**
 * Used for annotating experimental modal sheet API that is likely to change or be removed in the
 * future.
 */
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    "This API is experimental and is likely to change or to be removed in the future."
)
public annotation class ExperimentalSheetApi