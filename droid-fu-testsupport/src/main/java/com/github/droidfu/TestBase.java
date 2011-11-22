package com.github.droidfu;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Log.class })
public class TestBase {

    @Before
    public void mockLogger() {
        // redirect Logger output to STDOUT
        mockStatic(Log.class);
        when(Log.d(any(String.class), any(String.class))).thenAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                String tag = (String) invocation.getArguments()[0];
                String msg = (String) invocation.getArguments()[1];
                System.out.println(new StringBuilder("[").append(tag).append("] ").append(msg)
                        .toString());
                return 0;
            }
        });
    }

}
