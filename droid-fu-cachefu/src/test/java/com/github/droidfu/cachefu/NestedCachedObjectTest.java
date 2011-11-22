package com.github.droidfu.cachefu;

import static org.junit.Assert.assertEquals;

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
public class NestedCachedObjectTest extends TestBase {

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

    public class OuterTestObject extends CachedModel {

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
        public boolean reloadFromCachedModel(ModelCache modelCache, CachedModel cachedModel) {
            OuterTestObject cachedOuterObject = (OuterTestObject) cachedModel;
            internalObject = cachedOuterObject.internalObject;
            // HAVE TO DO THIS IN ORDER TO RELOAD NESTED OBJECT FROM CACHE
            boolean internalObjectReloaded = false;
            if (internalObject != null) {
                internalObjectReloaded = internalObject.reload(modelCache);
            }
            return internalObjectReloaded;
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
