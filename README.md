# Introduction

It is a small Java engine allows to organize a hexagonal field and make some manipulations with it.

# Modules

## jhexed-engine

The Engine Core, it is implemented maximally platform independently and I guess it can be used for GWT and Android also. It is very small and allows to organize a hexagonal field and define own hexagonal field model and a render. It allows to use hexagons of two types:

### Horizontal hexes
![Map](https://github.com/raydac/jhexed/blob/master/files/horzhexesexample.png)

### Vertical hexes
![Map](https://github.com/raydac/jhexed/blob/master/files/verthexesexample.png)

## jhexed-swing

it contains some Swing-based components for graphic operations. it uses the Apache™ Batik SVG Toolkit to parse and render SVG images. [A Small example of usage the engine with Swing, you can find in wiki](https://github.com/raydac/jhexed/wiki/ExampleOfUsage)

## jhexed-swing-editor

An Implementation of Swing-based Hexagonal Map Editor. it allows to create and edit layered hexagonal maps. Prebuilt versions for Java and Windows can be downloaded from my Google drive folder.
![Screenshot](https://github.com/raydac/jhexed/blob/master/files/mapeditorscreen.png)

# How to use with Android

The Engine can be used with Android [and there is a small example of usage under Android 2.1](https://github.com/raydac/jhexed/tree/master/samples/android/JHexedPhotoView)
![Screenshot](https://github.com/raydac/jhexed/blob/master/files/android_screen.jpg)
