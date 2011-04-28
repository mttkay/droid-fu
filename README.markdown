# Just looking for the API docs?

You can find them [here](http://kaeppler.github.com/droid-fu).

---

# Droid-Fu - for your Android needs

Droid-Fu is an open-source effort aiming to collect and bundle solutions to common concerns in the development of applications for the [Google Android platform](http://developer.android.com/index.html). Currently it mostly contains code which I pulled out of [Qype's Android app](http://www.qype.co.uk/go-mobile), and which I believe may prove useful to other developers. This is _very_ much a work-in-progress, and both content and APIs are still in flux. There is not much documentation (yet), and I intend to change that. For now, you may want to read all related articles [on my weblog](http://en.wordpress.com/tag/droid-fu/). You can also find me on [Twitter](http://twitter.com/twoofour).

There are no releases yet, we pretty much work directly with the sources. If you want to stay up-to-date, we suggest you check out the project sources, pull frequently, and compile your app against them.

## What does it offer?

Droid-Fu offers both support classes meant to be used alongside existing Android code, as well as self-contained, ready-to-be-used components like new adapters and widgets.

The areas tackled by Droid-Fu include:

  * application life-cycle helpers
  * support classes for handling Intents and diagnostics
  * better support for background tasks
  * super-easy and robust HTTP messaging
  * powerful caching of Objects, HTTP responses, and remote images
  * custom adapters and views

I suggest you read [this introductory article](http://brainflush.wordpress.com/2009/11/16/introducing-droid-fu-for-android-betteractivity-betterservice-and-betterasynctask/), and anything that follows.

## How do I install it?

Droid-Fu is deployed as a JAR. Just drop it in your app's lib folder and add it to the classpath. Alternatively, you can of course simply compile against the sources.

### Getting the JAR

If you don't want to compile against the sources, then I'm afraid you'll have to roll the JAR yourself. It's a little elaborate, but don't run off scared.
Droid-Fu employs a managed build process, driven by the wonderful [Maven build system](http://maven.apache.org) system.
This means you have to install Git to get the sources, and Maven 3 to build them.
Droid-Fu is built against the latest Android APIs (it's backwards compatible down to 1.5 though), so you must have the proper Android JARs installed as Maven artifacts, too.

The following steps summarize how to do all that.

#### Step 1: Make sure you have the latest Android library JARs

You only need to perform this step when Google releases a new platform version of the Android library files, which means every now and then.
Update your SDK files to the latest version like so:

    $ android update sdk --no-ui
    
It's important that you repeat this command until you see the message "There is nothing to install or update." That's because the tools update themselves,
so it may take several iterations of this command until everything is fully updated.

#### Step 2: Install the Android JARs to your local Maven repository

Droid-Fu must be compiled against the android.jar and maps.jar library files. Since the build is driven by Maven, you must provide these JARs as artifacts to Maven during the compile stage, otherwise the build will fail.
We can do this with the [maven-android SDK deployer](http://github.com/mosabua/maven-android-sdk-deployer).

    $ git clone https://github.com/mosabua/maven-android-sdk-deployer.git
    $ cd maven-android-sdk-deployer
    $ mvn install

(requires `ANDROID_HOME` to point to your SDK home)

This will install all JAR files from `$ANDROID_HOME/platforms` and `$ANDROID_HOME/add-ons` as Maven artifacts.

#### Step 3: Getting the source code

This is simple:

    $ git clone git://github.com/kaeppler/droid-fu.git

Alternatively, you can simply download the archived source code from the master branch [here](http://github.com/kaeppler/droid-fu/archives/master).

#### Step 4: Build and install the Droid-Fu JAR

To build and install the Droid-Fu JAR as a Maven artifact, run this from the directory where your checked out the sources:

    $ mvn install

This will build the JAR and place it under the `target` directory (and also install it as a Maven artifact).
To make your life easier, I included a switch which lets you deploy the JAR directly to your application's lib folder:

    $ mvn install -DcopyTo=/path/to/your/apps/lib/folder

This will additionally copy it to the given folder.

If you want a JavaDoc JAR to get inline docs in Eclipse, do this:

    $ mvn javadoc:jar

This will create a JavaDoc JAR under `target`.

## How do I use it?

1.  If your Android app is Maven managed, declare a dependency to `com.github.droidfu:droid-fu:1.0-SNAPSHOT`. If not, just put the JAR in your libs/ folder and add it to your application's classpath.

1.  If you haven't yet created an [Application](file:///home/matthias/devel/frameworks/android-sdk/docs/reference/android/app/Application.html) class for your app, create a new class and let it inherit from `com.github.droidfu.DroidFuApplication`. Otherwise, just alter your app class to include said inheritance relation.

1.  If you had to create a new app class in the previous step, modify your `AndroidManifest.xml` and change the `application` element so that its `android:name` attribute points to the name of the new class, e.g.:

        <?xml version="1.0" encoding="utf-8"?>
        <manifest xmlns:android="http://schemas.android.com/apk/res/android" ...>
            <application android:name="MyApplication" ...>
                ...
            </application>
            ...
        </manifest>

## How is it licensed?

Droid-Fu is free and open source and may be used under the terms of the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

