package ru.vps.umorili_client.model.loader.result;

public abstract class Result<T> {
    private boolean used;

    public final boolean isUsed() {
        return used;
    }

    public final T get() throws Throwable {
        used = true;

        return doGet();
    }

    protected abstract T doGet() throws Throwable;
}
