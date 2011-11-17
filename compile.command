#!/bin/sh
MY_DIRNAME=$(dirname $0)
cd $MY_DIRNAME

pwd

rm gameCanvasUtil/Collision/*.class
rm gameCanvasUtil/Resource/*.class
rm gameCanvasUtil/*.class
rm AppCanvas.class
rm GameApp.class
rm Game.class

javac gameCanvasUtil/Collision/*.java -encoding UTF-8
javac gameCanvasUtil/Resource/*.java -encoding UTF-8
javac gameCanvasUtil/*.java -encoding UTF-8
javac AppCanvas.java -encoding UTF-8
javac GameApp.java -encoding UTF-8
javac Game.java -encoding UTF-8

if [ -e Game.class ]
then
jar -cfmv GameApp.jar META-INF/MAINIFEST.MF *.class gameCanvasUtil/*.class res/* gameCanvasUtil/Collision/*.class gameCanvasUtil/Resource/*.class
java -jar GameApp.jar
fi

