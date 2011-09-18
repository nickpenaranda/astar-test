
package org.n4p.astartest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/**
 * @author Nick Penaranda
 * A* Test
 * Based on the tutorial by Patrick Lester:
 * http://www.policyalmanac.org/games/aStarTutorial.htm
 * 
 * This source code is distributed under the MIT license.
 *  
 * Copyright (c) 2011 Nick Penaranda
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public class AStarTest extends BasicGame {
  ArrayList<Node> open = new ArrayList<Node>();
  ArrayList<Node> path = new ArrayList<Node>();

  Random r = new Random();

  static final int winX = 640, winY = 480;
  
  static final int gSize = 60;

  static final int size = Math.min(winX, winY) / gSize;

  int grid[][] = new int[gSize][gSize];

  int sx, sy, ex, ey; // Start x,y; End x,y

  static final Color[] colors = new Color[] { Color.darkGray, Color.green,
      Color.red, Color.blue, Color.gray, Color.cyan, Color.orange };
  static final int NONE = 0;
  static final int START = 1;
  static final int END = 2;
  static final int WALL = 3;
  static final int CLOSED = 4;
  static final int PATH = 5;

  int mButton;

  public AStarTest() {
    super("A* Test");
  }

  public void generate() {
    for (int x = 0; x < gSize; ++x) {
      for (int y = 0; y < gSize; ++y) {
        grid[x][y] = 0;
      }
    }

    for (int n = 0; n < gSize / 2; ++n) {
      int i = 0, l = r.nextInt(gSize / 4);
      int x = r.nextInt(gSize);
      int y = r.nextInt(gSize);
      if (r.nextBoolean()) { // Horizontal
        do {
          grid[x++][y] = WALL;
          ++i;
        } while (x < gSize);
      } else { // Vertical
        do {
          grid[x][y++] = WALL;
          ++i;
        } while (y < gSize && i < l);
      }
    }

    sx = r.nextInt(gSize);
    sy = r.nextInt(gSize);
    ex = r.nextInt(gSize);
    ey = r.nextInt(gSize);
    grid[sx][sy] = START;
    grid[ex][ey] = END;

    findPath();
  }

  public void findPath() {
    boolean pathFound = false;
    Node current = null;
    path.clear();
    int attempts = 0;

    open.clear();
    open.add(new Node(sx, sy, null, 0));

    // Clear closed
    for (int x = 0; x < gSize; ++x) {
      for (int y = 0; y < gSize; ++y) {
        if (grid[x][y] == CLOSED)
          grid[x][y] = NONE;
      }
    }

    while (!pathFound && attempts < 1000) { // Arbitrary limit, usually ends due
                                            // to conditional below
      Collections.sort(open);

      if (open.isEmpty()) // No more squares to try--"No path available"
        break;

      current = open.get(0);

      if (current.getX() == ex && current.getY() == ey) {
        pathFound = true;
        break;
      }

      grid[current.getX()][current.getY()] = CLOSED;
      open.remove(current);

      checkAdjacent(current, -1, -1);
      checkAdjacent(current, 0, -1);
      checkAdjacent(current, 1, -1);
      checkAdjacent(current, -1, 0);
      checkAdjacent(current, 1, 0);
      checkAdjacent(current, -1, 1);
      checkAdjacent(current, 0, 1);
      checkAdjacent(current, 1, 1);
    }

    if (pathFound) {
      do {
        path.add(current);
        current = current.mParent;
      } while (current.mParent != null); // Start
    }
  }

  public void checkAdjacent(Node n, int dx, int dy) {
    int tx = n.getX() + dx;
    int ty = n.getY() + dy;
    int cost = (int) (Math.sqrt(dx * dx + dy * dy) * 100);
    // Overkill, but will be easier to generalize to 3D

    if (tx < gSize && tx >= 0 && ty < gSize && ty >= 0) { // Bound check
      int t = grid[tx][ty];
      if (t == CLOSED || t == WALL)
        return;
      else {
        Node m = new Node(tx, ty, n, cost);
        if (!open.contains(m)) {
          open.add(m);
        } else {
          Node o = open.get(open.indexOf(m));
          if (o.projG(n, cost) < o.getG()) {
            o.setParent(n, cost);
            Collections.sort(open);
          }
        }
      }
    }
  }

  @Override
  public void init(GameContainer arg0) throws SlickException {
    generate();
  }

  @Override
  public void update(GameContainer arg0, int arg1) throws SlickException {
    // TODO Auto-generated method stub
  }

  @Override
  public void render(GameContainer c, Graphics g) throws SlickException {
    for (int x = 0; x < gSize; x++) {
      for (int y = 0; y < gSize; y++) {
        int t = grid[x][y];
        g.setColor(colors[t]);
        if (t > 0)
          g.fillRect(x * size + 1, y * size + 1, size - 1, size - 1);
      }
    }
    g.setColor(colors[START]);
    g.fillRect(sx * size + 1, sy * size + 1, size - 1, size - 1);

    for (Node n : path) {
      g.setColor(colors[PATH]);
      g.fillOval(n.x * size + 2, n.y * size + 2, size - 3, size - 3);
    }
  }

  @Override
  public void mousePressed(int button, int x, int y) {
    int gx = x / size;
    int gy = y / size;
    switch (button) {
    case 0:
      grid[gx][gy] = WALL;
      break;
    case 1:
      grid[gx][gy] = NONE;
      break;
    }
    mButton = button;
    findPath();
  }

  @Override
  public void mouseDragged(int oldx, int oldy, int newx, int newy) {
    mousePressed(mButton, newx, newy);
  }

  @Override
  public void keyPressed(int key, char c) {
    if (key == Input.KEY_SPACE)
      generate();
    else if (key == Input.KEY_ESCAPE)
      System.exit(-1);
  }

  private class Node implements Comparable<Node> {
    int x, y;

    int F, G, H; // F = G + H

    private Node mParent = null;
    int costToParent;

    public Node(int x, int y, Node parent, int costToParent) {
      this.x = x;
      this.y = y;
      this.H = calcH();
      setParent(parent, costToParent);
      this.mParent = parent;
      this.costToParent = costToParent;
      // grid[x][y] = OPEN;
      // This clutters the display quite a bit, but may reveal some behavior
    }

    public void setParent(Node parent, int costToParent) {
      this.mParent = parent;
      this.costToParent = costToParent;
      G = calcG();
      F = G + H;
    }

    // Calculate G given this node's actual parent and associated cost
    public int calcG() {
      int g = 0;
      Node n = this;
      while (n.x != sx || n.y != sy) {
        g += n.costToParent;
        n = n.mParent;
      }
      return (g);
    }

    // Calculate a "projected" G if this node's parent were changed to
    // <p> with cost <costToParent>
    public int projG(Node p, int costToParent) {
      int g = 0;
      Node n = this;
      while (n.x != sx || n.y != sy) {
        if (n == this) {
          g += costToParent;
          n = p;
        } else {
          g += n.costToParent;
          n = n.mParent;
        }
      }
      return (g);
    }

    // Calculate H score
    public int calcH() {
      return ((Math.abs(this.x - ex) + Math.abs(this.y - ey)) * 100); // "Manhattan"
                                                                      // heuristic

      // return (int)(Math.sqrt((this.x - ex) * (this.x - ex) + (this.y - ey) *
      // (this.y - ey)) * 100); // Euclidean distance
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

    public int getG() {
      return G;
    }

    @Override
    public int compareTo(Node o) {
      // TODO Auto-generated method stub
      return (this.F - o.F);
    }

    @Override
    public boolean equals(Object aThat) {
      if (this == aThat)
        return true;
      if (!(aThat instanceof Node))
        return false;
      Node that = (Node) aThat;
      return (this.x == that.x && this.y == that.y);
    }

    @Override
    public String toString() {
      return (String.format("Node @ (%d,%d) F = %d; G = %d", x, y, F, G));
    }
  }

  public static void main(String[] args) {
    try {
      AppGameContainer appGameContainer = new AppGameContainer(new AStarTest());
      appGameContainer.setDisplayMode(640, 480, false);
      appGameContainer.start();
    } catch (SlickException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
