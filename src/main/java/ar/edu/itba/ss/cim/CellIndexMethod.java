package ar.edu.itba.ss.cim;

import java.util.*;
import java.util.stream.Collectors;

public class CellIndexMethod {
    final private double interactionRadius;
    final private Integer matrixCellCount;
    final private boolean periodicConditions;
    final private Plane plane;

    private CellIndexMethod(double interactionRadius, Integer matrixCellCount, boolean periodicConditions, Plane plane) {
        this.interactionRadius = interactionRadius;
        this.matrixCellCount = matrixCellCount;
        this.periodicConditions = periodicConditions;
        this.plane = plane;
    }

    public double getInteractionRadius() {
        return interactionRadius;
    }

    public Integer getMatrixCellCount() {
        return matrixCellCount;
    }

    public boolean isPeriodicConditions() {
        return periodicConditions;
    }

    public Plane getPlane() {
        return plane;
    }

    @Override
    public String toString() {
        return "CellIndexMethod{" +
                "matrixCellCount=" + matrixCellCount +
                ", periodicConditions=" + periodicConditions +
                ", plane=" + plane +
                '}';
    }

    public Map<Particle, Set<Particle>> execute() {
        final double cellSize = 1.0 * plane.getLength() / matrixCellCount;
        final HashMap<Integer, HashSet<Particle>> matrix = new HashMap<>();
        final HashMap<Particle, HashSet<Integer>> cellsForParticle = new HashMap<>();

        // Fill the matrix with the particles that are inside each cell
        for (Particle particle : plane.getParticles()) {
            for (int i = 0; i < matrixCellCount; i++) {
                for (int j = 0; j < matrixCellCount; j++) {
                    final int cellNumber = i * matrixCellCount + (j + 1);
                    final double cellTopLeftX = j * cellSize;
                    final double cellTopLeftY = plane.getLength() - i * cellSize;
                    final double cellBottomRightX = (j + 1) * cellSize;
                    final double cellBottomRightY = plane.getLength() - (i + 1) * cellSize;

                    // Find the nearest point on the cell to the center of the particle
                    final double nearestX = Math.max(cellTopLeftX, Math.min(particle.getX(), cellBottomRightX));
                    final double nearestY = Math.min(cellTopLeftY, Math.max(particle.getY(), cellBottomRightY));
                    final double distanceX = nearestX - particle.getX();
                    final double distanceY = nearestY - particle.getY();

                    if (Math.pow(distanceX, 2) + Math.pow(distanceY, 2) <= Math.pow(particle.getRadius(), 2)) {
                        matrix.putIfAbsent(cellNumber, new HashSet<>());
                        cellsForParticle.putIfAbsent(particle, new HashSet<>());
                        matrix.get(cellNumber).add(particle);
                        cellsForParticle.get(particle).add(cellNumber);
                    }
                }
            }
        }

        // Create neighbours list
        final Map<Particle, Set<Particle>> neighbours = new HashMap<>();
        for (Particle particle : plane.getParticles()) {
            neighbours.put(particle, new HashSet<>());
        }

        // Fill the neighbours list
        for (Particle particle : plane.getParticles()) {
            final HashSet<Integer> cells = cellsForParticle.get(particle);
            for (Integer cell : cells) {
                Optional.ofNullable(matrix.get(cell)).ifPresent(set -> neighbours.get(particle).addAll(set));
                Integer topCell = null;
                Integer topRightCell = null;
                Integer rightCell = null;
                Integer bottomRightCell = null;
                if (periodicConditions) {
                    if (cell % matrixCellCount == 0) {
                        topCell = (cell - matrixCellCount) % (matrixCellCount * matrixCellCount);
                        if (topCell == 0) {
                            topCell = matrixCellCount * matrixCellCount;
                        }
                        topRightCell = (cell - 2 * matrixCellCount + 1) % (matrixCellCount * matrixCellCount);
                        rightCell = (cell + 1 - matrixCellCount) % (matrixCellCount * matrixCellCount);
                        bottomRightCell = (cell + 1) % (matrixCellCount * matrixCellCount);
                    } else {
                        topCell = (cell - matrixCellCount) % (matrixCellCount * matrixCellCount);
                        topRightCell = (cell - matrixCellCount + 1) % (matrixCellCount * matrixCellCount);
                        rightCell = (cell + 1) % (matrixCellCount * matrixCellCount);;
                        bottomRightCell = (cell + matrixCellCount + 1) % (matrixCellCount * matrixCellCount);;
                    }
                } else {
                    if (cell < matrixCellCount) {
                        rightCell = cell + 1;
                        bottomRightCell = cell + matrixCellCount + 1;
                    } else if (cell % matrixCellCount == 0 &&  cell > matrixCellCount) {
                        topCell = cell - matrixCellCount;
                    } else if (cell > matrixCellCount * (matrixCellCount - 1) && cell % matrixCellCount != 0) {
                        topCell = cell - matrixCellCount;
                        topRightCell = cell - matrixCellCount + 1;
                        rightCell = cell + 1;
                    } else if (cell % matrixCellCount != 0) {
                        topCell = cell - matrixCellCount;
                        topRightCell = cell - matrixCellCount + 1;
                        rightCell = cell + 1;
                        bottomRightCell = cell + matrixCellCount + 1;
                    }
                }
                if (topCell != null) {
                    Optional.ofNullable(matrix.get(topCell)).ifPresent(set -> {
                        neighbours.get(particle).addAll(set);
                        set.forEach(p -> neighbours.get(p).add(particle));
                    });
                }
                if (topRightCell != null) {
                    Optional.ofNullable(matrix.get(topRightCell)).ifPresent(set -> {
                        neighbours.get(particle).addAll(set);
                        set.forEach(p -> neighbours.get(p).add(particle));
                    });
                }
                if (rightCell != null) {
                    Optional.ofNullable(matrix.get(rightCell)).ifPresent(set -> {
                        neighbours.get(particle).addAll(set);
                        set.forEach(p -> neighbours.get(p).add(particle));
                    });
                }
                if (bottomRightCell != null) {
                    Optional.ofNullable(matrix.get(bottomRightCell)).ifPresent(set -> {
                        neighbours.get(particle).addAll(set);
                        set.forEach(p -> neighbours.get(p).add(particle));
                    });
                }
            }
        }

        for (Particle particle : getPlane().getParticles()) {
            Set<Particle> newNeighboursForParticle = neighbours.get(particle).stream()
                    .filter(p -> !p.equals(particle) && p.distanceTo(particle, true) <= interactionRadius)
                    .collect(Collectors.toSet());
            neighbours.put(particle, newNeighboursForParticle);
        }

        return neighbours;
    }

    public Map<Particle, Set<Particle>> bruteForce() {
        Map<Particle, Set<Particle>> ans = new HashMap<>();
        for (Particle particle : getPlane().getParticles()) {
            ans.put(particle, new HashSet<>());
        }
        for (Particle particle : getPlane().getParticles()) {
            for (Particle otherParticle : getPlane().getParticles()) {
                if (
                        !particle.equals(otherParticle) &&
                        particle.distanceTo(otherParticle, true) <= interactionRadius
                ) {
                    ans.get(particle).add(otherParticle);
                }
            }
        }
        return ans;
    }

    public static class Builder {
        private boolean optimumMatrixCellCount = false;
        private Integer matrixCellCount;
        private Double interactionRadius;
        private Boolean periodicConditions = false;
        private Plane plane;

        private Builder() {

        }

        private void calculateOptimumMatrixCellCount() {
            this.matrixCellCount = (int) Math.ceil(plane.getLength() / interactionRadius);
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withOptimumMatrixCellCount() {
            this.optimumMatrixCellCount = true;
            if (plane != null && interactionRadius != null) {
                calculateOptimumMatrixCellCount();
            }
            return this;
        }

        public Builder withMatrixCellCount(int matrixCellCount) {
            this.optimumMatrixCellCount = false;
            this.matrixCellCount = matrixCellCount;
            return this;
        }

        public Builder withInteractionRadius(double interactionRadius) {
            this.interactionRadius = interactionRadius;
            if (plane != null && optimumMatrixCellCount) {
                calculateOptimumMatrixCellCount();
            }
            return this;
        }

        public Builder withPeriodicConditions(boolean periodicConditions) {
            this.periodicConditions = periodicConditions;
            return this;
        }

        public Builder withPlane(Plane plane) {
            this.plane = plane;
            if (plane != null && this.interactionRadius != null && optimumMatrixCellCount) {
                calculateOptimumMatrixCellCount();
            }
            return this;
        }

        public CellIndexMethod build() {
            if (this.matrixCellCount == null || this.interactionRadius == null || this.plane == null) {
                throw new IllegalStateException();
            }

            return new CellIndexMethod(
                    this.interactionRadius,
                    this.matrixCellCount,
                    this.periodicConditions,
                    this.plane
            );
        }
    }
}
