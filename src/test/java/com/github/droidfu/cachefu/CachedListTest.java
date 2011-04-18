package com.github.droidfu.cachefu;

import static org.junit.Assert.assertEquals;
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
public class CachedListTest extends TestBase {

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

        CachedList<TestObject> preList = new CachedList<TestObject>(TestObject.class, id);
        TestObject preObject;
        preObject = new TestObject();
        preObject.setTestString("this is a test");
        preList.add(preObject);
        preObject = new TestObject();
        preObject.setTestString("this is also a test");
        preList.add(preObject);
        assertTrue(preList.save(modelCache));

        @SuppressWarnings("unchecked")
        CachedList<TestObject> postList = (CachedList<TestObject>) modelCache.get(preList.getKey());
        assertEquals("this is a test", postList.getList().get(0).getTestString());
        assertEquals("this is also a test", postList.getList().get(1).getTestString());
    }

    @Test
    public void testReloadUpdatesObjectWithCachedData() {
        ModelCache modelCache = new ModelCache(1, 1, 1);
        String id = "123";

        CachedList<CachedModel> originalList = new CachedList<CachedModel>(TestObject.class, id);
        TestObject originalListObject;
        originalListObject = new TestObject();
        originalListObject.setTestString("original text");
        originalList.add(originalListObject);
        originalListObject = new TestObject();
        originalListObject.setTestString("original other text");
        originalList.add(originalListObject);
        originalList.save(modelCache);

        CachedList<CachedModel> overridingList = new CachedList<CachedModel>(TestObject.class, id);
        TestObject overridingListObject = new TestObject();
        overridingListObject.setTestString("new text");
        overridingList.add(overridingListObject);
        overridingList.save(modelCache);

        assertTrue(originalList.reload(modelCache));
        assertEquals(1, originalList.getList().size());
        assertEquals("new text", ((TestObject) originalList.getList().get(0)).getTestString());
    }

    @Test
    public void testReloadAllUpdatesListObjects() {
        ModelCache modelCache = new ModelCache(2, 1, 1);
        String id = "123";

        CachedList<CachedModel> originalList = new CachedList<CachedModel>(TestObject.class, id);
        TestObject originalListObject;
        originalListObject = new TestObject();
        originalListObject.setTestString("original text");
        originalList.add(originalListObject);
        originalListObject = new TestObject(id);
        originalListObject.setTestString("original other text");
        originalListObject.save(modelCache);
        originalList.add(originalListObject);
        originalList.save(modelCache);

        TestObject overridingListObject = new TestObject(id);
        overridingListObject.setTestString("new text");
        overridingListObject.save(modelCache);

        assertTrue(originalList.reload(modelCache));
        assertEquals(2, originalList.getList().size());
        assertEquals("original text", ((TestObject) originalList.getList().get(0)).getTestString());
        assertEquals("new text", ((TestObject) originalList.getList().get(1)).getTestString());
    }

}
