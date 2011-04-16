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
public class SuperclassCachedObjectTest extends TestBase {

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
        public boolean reloadFromCachedModel(ModelCache modelCache, CachedModel cachedModel) {
            TestObject cachedTestObject = (TestObject) cachedModel;
            testString = cachedTestObject.testString;
            return false;
        }

        @Override
        public String createKey(String id) {
            return "test_object_" + id;
        }

    }

    class SubclassTestObject extends TestObject {

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
        public boolean reloadFromCachedModel(ModelCache modelCache, CachedModel cachedModel) {
            super.reloadFromCachedModel(modelCache, cachedModel);
            SubclassTestObject cachedTestObject = (SubclassTestObject) cachedModel;
            extraString = cachedTestObject.extraString;
            return false;
        }

        @Override
        public void readFromParcel(Parcel source) throws IOException {
            super.readFromParcel(source);
            extraString = source.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(extraString);
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
