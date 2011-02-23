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
public class NestedCachedObjectTest extends TestBase {

    class TestObject extends CachedObject {

        private static final long serialVersionUID = -4264329112200722511L;

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

    public class OuterTestObject extends CachedObject {

        private static final long serialVersionUID = -4127153536940947286L;

        private TestObject internalObject;

        public OuterTestObject() {
            super();
        }

        public OuterTestObject(String id) {
            super(id);
        }

        public TestObject getInternalObject() {
            return internalObject;
        }

        public void setInternalObject(TestObject internalObject) {
            this.internalObject = internalObject;
        }

        @Override
        public void reloadFromCachedObject(ModelCache modelCache, CachedObject cachedObject) {
            OuterTestObject cachedOuterObject = (OuterTestObject) cachedObject;
            internalObject = cachedOuterObject.internalObject;
            // HAVE TO DO THIS IN ORDER TO RELOAD NESTED OBJECT FROM CACHE
            if (internalObject == null) {
                internalObject.reload(modelCache);
            }
        }

        @Override
        public String createKey(String id) {
            return "outer_test_object_" + id;
        }

    }

    @Test
    public void testInternalObjectReloads() {
        ModelCache modelCache = new ModelCache(1, 1, 1);
        String id = "123";

        TestObject internalTestObject = new TestObject(id);
        internalTestObject.setTestString("this is a test");
        internalTestObject.save(modelCache);

        OuterTestObject outerTestObject = new OuterTestObject(id);
        outerTestObject.setInternalObject(internalTestObject);
        outerTestObject.save(modelCache);

        TestObject overridingTestObject = new TestObject(id);
        overridingTestObject.setTestString("yet another test");
        overridingTestObject.save(modelCache);

        internalTestObject.reload(modelCache);
        assertEquals("yet another test", outerTestObject.getInternalObject().getTestString());
    }

}
