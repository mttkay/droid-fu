package com.github.droidfu.support;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.text.TextUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { TextUtils.class })
public class StringSupportTest {

    @Before
    public void mockAndroidFrameWorkClasses() {
        // mock TextUtils
        mockStatic(TextUtils.class);
    }

    @Test
    public void splitByCharacterType() {

        assertArrayEquals(new String[] {}, StringSupport.splitByCharacterTypeCamelCase(""));

        assertArrayEquals(new String[] { "Hello" },
            StringSupport.splitByCharacterTypeCamelCase("Hello"));

        assertArrayEquals(new String[] { "Hello", "World" },
            StringSupport.splitByCharacterTypeCamelCase("HelloWorld"));

    }

    @Test
    public void underscore() {

        when(TextUtils.join("_", new String[] { "Hello", "World" })).thenReturn("Hello_World");

        assertEquals("hello_world", StringSupport.underscore("HelloWorld"));

        verifyStatic();
    }
}
