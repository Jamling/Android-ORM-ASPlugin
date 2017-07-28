## Introduction

Android-ORM-ASPlugin is an [Android ORM] Tool plugin for Android Studio or Intellij IDEA, it can be used to generate ORM annotation and ContentProvider.

Eclipse plugin please see [adt-extensions]

## Screenshot

![screenshot](https://raw.githubusercontent.com/Jamling/Android-ORM-ASPlugin/master/screenshot.gif)

## Install

1. **File** -> **Settings** -> **Plugins** -> **Browse repositories...** to open plugin repo.
2. Input <kbd>Android ORM Tool</kbd> to search
3. Install and restart to enable the plugin.

## Usage

### Add/Edit Annotation

#### In project view.

 1. Choose the your java files want to mapping
 2. Right click to popup the context menu
 3. Click **Android ORM** -> **Add Annotation**
 
#### In editor

 1. Right click to popup the context menu
 2. Click **Generate** -> **Add annotation** to open the dialog
 3. Edit the annotation property for each field
 4. Click `OK` to add or edit the annotation.
 
 *The hotkey `Alt + O` can be used to open the add annotation dialog quickly.*

### New provider

1. Right click the java package/directory which your want to create the class under it.
2. Click **Android ORM** -> **New provider** to open the dialog
3. Fill the class name, db name and add or unselect the class
4. Click `OK` to create the provider class.

[adt-extensions]: https://github.com/Jamling/adt-extensions
[Android ORM]: https://github.com/Jamling/Android-ORM
