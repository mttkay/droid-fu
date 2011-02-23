package com.github.droidfu.cachefu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

import com.github.droidfu.TestBase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Log.class })
public class ModelCacheTest extends TestBase {

    class TestObject extends CachedObject {

        private static final long serialVersionUID = 4068669033000677879L;

        private String testString;

        public String getTestString() {
            return testString;
        }

        public void setTestString(String testString) {
            this.testString = testString;
        }

        @Override
        public void reloadFromCachedObject(ModelCache modelCache, CachedObject cachedObject) {
            TestObject cachedTestObject = (TestObject) cachedObject;
            testString = cachedTestObject.testString;
        }

        @Override
        public String createKey(String id) {
            return "test_object_" + id;
        }

    }

    @Test
    public void testCachesObject() {
        ModelCache modelCache = new ModelCache(1, 1, 1);
        String key = "test_object";

        assertNull(modelCache.get(key));

        TestObject preObject = new TestObject();
        preObject.setTestString("this is a test");

        modelCache.put(key, preObject);

        TestObject postObject = (TestObject) modelCache.get(key);
        assertEquals("this is a test", postObject.getTestString());
    }

}
