package net.azisaba.userworldedit.util;

import com.sk89q.worldedit.math.BlockVector3;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockVectorUtil {
    public static Set<BlockVector3> getBallooned(Set<BlockVector3> vset, double radius) {
        Set<BlockVector3> returnset = new HashSet<>();
        int ceilrad = (int) Math.ceil(radius);

        for (BlockVector3 v : vset) {
            int tipx = v.getBlockX();
            int tipy = v.getBlockY();
            int tipz = v.getBlockZ();

            for (int loopx = tipx - ceilrad; loopx <= tipx + ceilrad; loopx++) {
                for (int loopy = tipy - ceilrad; loopy <= tipy + ceilrad; loopy++) {
                    for (int loopz = tipz - ceilrad; loopz <= tipz + ceilrad; loopz++) {
                        if (hypot(loopx - tipx, loopy - tipy, loopz - tipz) <= radius) {
                            returnset.add(BlockVector3.at(loopx, loopy, loopz));
                        }
                    }
                }
            }
        }
        return returnset;
    }

    public static double hypot(double... pars) {
        double sum = 0;
        for (double d : pars) {
            sum += Math.pow(d, 2);
        }
        return Math.sqrt(sum);
    }

    public static Set<BlockVector3> getHollowed(Set<BlockVector3> vset) {
        Set<BlockVector3> returnset = new HashSet<>();
        for (BlockVector3 v : vset) {
            double x = v.getX();
            double y = v.getY();
            double z = v.getZ();
            if (!(vset.contains(BlockVector3.at(x + 1, y, z))
                    && vset.contains(BlockVector3.at(x - 1, y, z))
                    && vset.contains(BlockVector3.at(x, y + 1, z))
                    && vset.contains(BlockVector3.at(x, y - 1, z))
                    && vset.contains(BlockVector3.at(x, y, z + 1))
                    && vset.contains(BlockVector3.at(x, y, z - 1)))) {
                returnset.add(v);
            }
        }
        return returnset;
    }

    public static @NotNull Set<@NotNull BlockVector3> drawLine(@NotNull List<@NotNull BlockVector3> vectors, double radius, boolean filled) {
        Set<BlockVector3> vset = new HashSet<>();
        for (int i = 0; !vectors.isEmpty() && i < vectors.size() - 1; i++) {
            BlockVector3 pos1 = vectors.get(i);
            BlockVector3 pos2 = vectors.get(i + 1);
            int x1 = pos1.getBlockX();
            int y1 = pos1.getBlockY();
            int z1 = pos1.getBlockZ();
            int x2 = pos2.getBlockX();
            int y2 = pos2.getBlockY();
            int z2 = pos2.getBlockZ();
            int dx = Math.abs(x2 - x1);
            int dy = Math.abs(y2 - y1);
            int dz = Math.abs(z2 - z1);

            if (dx + dy + dz == 0) {
                vset.add(BlockVector3.at(x1, y1, z1));
                continue;
            }

            int dMax = Math.max(Math.max(dx, dy), dz);
            if (dMax == dx) {
                addLinePoints(vset, x1, y1, z1, x2, y2, z2, dx, dy, dz, dMax);
            } else if (dMax == dy) {
                addLinePoints(vset, x1, y1, z1, x2, y2, z2, dx, dy, dz, dMax);
            } else /* if (dMax == dz) */ {
                addLinePoints(vset, x1, y1, z1, x2, y2, z2, dx, dy, dz, dMax);
            }
        }

        if (!filled) {
            vset = getHollowed(vset);
        }
        return getBallooned(vset, radius);
    }

    private static void addLinePoints(Set<BlockVector3> vset, int x1, int y1, int z1, int x2, int y2, int z2, int dx, int dy, int dz, int dMax) {
        int tipx;
        int tipy;
        int tipz;

        for (int domstep = 0; domstep <= dMax; domstep++) {
            if (dMax == dx) {
                tipx = x1 + domstep * (x2 - x1 > 0 ? 1 : -1);
                tipy = (int) Math.round(y1 + domstep * ((double) dy) / ((double) dx) * (y2 - y1 > 0 ? 1 : -1));
                tipz = (int) Math.round(z1 + domstep * ((double) dz) / ((double) dx) * (z2 - z1 > 0 ? 1 : -1));
            } else if (dMax == dy) {
                tipy = y1 + domstep * (y2 - y1 > 0 ? 1 : -1);
                tipx = (int) Math.round(x1 + domstep * ((double) dx) / ((double) dy) * (x2 - x1 > 0 ? 1 : -1));
                tipz = (int) Math.round(z1 + domstep * ((double) dz) / ((double) dy) * (z2 - z1 > 0 ? 1 : -1));
            } else /* if (dMax == dz) */ {
                tipz = z1 + domstep * (z2 - z1 > 0 ? 1 : -1);
                tipy = (int) Math.round(y1 + domstep * ((double) dy) / ((double) dz) * (y2 - y1 > 0 ? 1 : -1));
                tipx = (int) Math.round(x1 + domstep * ((double) dx) / ((double) dz) * (x2 - x1 > 0 ? 1 : -1));
            }

            vset.add(BlockVector3.at(tipx, tipy, tipz));
        }
    }
}
