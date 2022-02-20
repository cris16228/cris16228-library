package com.github.cris16228.library.http.image_loader.interfaces;

import com.github.cris16228.library.http.image_loader.MemoryCache;

public interface ConnectionErrors {

    void FileNotFound();

    default void OutOfMemory(MemoryCache memoryCache) {
        memoryCache.clear();
    }

    void NormalError();
}
