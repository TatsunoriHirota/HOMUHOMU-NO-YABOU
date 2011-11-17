#!/bin/sh
MY_DIRNAME=$(dirname $0)
cd $MY_DIRNAME

pwd

rm Game.class
javac Game.java -encoding UTF-8

if [ -e Game.class ]
then
jar -cfmv GameApp.jar META-INF/MAINIFEST.MF *.class gameCanvasUtil/*.class res/* gameCanvasUtil/Collision/*.class gameCanvasUtil/Resource/*.class
java -jar GameApp.jar
fi

