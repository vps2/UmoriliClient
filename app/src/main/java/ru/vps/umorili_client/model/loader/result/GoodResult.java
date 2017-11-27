package ru.vps.umorili_client.model.loader.result;

public class GoodResult<T> extends Result<T> {
    private final T result;

    public GoodResult(T result) {
        this.result = result;
    }

    @Override
    public T doGet() throws Throwable {
        return result;
    }
}
