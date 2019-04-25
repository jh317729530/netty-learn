package cn.gunn.netty.future.impl;

import cn.gunn.netty.future.GFuture;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.Promise;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class RedisFuture<T> extends CompletableFuture<T> implements GFuture<T> {

    private final Promise<T> promise = ImmediateEventExecutor.INSTANCE.newPromise();

    @Override
    public boolean isSuccess() {
        return promise.isSuccess();
    }

    @Override
    public boolean isDone() {
        return promise.isDone();
    }

    @Override
    public void onComplete(BiConsumer<? super T, ? super Throwable> action) {
        promise.addListener(f -> {
            if (!f.isSuccess()) {
                action.accept(null, f.cause());
                return;
            }

            action.accept((T) f.getNow(), null);
        });
    }

    @Override
    public T getNow() {
        return promise.getNow();
    }

    @Override
    public boolean trySuccess(T result) {
        if (promise.trySuccess(result)) {
            complete(result);
            return true;
        }
        return false;
    }

}
