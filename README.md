[![License Apache 2.0](https://img.shields.io/badge/license-Apache%20License%202.0-green.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Java 6.0+](https://img.shields.io/badge/java-6.0%2b-green.svg)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![PayPal donation](https://img.shields.io/badge/donation-PayPal-red.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=AHWJHJFBAWGL2)
[![Yandex.Money donation](https://img.shields.io/badge/donation-Я.деньги-yellow.svg)](http://yasobe.ru/na/iamoss)

# Introduction

It is a small Java engine allows to organize a hexagonal field and make some manipulations with it.

# License
The Framework is under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

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

An Implementation of Swing-based Hexagonal Map Editor. it allows to create and edit layered hexagonal maps. Prebuilt versions for Java and Windows can be downloaded from [my Google drive folder](https://drive.google.com/folderview?id=0BxHnNp97IgMRcERvNTI4SjZJN1k&usp=drive_web).
![Screenshot](https://github.com/raydac/jhexed/blob/master/files/mapeditorscreen.png)

# How to use with Android   
The Engine can be used with Android [and there is a small example of usage under Android 2.1](https://github.com/raydac/jhexed/tree/master/samples/android/JHexedPhotoView)
![Screenshot](https://github.com/raydac/jhexed/blob/master/files/android_screen.jpg)
