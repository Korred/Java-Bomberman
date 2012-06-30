package ai;

import game.Bomb;
import game.GameData;
import game.Player;

import java.awt.Point;
import java.util.LinkedList;

public class Pathfinding {

    public int               w, h;
    public boolean           dangerous[][];
    public boolean           blocked[][];
    public int               distance[][];
    public Point             previous[][];
    public int               dx[]      = { 0, 1, 0, -1 };
    public int               dy[]      = { -1, 0, 1, 0 };
    public static final int  UNVISITED = -1;
    public LinkedList<Point> reachable;

    public Pathfinding() {
        w = GameData.map.getWidth();
        h = GameData.map.getHeight();
        dangerous = new boolean[w][h];
        distance = new int[w][h];
        previous = new Point[w][h];
        blocked = new boolean[w][h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                dangerous[x][y] = false;
                blocked[x][y] = GameData.map.isBlocked(x, y);
                distance[x][y] = UNVISITED;
                previous[x][y] = null;
            }
        }
        reachable = new LinkedList<Point>();
        for (Bomb bomb : GameData.bombs)
            if (!bomb.isExpired()) markDangerous(bomb.x, bomb.y, bomb.getRadius());
    }

    private void explore(Point p, int direction) {
        int x = p.x + dx[direction];
        int y = p.y + dy[direction];
        int d = distance[x][y];
        if (contains(x, y) && !blocked[x][y] && d == UNVISITED) {
            // If field is dangerous and we can not run away from it
            // if (dangerous[x][y] && d > 2) return;
            previous[x][y] = p;
            distance[x][y] = distance[p.x][p.y] + 1;
            reachable.addLast(new Point(x, y));
        }
    }

    public void start(int x, int y) {
        previous[x][y] = null;
        distance[x][y] = 0;
        reachable.push(new Point(x, y));
        while (!reachable.isEmpty()) {
            Point p = reachable.pollFirst();
            for (int i = 0; i < 4; i++)
                explore(p, i);
        }
    }

    public boolean contains(int x, int y) {
        return x >= 0 && y >= 0 && x < w && y < h;
    }

    public void markDangerous(int x, int y, int radius) {
        if (contains(x, y)) dangerous[x][y] = true;
        for (int i = 0; i < 4; i++) {
            int x2 = x;
            int y2 = y;
            for (int r = 1; r <= radius; r++) {
                x2 += dx[i];
                y2 += dy[i];
                if (contains(x2, y2)) dangerous[x2][y2] = true;
            }
        }
    }

    public Point getClosestReachableNicePoint(int px, int py) {
        Point p = null;
        int dist = Integer.MAX_VALUE;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int d = distance[x][y];
                if (d != UNVISITED) {
                    if ((x != px || y != py) && d < dist) {
                        p = new Point(x, y);
                        dist = d;
                    }
                }
            }
        }
        return p;
    }

    public Player getClosestReachablePlayer(Player thisPlayer) {
        Player closest = null;
        int dist = Integer.MAX_VALUE;
        for (Player player : GameData.players) {
            int d = distance[player.x][player.y];
            if (player.isAlive() && player != thisPlayer && d != UNVISITED && d < dist) {
                closest = player;
                dist = d;
            }
        }
        return closest;
    }

    public Point getClosestBox() {
        Point p = null;
        int dist = Integer.MAX_VALUE;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (GameData.map.getField(x, y).getStrength() > 0) {
                    for (int i = 0; i < 4; i++) {
                        int x2 = x + dx[i];
                        int y2 = y + dy[i];
                        int d = distance[x2][y2];
                        if (d != UNVISITED && d < dist) {
                            p = new Point(x2, y2);
                            d = dist;
                        }
                    }
                }
            }
        }
        return p;
    }

    public Point getPointTowards(Point target, Point source) {
        Point prev = source;
        while (target.x != source.x || target.y != source.y) {
            prev = target;
            target = previous[target.x][target.y];
        }
        return prev;
    }
}
