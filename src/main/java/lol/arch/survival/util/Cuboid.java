package lol.arch.survival.util;

/**
 * @author elijuh
 */
public class Cuboid {
    private final int minX, minY, minZ, maxX, maxY, maxZ;
    public Cuboid(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }
    public int[][] getAllPoints() {
        int[][] points = new int[getArea()][];
        int index = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    points[index++] = new int[]{x, y, z};
                }
            }
        }
        return points;
    }
    public int getArea() {
        int dx = maxX - minX;
        int dy = maxY - minY;
        int dz = maxZ - minZ;
        return (dx + 1) * (dy + 1) * (dz + 1);
    }
}
