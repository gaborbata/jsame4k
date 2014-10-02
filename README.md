JSame4k
=======

Overview
--------
JSame4k is an entry for the 2010 Java 4K Game Programming Contest. The goal of the contest is to develop the best game possible within four kilobytes (4096 bytes) of data.

JSame4k is a puzzle game (inspired by [SameGame](http://en.wikipedia.org/wiki/SameGame)), which is played on a rectangular field, filled with coloured squares. The objective of the game is to clear the field by removing groups of pieces of the same colour. You get points for each group you remove and the larger the group, the more points you get. To get the highest score you need to make large blocks of one colour.

![JSame4k](https://raw.githubusercontent.com/gaborbata/jsame4k/master/resources/jsame4k-screenshot.png)

Game rules
----------
Pieces can be removed when there is a block of at least two pieces of the same colour. These pieces will be marked when you move the mouse over them, then they can be removed simply by clicking them with the mouse. After a block is removed, the pieces above it drop down to fill the empty space. When a column is empty, all columns right of it are shifted to the left. When there are no more blocks of two or more pieces left, the game is over.

The points of a marked block of pieces are calculated by the formula `(n - 2) ^ 2`, where `n` is the number of pieces. So try to remove as many pieces at a time as possible to get a higher score. When there are no pieces left at the end of the game, you'll get a 1000 points bonus.

How to compile
--------------
Use `mvn clean package` or `gradle clean build` which do the following:

* compile sources with `javac -target 1.5 S.java`
* create jar files:
    * application: `jar cvfe jsame4k-app.jar S *.class`
    * applet: `jar cvf jsame4k-applet.jar *.class`
* optimize/obfuscate classes with [ProGuard](http://proguard.sourceforge.net/)

Usage
-----
Java 5 or later is recommended to run the game.

* Application: Most platforms have a mechanism to execute `.jar` files (e.g. double click the `jsame4k-app-1.0.0.jar`).
  You can also run the game from the command line by typing:

        java -jar jsame4k-app-1.0.0.jar

* Applet: Open `jsame4k-applet-1.0.0.html` in a web browser which supports Java applets.

HTML5 port
----------
An experimental HTML5/JavaScript port has also been created of the game (using the the `canvas` element).

It can be found in the `jsame-html5` folder.

License
-------
Copyright (c) 2009-2010 Gabor Bata

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
