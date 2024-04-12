package ar.edu.itba.ss.cim;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        // Leemos archivo y obtenemos los datos
        List<String> data = null;
        try (Stream<String> stream = Files.lines(Paths.get("input.txt"))) {
            data = stream.toList();
        } catch (Exception e) {
            System.err.println("No input file found");
        }

        if (data == null) {
            throw new IllegalStateException();
        }

        final int particleCount = Integer.parseInt(data.get(0)); // N
        final int planeLength = Integer.parseInt(data.get(1)); // L
        final boolean optimumMatrixCellCount = data.get(2).equals("-");
        final Integer matrixCellCount = optimumMatrixCellCount ? null : Integer.parseInt(data.get(2)); // M
        final double interactionRadius = Double.parseDouble(data.get(3)); // r_c
        final boolean periodicConditions = Boolean.parseBoolean(data.get(4)); // cond
        final ArrayList<Double> particlesRadius = new ArrayList<>();
        for (int i = 6; i < data.size(); i++) {
            particlesRadius.add(Double.parseDouble(data.get(i))); // r_i
        }

        if (data.size() - 6 != particleCount) {
            throw new IllegalStateException("Particle count does not match the amount of radii provided");
        }

        // Creamos el plano
        Plane.Builder<Particle> planeBuilder = Plane.Builder.newBuilder().withLength(1.0 * planeLength);
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
        final Plane<Particle> plane = planeBuilder.build();

        // Creamos el ejecutor del método
        CellIndexMethod.Builder<Particle> cimBuilder = CellIndexMethod.Builder.newBuilder()
                .withInteractionRadius(interactionRadius)
                .withPeriodicConditions(periodicConditions)
                .withPlane(plane);
        if (optimumMatrixCellCount) {
            cimBuilder = cimBuilder.withOptimumMatrixCellCount();
        } else {
            cimBuilder = cimBuilder.withMatrixCellCount(matrixCellCount);
        }
        final CellIndexMethod<Particle> cim = cimBuilder.build();

        // Ejecutamos el método CIM
        LocalDateTime startTime = LocalDateTime.now();
        System.out.printf("%s: Starting Cell Index Method execution%n", startTime);
        final Map<Particle, Set<Particle>> neighbours = cim.execute();
        System.out.printf("%s: Finished Cell Index Method execution%n", LocalDateTime.now());
        System.out.printf("Execution time: %d ms%n", Duration.between(startTime, LocalDateTime.now()).toMillis());
        System.out.println("--------------------------------------------------");

        // Exportamos los resultados
        try (
                BufferedWriter writer = Files.newBufferedWriter(
                        Paths.get("output.txt"),
                        StandardOpenOption.WRITE,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                )
        ) {
            List<Map.Entry<Particle, Set<Particle>>> entries = new ArrayList<>(neighbours.entrySet());
            entries.sort(Comparator.comparing(entry -> Integer.parseInt(entry.getKey().getIdentifier().substring(2))));
            for (Map.Entry<Particle, Set<Particle>> entry : entries) {
                String neighboursString = entry.getValue().stream().map(Particle::getIdentifier).reduce((s1, s2) -> s1 + ", " + s2).orElse("");
                writer.write(String.format("%s %f %f %f \"%s\"", entry.getKey().getIdentifier(), entry.getKey().getRadius(), entry.getKey().getX(), entry.getKey().getY(), neighboursString));
                writer.newLine();
            }
        } catch (Exception e) {
            System.err.println("Error writing output");
        }

        // Ejecutamos el método de fuerza bruta
        startTime = LocalDateTime.now();
        System.out.printf("%s: Starting Brute Force execution%n", startTime);
        final Map<Particle, Set<Particle>> bruteForceNeighbours = cim.bruteForce();
        System.out.printf("%s: Finished Brute Force execution%n", LocalDateTime.now());
        System.out.printf("Execution time: %d ms%n", Duration.between(startTime, LocalDateTime.now()).toMillis());

        if (!neighbours.equals(bruteForceNeighbours)) {
            throw new IllegalStateException("Results from CI and Brute Force methods do not match");
        }
    }
}
