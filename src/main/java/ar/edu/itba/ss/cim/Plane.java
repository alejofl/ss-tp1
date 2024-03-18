package ar.edu.itba.ss.cim;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Plane<T extends Particle> {
    private final int length;
    private final List<T> particles;

    protected Plane(int length, List<T> particles) {
        this.length = length;
        this.particles = particles;
    }

    public int getLength() {
        return length;
    }

    public List<T> getParticles() {
        return particles;
    }

    @Override
    public String toString() {
        return "Plane{" +
                "length=" + length +
                ", particles=" + particles +
                '}';
    }

    public static class Builder<T extends Particle> {
        private Integer length;
        private final ArrayList<T> particles;

        protected Builder() {
            this.particles = new ArrayList<>();
        }

        public static <K extends Particle> Builder<K> newBuilder() {
            return new Builder<>();
        }

        public Builder<T> withLength(int length) {
            if (particles.stream().anyMatch(particle -> particle.getX() > length || particle.getY() > length) || length <= 0) {
                throw new IllegalArgumentException();
            }
            this.length = length;
            return this;
        }

        public Builder<T> withParticle(T particle) {
            if (particle.getX() < 0 || particle.getY() < 0 || (length != null && (particle.getX() > length || particle.getY() > length))) {
                throw new IllegalArgumentException();
            }
            this.particles.add(particle);
            return this;
        }

        public Builder<T> withParticles(T... particles) {
            for (T p : particles) {
                this.withParticle(p);
            }
            return this;
        }

        public Builder<T> withParticles(Iterable<T> particles) {
            for (T p : particles) {
                this.withParticle(p);
            }
            return this;
        }

        public Plane<T> build() {
            if (length == null || particles.isEmpty()) {
                throw new IllegalStateException();
            }

            return new Plane<>(length, new ArrayList<>(particles));
        }
    }
}
