package com.aefyr.flexfilter.applier;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Runs the (potentially expensive) filter/sort pass on a background thread
 * and publishes the result via LiveData, discarding results from any
 * apply() call superseded by a newer one (important since BackupViewModel's
 * search fires on every keystroke).
 */
public class LiveFilterApplier<T> {
    private final MutableLiveData<List<T>> liveData = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicInteger requestToken = new AtomicInteger(0);

    public LiveData<List<T>> asLiveData() {
        return liveData;
    }

    public void apply(ComplexCustomFilter<T> filter, List<T> items) {
        int myToken = requestToken.incrementAndGet();
        executor.execute(() -> {
            List<T> result = filter.apply(items);
            if (requestToken.get() == myToken) {
                liveData.postValue(result);
            }
        });
    }
}
