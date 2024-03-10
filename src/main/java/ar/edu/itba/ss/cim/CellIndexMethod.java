package ar.edu.itba.ss.cim;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class CellIndexMethod {
    final private Integer matrixCellCount;
    final private boolean periodicConditions;
    final private Plane plane;

    private CellIndexMethod(Integer matrixCellCount, boolean periodicConditions, Plane plane) {
        this.matrixCellCount = matrixCellCount;
        this.periodicConditions = periodicConditions;
        this.plane = plane;
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
            neighbours.get(particle).remove(particle);
        }

        return neighbours;
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
                    this.matrixCellCount,
                    this.periodicConditions,
                    this.plane
            );
        }
    }

    public static void main(String[] args) {
        // Leemos archivo y obtenemos los datos
        String[] data = new String[0];
        try (Stream<String> stream = Files.lines(Paths.get(CellIndexMethod.class.getClassLoader().getResource("input.txt").toURI()))) {
            data = stream.toArray(String[]::new);
        } catch (Exception e) {
            System.err.println("No input file found");
        }

        final int particleCount = Integer.parseInt(data[0]); // N
        final int planeLength = Integer.parseInt(data[1]); // L
        final boolean optimumMatrixCellCount = data[2].equals("-");
        final Integer matrixCellCount = optimumMatrixCellCount ? null : Integer.parseInt(data[2]); // M
        final double interactionRadius = Double.parseDouble(data[3]); // r_c
        final boolean periodicConditions = Boolean.parseBoolean(data[4]); // cond
        final ArrayList<Double> particlesRadius = new ArrayList<>();
        for (int i = 5; i < data.length; i++) {
            particlesRadius.add(Double.parseDouble(data[i])); // r_i
        }

        if (data.length - 5 != particleCount) {
            throw new IllegalStateException("Particle count does not match the amount of radii provided");
        }

        // Creamos el plano
        Plane.Builder planeBuilder = Plane.Builder.newBuilder().withLength(planeLength);
        // Creamos todas las partículas (con posiciones random)
        // Asignamos las partículas al plano
        for (int i = 0; i < particlesRadius.size(); i++) {
            final double x = Math.random() * planeLength;
            final double y = Math.random() * planeLength;
            planeBuilder = planeBuilder.withParticle(
                    Particle.Builder.newBuilder()
                            .withIdentifier(String.format("p_%d", i))
                            .withX(x)
                            .withY(y)
                            .withRadius(particlesRadius.get(i))
                            .build()
            );
        }
        final Plane plane = planeBuilder.build();

        // Creamos el ejecutor del método
        CellIndexMethod.Builder cimBuilder = CellIndexMethod.Builder.newBuilder()
                .withInteractionRadius(interactionRadius)
                .withPeriodicConditions(periodicConditions)
                .withPlane(plane);
        if (optimumMatrixCellCount) {
            cimBuilder = cimBuilder.withOptimumMatrixCellCount();
        } else {
            cimBuilder = cimBuilder.withMatrixCellCount(matrixCellCount);
        }
        final CellIndexMethod cim = cimBuilder.build();

        // Ejecutamos el método
        final Map<Particle, Set<Particle>> neighbours = cim.execute();
        for (Map.Entry<Particle, Set<Particle>> entry : neighbours.entrySet()) {
            System.out.printf("%s (%.2f - %.2f) -> %s%n", entry.getKey().getIdentifier(), entry.getKey().getX(), entry.getKey().getY(), entry.getValue().stream().map(Particle::getIdentifier).reduce((s1, s2) -> s1 + ", " + s2).orElse(""));
        }
    }
}
