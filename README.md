# DynamicCurve

[ ![Download](https://api.bintray.com/packages/binishmanandhar23/PhotoEditorX/com.binish.photoeditorx/images/download.svg?version=1.0.2) ](https://bintray.com/binishmanandhar23/PhotoEditorX/com.binish.photoeditorx/1.0.2/link) ![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)

Helps make curves easily for your app

## Installation

Add the following dependencies in the gradle file of your app module to get started:

Gradle
```kotlin
implementation 'com.binish.dynamiccurve:dynamiccurve:1.0.0'
```
Maven
```xml
<dependency>
  <groupId>com.binish.dynamiccurve</groupId>
  <artifactId>dynamiccurve</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

or if you want to further customize the module, simply import it.

## Setting up the view

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
        app:y3="8.3"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintVertical_bias="0"/>
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
//and so on if second curve is needed..
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


## Retrieving values
To retrieve values of any of the co-ordinate
```kotlin
dynamicCurve.getValue(XYControls.X0) //This retrieves the x0 co-ordinate's value.
//And so on...
```

## Advance controls
# Mirroring
```bash
app:mirror="true"
```
or,
```kotlin
dynamicCurve.isMirrored(true) // By default false
```
# Reversing
```bash
app:reverse="true
```
or,
```kotlin
dynamicCurve.isReversed(true) // By deafault false
```

# Inverting
```bash
app:upsideDown="true"
```
or,
```kotlin
dynamicCurve.isInverted(true) // By default false
```

# Decrease height of the curve
```bash
app:decreaseHeightBy="[float value]"
```
or,
```kotlin
dynamicCurve.decreaseHeightBy("1.0") // If done programmatically the value must be in strings
```

## Callbacks
There are two callbacks which can be useful to track reverse and/or second curve addition changes
```kotlin
val mainListener = object : DynamicCurve.DynamicCurveAdapter(){
                    override fun isHalfWidth(halfWidth: Boolean) {
                        
                    }

                    override fun isReversed(reversed: Boolean) {
                        
                    }
                }
```

## Adding a second curve
To add a second curve we need to change the value of x3:
```kotlin
dynamicCurve.changeValues(XYControls.X3,if (secondCurveAdded) X3Type.HALF.type else X3Type.FULL.type)
```





