A* Test
=======

![screenshot](http://github.com/nickpenaranda/astar-test/raw/master/screenshot.jpg)

This is a barebones test/demonstration of the A* pathfinding algorithm, as described by Patrick Lester [here](http://www.polyalmanac.org/games/aStarTutorial.html).

Written in Java using Slick/LWJGL:

* [Slick](http://slick.cokeandcode.com)
* [LWJGL](http://lwjgl.org)

Copyright (c) 2011 Nick Penaranda.
Distributed under the MIT License.

Instructions
------------

* Press *SPACEBAR* to generate a new map and randomize start/end locations
* *Left click* to place a wall and *right click* to remove a wall.  Adding and removing walls will automatically recalculate the path.

* Gray blocks are locations that the algorithm attempted, providing a rough indication of the amount of difficulty it had trying to produce the path.
