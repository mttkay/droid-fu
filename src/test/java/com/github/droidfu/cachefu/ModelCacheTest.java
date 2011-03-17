package com.github.droidfu.cachefu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.os.Parcel;
import android.util.Log;

import com.github.droidfu.TestBase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Log.class })
public class ModelCacheTest extends TestBase {

    class TestObject extends CachedModel {

        private String testString;

        public String getTestString() {
            return testString;
        }

        public void setTestString(String testString) {
            this.testString = testString;
        }

        @Override
        public boolean reloadFromCachedModel(ModelCache modelCache, CachedModel cachedModel) {
            TestObject cachedTestObject = (TestObject) cachedModel;
            testString = cachedTestObject.testString;
            return false;
        }

        @Override
        public void readFromParcel(Parcel source) throws IOException {
            super.readFromParcel(source);
            testString = source.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(testString);
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
