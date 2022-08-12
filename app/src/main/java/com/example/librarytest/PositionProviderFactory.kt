package com.example.librarytest

import android.content.Context

object PositionProviderFactory {

    fun create(context: Context, listener: PositionProvider.PositionListener): PositionProvider {
        return AndroidPositionProvider(context, listener)
    }
}