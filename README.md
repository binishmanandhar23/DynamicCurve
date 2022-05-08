# DynamicCurve

[ ![Download](https://api.bintray.com/packages/binishmanandhar23/DynamicCurve/com.binish.dynamiccurve/images/download.svg?version=1.1.0) ](https://bintray.com/binishmanandhar23/DynamicCurve/com.binish.dynamiccurve/1.1.0/link) ![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)

Making Curves has never been easier.

# Installation

Add the following dependencies in the gradle file of your app module to get started:

Gradle
```kotlin
implementation 'io.github.binishmanandhar23.dynamiccurve:dynamiccurve:2.0.0'
```
Maven
```xml
<dependency>
  <groupId>io.github.binishmanandhar23.dynamiccurve</groupId>
  <artifactId>dynamiccurve</artifactId>
  <version>2.0.0</version>
  <type>pom</type>
</dependency>
```

or if you want to further customize the module, simply import it.

## Note
If there are any confusions just clone github repository for proper use cases & to get the example app shown in the gifs below.


# Setting up the view

## Compose
Initialization

```kotlin
DynamicCurveCompose.Curve(
    modifier = Modifier.fillMaxSize(),
    curveValues = CurveValues(x0 = 0f, x1 = 1f, x2 = 2f, x3InString = "width", y0 = 3f, y1 = 4f, y2 = 5f, y3 = 6f),
    curvePropertiesMain = CurveProperties(paintColor = R.color.color_orange)
)
```

##XML
There are multiple ways to set up the curve. One way is to directly add values in xml

```xml
<com.binish.dynamiccurve.DynamicCurve
        android:id="@+id/dynamicCurve"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:curveColor="@color/color_orange"
        app:x0="0"
        app:x1="2.8"
        app:x2="6.5"
        app:x3="width"
        app:y0="4.9"
        app:y1="3.5"
        app:y2="1.9"
        app:y3="8.3"/>
```

This results in:  
![](https://i.imgur.com/m1TYwCu.png)

Or,  
We can create a curve programmatically:  
First add
```xml
<com.binish.dynamiccurve.DynamicCurve
        android:id="@+id/dynamicCurve"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```
Then,
```kotlin
dynamicCurve.changeValues(XYControls.X0, "0")
dynamicCurve.changeValues(XYControls.X1, "2.8")
dynamicCurve.changeValues(XYControls.X2, "6.5")
dynamicCurve.changeValues(XYControls.X3, "width")
dynamicCurve.changeValues(XYControls.Y0, "4.9")
dynamicCurve.changeValues(XYControls.Y1, "3.5")
dynamicCurve.changeValues(XYControls.Y2, "1.9")
dynamicCurve.changeValues(XYControls.Y3, "8.3")
//For second curve..
/*
dynamicCurve.changeValues(XYControls.X1a, "[value in string]")
dynamicCurve.changeValues(XYControls.X2a, "[value in string]")
dynamicCurve.changeValues(XYControls.X3a, "[value in string]")
dynamicCurve.changeValues(XYControls.Y1a, "[value in string]")
dynamicCurve.changeValues(XYControls.Y2a, "[value in string]")
dynamicCurve.changeValues(XYControls.Y3a, "[value in string]")
*/
```
if only first curves are to be added
```kotlin
val x0 = "0"
val x1 = "2.8"
val x2 = "6.5"
val x3 = "width"
val y0 = "4.9"
val y1 = "3.5"
val y2 = "1.9"
val y3 = "8.3"
dynamicCurve.changeValues(x0,y0,x1,y1,x2,y2,x3,y3)
```

![](https://i.imgur.com/RfEurNM.gif)


# Retrieving values
To retrieve values of any of the co-ordinate
```kotlin
dynamicCurve.getValue(XYControls.X0) //This retrieves the x0 co-ordinate's value.
//And so on...
```

# Advance controls
## Mirroring
(Note) For now, Mirroring doesn't work when second curve is enabled  
![](https://i.imgur.com/9bUGLmF.gif)

```bash
app:mirror="true"
```
or,
```kotlin
dynamicCurve.isMirrored(true) // By default false
```

![](https://i.imgur.com/731OYvD.gif)

## Reversing
```bash
app:reverse="true
```
or,
```kotlin
dynamicCurve.isReversed(true) // By default false
```

## Inverting
```bash
app:upsideDown="true"
```
or,
```kotlin
dynamicCurve.isInverted(true) // By default false
```

## Decrease height of the curve
```bash
app:decreaseHeightBy="[float value]"
```
or,
```kotlin
dynamicCurve.decreaseHeightBy("1.0") // If done programmatically the value must be in strings
```

## Change curve color
```bash
app:curveColor="@color/black"
```
or,
```kotlin
dynamicCurve.changeCurveColor(ContextCompat.getColor(requireContext(), R.color.black))
```

## Change background color
```kotlin
dynamicCurve.changeBackgroundColor(R.color.white)
```

## Enable shadow for the curve and change it's radius, dx value & dy value
```kotlin
dynamicCurve.isShadow(true) //By default false
dynamicCurve.setCurveShadowRadius(2f) // By default 2f
dynamicCurve.setCurveShadowDx(1f) // ByDefault 1f
dynamicCurve.setCurveShadowDy(1f) // ByDefault 1f
```


# Callbacks
There are two callbacks which can be useful to track reverse and/or second curve addition changes
```kotlin
val mainListener = object : DynamicCurve.DynamicCurveAdapter(){
                    override fun isHalfWidth(halfWidth: Boolean) {
                        
                    }

                    override fun isReversed(reversed: Boolean) {
                        
                    }
                }
```

# (Important) Adding a second curve
![](https://i.imgur.com/v2Myw2K.gif)

To add a second curve we need to change the value of x3:
```kotlin
dynamicCurve.changeValues(XYControls.X3,if (secondCurveAdded) X3Type.HALF.type else X3Type.FULL.type)
```
## Contributions

If you want to contribute or just wanna say Hi!, you can find me at:
1. [LinkedIn](https://www.linkedin.com/in/binish-manandhar-3136621b2/)
2. [Facebook](https://www.facebook.com/binish.manandhar)
3. [Twitter](https://twitter.com/NotBinish)





