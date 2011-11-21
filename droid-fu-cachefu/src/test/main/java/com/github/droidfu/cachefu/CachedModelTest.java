package com.github.droidfu.cachefu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
public class CachedModelTest extends TestBase {

    class TestObject extends CachedModel {

        private String testString;

        public TestObject() {
            super();
        }

        public TestObject(String id) {
            super(id);
        }

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
    public void testSaveStoresInCache() {
        ModelCache modelCache = new ModelCache(1, 1, 1);
        String id = "123";

        TestObject preObject = new TestObject(id);
        preObject.setTestString("this is a test");
        assertTrue(preObject.save(modelCache));

        TestObject postObject = (TestObject) modelCache.get(preObject.getKey());
        assertEquals("this is a test", postObject.getTestString());
    }

    @Test
    public void testSaveFailsWithoutObjectCacheOrId() {
        ModelCache modelCache = new ModelCache(1, 1, 1);
        String id = "123";
        TestObject testObject = new TestObject();

        assertFalse(testObject.save(modelCache));

        testObject.setId(id);
        assertFalse(testObject.save(null));

        testObject.setId(null);
        assertFalse(testObject.save(modelCache));

        testObject.setId(id);
        assertTrue(testObject.save(modelCache));
    }

    @Test
    public void testReloadUpdatesObjectWithCachedData() {
        ModelCache modelCache = new ModelCache(1, 1, 1);
        String id = "123";

        TestObject originalObject = new TestObject(id);
        originalObject.setTestString("original text");
        originalObject.save(modelCache);

        TestObject overridingObject = new TestObject(id);
        overridingObject.setTestString("this is new text");
        overridingObject.save(modelCache);

        originalObject.reload(modelCache);
        assertEquals("this is new text", originalObject.getTestString());
    }

}
