package ru.vps.umorili_client.model.loader.result;

public class ErrorResult<T> extends Result<T> {
    private final Throwable throwable;

    public ErrorResult(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public T doGet() throws Throwable {
        throw throwable;
    }
}
