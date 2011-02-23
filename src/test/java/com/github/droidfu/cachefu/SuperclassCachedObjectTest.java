package com.github.droidfu.cachefu;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

import com.github.droidfu.TestBase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Log.class })
public class SuperclassCachedObjectTest extends TestBase {

    class TestObject extends CachedObject {

        private static final long serialVersionUID = -9222018816042303591L;

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
        public void reloadFromCachedObject(ModelCache modelCache, CachedObject cachedObject) {
            TestObject cachedTestObject = (TestObject) cachedObject;
            testString = cachedTestObject.testString;
        }

        @Override
        public String createKey(String id) {
            return "test_object_" + id;
        }

    }

    class SubclassTestObject extends TestObject {

        private static final long serialVersionUID = -8273574679711783718L;

        private String extraString;

        public SubclassTestObject() {
            super();
        }

        public SubclassTestObject(String id) {
            super(id);
        }

        public String getExtraString() {
            return extraString;
        }

        public void setExtraString(String extraString) {
            this.extraString = extraString;
        }

        @Override
        public void reloadFromCachedObject(ModelCache modelCache, CachedObject cachedObject) {
            super.reloadFromCachedObject(modelCache, cachedObject);
            SubclassTestObject cachedTestObject = (SubclassTestObject) cachedObject;
            extraString = cachedTestObject.extraString;
        }

        @Override
        public String createKey(String id) {
            return "subclass_test_object_" + id;
        }

        // HAVE TO DO THIS IN ORDER TO OVERWRITE SUPERCLASS CACHE
        @Override
        public boolean save(ModelCache modelCache) {
            return super.save(modelCache) && super.save(modelCache, super.createKey(getId()));
        }

    }

    @Test
    public void testSubclassSavesOverSuperclass() {
        ModelCache modelCache = new ModelCache(2, 1, 1);
        String id = "123";

        TestObject testObject = new TestObject(id);
        testObject.setTestString("this is a test");
        testObject.save(modelCache);

        SubclassTestObject subclassTestObject = new SubclassTestObject(id);
        subclassTestObject.setTestString("yet another test");
        subclassTestObject.setExtraString("quite a few really");
        subclassTestObject.save(modelCache);

        testObject.reload(modelCache);
        assertEquals("yet another test", testObject.getTestString());
    }

    @Test
    public void testSuperclassDoesntSaveOverSubclass() {
        ModelCache modelCache = new ModelCache(2, 1, 1);
        String id = "123";

        SubclassTestObject subclassTestObject = new SubclassTestObject(id);
        subclassTestObject.setTestString("this is a test");
        subclassTestObject.setExtraString("yet another test");
        subclassTestObject.save(modelCache);

        TestObject testObject = new TestObject(id);
        testObject.setTestString("quite a few really");
        testObject.save(modelCache);

        subclassTestObject.reload(modelCache);
        assertEquals("this is a test", subclassTestObject.getTestString());
    }

}
