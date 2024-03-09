package ar.edu.itba.ss.cim;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Plane {
    private final int length;
    private final List<Particle> particles;

    private Plane(int length, List<Particle> particles) {
        this.length = length;
        this.particles = particles;
    }

    public int getLength() {
        return length;
    }

    public List<Particle> getParticles() {
        return particles.stream().collect((Collectors.toList()));
    }

    public static class Builder {
        private Integer length;
        private final ArrayList<Particle> particles;

        private Builder() {
            this.particles = new ArrayList<>();
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withLength(int length) {
            if (particles.stream().anyMatch(particle -> particle.getX() > length || particle.getY() > length) || length <= 0) {
                throw new IllegalArgumentException();
            }
            this.length = length;
            return this;
        }

        public Builder withParticle(Particle particle) {
            if (particle.getX() < 0 || particle.getY() < 0 || (length != null && (particle.getX() > length || particle.getY() > length))) {
                throw new IllegalArgumentException();
            }
            this.particles.add(particle);
            return this;
        }

        public Builder withParticles(Particle... particles) {
            for (Particle p : particles) {
                this.withParticle(p);
            }
            return this;
        }

        public Builder withParticles(Iterable<Particle> particles) {
            for (Particle p : particles) {
                this.withParticle(p);
            }
            return this;
        }

        public Plane build() {
            if (length == null || particles.isEmpty()) {
                throw new IllegalStateException();
            }

            return new Plane(length, new ArrayList<>(particles));
        }
    }
}
