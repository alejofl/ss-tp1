package ar.edu.itba.ss.cim;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class CellIndexMethod {
    final private Integer matrixCellCount;
    final private double interactionRadius;
    final private boolean periodicConditions;
    final private Plane plane;

    public CellIndexMethod(Integer matrixCellCount, double interactionRadius, boolean periodicConditions, Plane plane) {
        this.matrixCellCount = matrixCellCount;
        this.interactionRadius = interactionRadius;
        this.periodicConditions = periodicConditions;
        this.plane = plane;
    }

    public Integer getMatrixCellCount() {
        return matrixCellCount;
    }

    public double getInteractionRadius() {
        return interactionRadius;
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
                ", interactionRadius=" + interactionRadius +
                ", periodicConditions=" + periodicConditions +
                ", plane=" + plane +
                '}';
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
                    this.interactionRadius,
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
        for (Double particleRadius : particlesRadius) {
            final double x = Math.random() * planeLength;
            final double y = Math.random() * planeLength;
            planeBuilder = planeBuilder.withParticle(
                    Particle.Builder.newBuilder()
                            .withX(x)
                            .withY(y)
                            .withRadius(particleRadius)
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
        System.out.println(cim);
    }
}
