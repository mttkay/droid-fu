# Just looking for the API docs?

You can find them [here](http://kaeppler.github.com/droid-fu).

---

# Droid-Fu - for your Android needs

Droid-Fu is an open-source effort aiming to collect and bundle solutions to common concerns in the development of applications for the [Google Android platform](http://developer.android.com/index.html). Currently it mostly contains code which I pulled out of [Qype Radar](http://www.qype.co.uk/go-mobile), and which I believe may prove useful to other developers. This is _very_ much a work-in-progress, and both content and APIs are still in flux. There is not much documentation (yet), and I intend to change that. For now, you may want to read all related articles [on my weblog](http://en.wordpress.com/tag/droid-fu/). You can also find me on [Twitter](http://twitter.com/twoofour).

## What does it offer?

Droid-Fu offers both support classes meant to be used alongside existing Android code, as well as self-contained, ready-to-be-used components like new adapters and widgets.

The areas tackled by Droid-Fu include:

  * application life-cycle helpers
  * support classes for handling Intents and diagnostics
  * better support for background tasks
  * super-easy and robust HTTP messaging
  * (remote) image handling and caching
  * custom adapters and views

I suggest you read [this introductory article](http://brainflush.wordpress.com/2009/11/16/introducing-droid-fu-for-android-betteractivity-betterservice-and-betterasynctask/), and anything that follows.

## How do I install it?

Droid-Fu is deployed as a JAR. Just drop it in your app's lib folder and add it to the classpath.

### Getting the JAR

You can get the Droid-Fu JAR in several ways.

The easiest one is probably to download a pre-built version from [GitHub](http://github.com/kaeppler/droid-fu/downloads). Just know that these builds may not contain the most recent changes you see in the master branch.

If you want to stay on the bleeding edge, you must download the sources and roll the JAR yourself. It's a little elaborate, but don't run off scared. Just follow these steps and you'll be fine. Droid-Fu employs a managed build process, driven by the wonderful [Maven build system](http://maven.apache.org) system. This means you have to install both Maven (v2.2.1 or newer), and the [maven-android SDK deployer](http://github.com/mosabua/maven-android-sdk-deployer). Droid-Fu is currently built against the Android 1.5 R4 APIs, so you must have the proper Android JAR installed, too. Consult the Android SDK docs to learn about how to download and install different Android platform versions using the AVD/SDK Manager bundled with the ADT.

#### Step 1: Getting the source codes

If you're using [git](http://www.git-scm.com), do a 

    git clone git://github.com/kaeppler/droid-fu.git

and

    git clone git://github.com/mosabua/maven-android-sdk-deployer.git

now.

Alternatively, you can simply download the archived source codes from the master branches, [here](http://github.com/kaeppler/droid-fu/archives/master) and [here](http://github.com/mosabua/maven-android-sdk-deployer/archives/master).

#### Step 2: Install the Android JAR to your local Maven repository

Droid-Fu must be compiled against the Android 1.5 R4 JAR. Since the build is driven by Maven, we must provide the Android JAR to Maven during the compile stage, otherwise the build will fail. Change to the folder to which you downloaded the maven-android SDK deployer source code, and into the platforms/android-3 sub-directory, e.g.:

    $ cd ~/projects/maven-android-sdk-deployer/platforms/android-3

Then install the Android JAR:

    $ mvn install -Dandroid.sdk.path=/path/to/your/android/sdk/root

If the build fails, you probably provided a wrong SDK root. An `ls` in the `android.sdk.path` should list (among other files) a `platforms` and `add-ons` folder.

#### Step 3: Build and install the Droid-Fu JAR

If you just want to build the JAR, and copy it around manually, change to the folder where you downloaded/cloned the Droid-Fu sources, and run:

    $ mvn package

This will build the JAR and place it under the `target` directory. To make your life easier, I included a switch which lets you deploy the JAR directly to your application's lib folder:

    $ mvn install -DcopyTo=/path/to/your/apps/lib/folder

This will build the JAR and copy it to the given folder.

If you want a JavaDoc JAR to get inline docs in Eclipse, do this:

    $ mvn javadoc:jar

This will create a JavaDoc JAR under `target`.

## How do I use it?

1.  Link the JAR to your application's classpath, as you would with any other JAR in any other Java or Android project.

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

