package ar.edu.itba.ss.cim;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class CellIndexMethod {
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
        final Plane.Builder planeBuilder = Plane.Builder.newBuilder().withLength(planeLength);
        // Creamos todas las partículas (con posiciones random)
        // Asignamos las partículas al plano
        for (Double particleRadius : particlesRadius) {
            final double x = Math.random() * planeLength;
            final double y = Math.random() * planeLength;
            planeBuilder.withParticle(
                    Particle.Builder.newBuilder()
                            .withX(x)
                            .withY(y)
                            .withRadius(particleRadius)
                            .build()
            );
        }
        final Plane plane = planeBuilder.build();

        // Creamos el ejecutor del método
        // Ejecutamos el método
    }
}
