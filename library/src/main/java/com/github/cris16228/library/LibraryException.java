package com.github.cris16228.library;

public class LibraryException extends Exception {


    public LibraryException(Class<?> fileClass, String errorMessage) {
        super("[CommonUtils] " + fileClass + ": " + errorMessage);
    }
}
