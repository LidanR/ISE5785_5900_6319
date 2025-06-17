package acceleration;

import java.util.Objects;

/**
 * Represents a voxel's 3D index (i, j, k) in the grid.
 * Used as a key in the voxel HashMap.
 */
public class VoxelIndex {
    public final int i, j, k; // Indices in the voxel grid
    /**
     * Constructs a VoxelIndex with the specified indices.
     *
     * @param i the index in the x-direction
     * @param j the index in the y-direction
     * @param k the index in the z-direction
     */
    public VoxelIndex(int i, int j, int k) {
        this.i = i;
        this.j = j;
        this.k = k;
    }

    /**
     * Checks if this VoxelIndex is equal to another object.
     * @param obj the object to compare with
     * @return true if the object is a VoxelIndex with the same indices, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VoxelIndex other)) return false;
        return i == other.i && j == other.j && k == other.k;
    }
    /**
     * Returns the hash code for this VoxelIndex.
     * The hash code is computed based on the indices i, j, and k.
     *
     * @return the hash code of this VoxelIndex
     */
    @Override
    public int hashCode() {
        return Objects.hash(i, j, k);
    }
}
