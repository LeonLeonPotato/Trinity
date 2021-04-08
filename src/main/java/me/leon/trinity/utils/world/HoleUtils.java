package me.leon.trinity.utils.world;

import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.entity.PlayerUtils;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class HoleUtils implements Util {
    public static HoleUtils.Hole getHole(BlockPos pos, int height) {
        boolean a = false;
        for(int b = 0; b < height; b++) {
            if(!WorldUtils.empty.contains(WorldUtils.getBlock(pos.add(0, b + 1, 0)))) {
                a = true;
            }
        }
        if(a) {
            return null;
        }
        if(WorldUtils.empty.contains(WorldUtils.getBlock(pos)) && !WorldUtils.empty.contains(WorldUtils.getBlock(pos.down()))) {
            if ((WorldUtils.getBlock(pos.north()) instanceof BlockObsidian || WorldUtils.getBlock(pos.north()) == Blocks.BEDROCK)
                    && (WorldUtils.getBlock(pos.south()) instanceof BlockObsidian || WorldUtils.getBlock(pos.south()) == Blocks.BEDROCK)
                    && (WorldUtils.getBlock(pos.east()) instanceof BlockObsidian || WorldUtils.getBlock(pos.east()) == Blocks.BEDROCK)
                    && (WorldUtils.getBlock(pos.west()) instanceof BlockObsidian || WorldUtils.getBlock(pos.west()) == Blocks.BEDROCK))
            {

                if(WorldUtils.getBlock(pos.north()) instanceof BlockObsidian
                        || WorldUtils.getBlock(pos.east()) instanceof BlockObsidian
                        || WorldUtils.getBlock(pos.south()) instanceof BlockObsidian
                        || WorldUtils.getBlock(pos.west()) instanceof BlockObsidian)
                {
                    return new SingleHole(pos, material.OBSIDIAN);
                }

                return new SingleHole(pos, material.BEDROCK);
            } else {
                final BlockPos[] dir = new BlockPos[] {
                        pos.west(), pos.north(), pos.east(), pos.south()
                };

                BlockPos pos1 = null;
                for(BlockPos dir1 : dir) {
                    if(WorldUtils.empty.contains(WorldUtils.getBlock(dir1))) {
                        pos1 = dir1;
                        break;
                    }
                }

                if(pos1 == null || WorldUtils.empty.contains(WorldUtils.getBlock(pos1.down()))) {
                    return null;
                }

                final BlockPos[] dir1 = new BlockPos[] {
                        pos1.west(), pos1.north(), pos1.east(), pos1.south()
                };

                int checked = 0;
                boolean obi = false;
                EnumFacing facing = null;

                for(BlockPos pos2 : dir1) {
                    if(pos2 == pos) continue;

                    if(WorldUtils.getBlock(pos2) instanceof BlockObsidian) {
                        obi = true;
                        checked++;
                    }
                    if(WorldUtils.getBlock(pos2) == Blocks.BEDROCK) {
                        checked++;
                    }
                }

                for(BlockPos pos2 : dir) {
                    if(pos2 == pos1) continue;

                    if(WorldUtils.getBlock(pos2) instanceof BlockObsidian) {
                        obi = true;
                        checked++;
                    }
                    if(WorldUtils.getBlock(pos2) == Blocks.BEDROCK) {
                        checked++;
                    }
                }

                for(EnumFacing facing1 : EnumFacing.values()) {
                    if(pos.add(facing1.getXOffset(), facing1.getYOffset(), facing1.getZOffset()).equals(pos1)) {
                        facing = facing1;
                    }
                }

                if(checked >= 6) {
                    return new DoubleHole(pos, pos1, obi ? material.OBSIDIAN : material.BEDROCK, facing);
                }
            }
        }
        return null;
    }

    public static class Hole {
        public type type;
        public material mat;
        public Hole(type type, material mat) {
            this.type = type;
            this.mat = mat;
        }
    }

    public final static class SingleHole extends Hole {
        public BlockPos pos;
        public SingleHole(BlockPos pos, material mat) {
            super(HoleUtils.type.SINGLE, mat);
            this.pos = pos;
        }
    }

    public final static class DoubleHole extends Hole {
        public BlockPos pos;
        public BlockPos pos1;
        public EnumFacing dir;
        public DoubleHole(BlockPos pos, BlockPos pos1, material mat, EnumFacing dir) {
            super(HoleUtils.type.DOUBLE, mat);
            this.pos = pos;
            this.pos1 = pos1;
            this.dir = dir;
        }

        public boolean contains(BlockPos pos) {
            if(this.pos == pos) return true;
            return this.pos1 == pos;
        }

        public boolean contains(DoubleHole pos) {
            if(pos.pos.equals(this.pos) || pos.pos.equals(this.pos1)) return true;
            if(pos.pos1.equals(this.pos) || pos.pos1.equals(this.pos1)) return true;
            return false;
        }
    }

    public static ArrayList<Hole> holes(float r, int height) {
        ArrayList<Hole> holes = new ArrayList<>();
        for(BlockPos pos : WorldUtils.getSphere(PlayerUtils.getPlayerPosFloored(), r, (int) r, false, true, 0)) {
            final Hole hole = getHole(pos, height);
            if(hole != null) {
                holes.add(hole);
            }
        }
        return holes;
    }


    public enum type {
        DOUBLE, SINGLE
    }

    public enum material {
        BEDROCK, OBSIDIAN
    }
}
