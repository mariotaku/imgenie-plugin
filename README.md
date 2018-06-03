Say goodbye to manually updating drawable assets!

---

ImGenie is a gradle plugin for Android projects, it automatically converts many kind of images 
to drawable/mipmap resources.

## Usage

Add dependencies to your build.gradle:

````groovy
apply plugin: 'com.android.application'
// Add plugin
apply plugin: 'imgenie'

buildscript {
    repositories {
        // Add jitpack repo
        maven { url 'https://jitpack.io' }
    }

    dependencies {
        // Add plugin classpath
        classpath 'com.github.mariotaku:imgenie-plugin:0.0.5' 
    }
}
```` 

Place images to `src/*/images` just like how you put normal drawable files:

````text
src/main/images/
├── drawable
│   ├── bg_foo.svg
├── drawable-mdpi
│   ├── ic_action_foo.svg
├── mipmap-mdpi
│   ├── ic_launcher.pdf
└── mipmap-xxhdpi
    └── bg_bar.png
````

...aaaand that's done!

## Features

* Generate drawables from svg/pdf/png/jpg images
* Override output format by filename: `bg_output_to_jpeg.jpg.svg`

## Caveats

* If image placed in folders without density configuration e.g. `drawable/`, no other densities 
will be generated.
* You may need to perform `clear` task if you deleted image files.  