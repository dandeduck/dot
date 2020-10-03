package com.dot.maps;

public interface Callback<T> {
    void onSuccess(T result);
    void onFailure(Throwable error);
}
