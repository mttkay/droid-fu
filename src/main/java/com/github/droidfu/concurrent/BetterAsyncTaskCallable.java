package com.github.droidfu.concurrent;

public interface BetterAsyncTaskCallable<ParameterT, ProgressT, ReturnT> {

    public ReturnT call(BetterAsyncTask<ParameterT, ProgressT, ReturnT> task)
            throws Exception;

}
