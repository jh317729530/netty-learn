package cn.gunn.netty.future;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

public interface GFuture<V> extends Future<V>, CompletionStage<V> {

    boolean isSuccess();

    void onComplete(BiConsumer<? super V, ? super Throwable> action);

    V getNow();

    boolean trySuccess(V result);
}
