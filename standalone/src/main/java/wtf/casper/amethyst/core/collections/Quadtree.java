package wtf.casper.amethyst.core.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Quadtree<T> {
    public static class Point2D {
        double x, y;

        public Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            Point2D point2D = (Point2D) object;
            return Double.compare(x, point2D.x) == 0 && Double.compare(y, point2D.y) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    public static class BoundingBox {
        Point2D center;
        double halfSize;

        public BoundingBox(Point2D center, double halfSize) {
            this.center = center;
            this.halfSize = halfSize;
        }

        public boolean contains(Point2D point) {
            return Math.abs(point.x - center.x) <= halfSize &&
                    Math.abs(point.y - center.y) <= halfSize;
        }

        public boolean intersects(BoundingBox other) {
            return Math.abs(center.x - other.center.x) <= halfSize + other.halfSize &&
                    Math.abs(center.y - other.center.y) <= halfSize + other.halfSize;
        }
    }

    private static final int MAX_CAPACITY = 4;
    private BoundingBox boundary;
    private List<Entry> points;

    private Quadtree<T>[] children;
    private boolean isDivided;

    private class Entry {
        Point2D position;
        T data;

        Entry(Point2D position, T data) {
            this.position = position;
            this.data = data;
        }
    }

    public Quadtree(BoundingBox boundary) {
        this.boundary = boundary;
        this.points = new ArrayList<>();
        this.isDivided = false;
    }

    public boolean insert(Point2D point, T data) {
        if (!boundary.contains(point)) {
            return false;
        }

        if (points.size() < MAX_CAPACITY) {
            points.add(new Entry(point, data));
            return true;
        }

        if (!isDivided) {
            subdivide();
        }

        for (Quadtree<T> child : children) {
            if (child.insert(point, data)) {
                return true;
            }
        }

        return false;
    }

    private void subdivide() {
        children = new Quadtree[4];

        double x = boundary.center.x;
        double y = boundary.center.y;
        double hs = boundary.halfSize / 2;

        children[0] = new Quadtree<>(new BoundingBox(new Point2D(x - hs, y - hs), hs));
        children[1] = new Quadtree<>(new BoundingBox(new Point2D(x + hs, y - hs), hs));
        children[2] = new Quadtree<>(new BoundingBox(new Point2D(x - hs, y + hs), hs));
        children[3] = new Quadtree<>(new BoundingBox(new Point2D(x + hs, y + hs), hs));

        isDivided = true;
    }

    public List<T> queryRange(BoundingBox range) {
        List<T> found = new ArrayList<>();

        if (!boundary.intersects(range)) {
            return found;
        }

        for (Entry entry : points) {
            if (range.contains(entry.position)) {
                found.add(entry.data);
            }
        }

        if (isDivided) {
            for (Quadtree<T> child : children) {
                found.addAll(child.queryRange(range));
            }
        }

        return found;
    }

    public List<T> queryRange(Point2D center, double halfSize) {
        return queryRange(new BoundingBox(center, halfSize));
    }

    public boolean remove(Point2D point, T data) {
        if (!boundary.contains(point)) {
            return false;
        }

        if (points.size() < MAX_CAPACITY && !isDivided) {
            points.removeIf(entry -> entry.position.equals(point) && entry.data.equals(data));
            return true;
        }

        if (!isDivided) {
            return false;
        }

        for (Quadtree<T> child : children) {
            if (child.remove(point, data)) {
                return true;
            }
        }

        return false;
    }

    public void clear() {
        points.clear();
        isDivided = false;
        children = null;
    }

    public int size() {
        int size = points.size();
        if (isDivided) {
            for (Quadtree<T> child : children) {
                size += child.size();
            }
        }
        return size;
    }

    public boolean isEmpty() {
        return points.isEmpty() && !isDivided;
    }

    public List<T> getAll() {
        List<T> all = new ArrayList<>(points.size());
        for (Entry entry : points) {
            all.add(entry.data);
        }

        if (isDivided) {
            for (Quadtree<T> child : children) {
                all.addAll(child.getAll());
            }
        }

        return all;
    }

    public List<T> getPoints() {
        List<T> all = new ArrayList<>(points.size());
        for (Entry entry : points) {
            all.add(entry.data);
        }
        return all;
    }

    public List<T> getChildren() {
        List<T> all = new ArrayList<>();
        if (isDivided) {
            for (Quadtree<T> child : children) {
                all.addAll(child.getAll());
            }
        }
        return all;
    }

    public List<T> getPointsInRegion(Point2D center, double halfSize) {
        return queryRange(new BoundingBox(center, halfSize));
    }

    public List<T> getPointsInRegion(double x, double y, double halfSize) {
        return getPointsInRegion(new Point2D(x, y), halfSize);
    }
}
