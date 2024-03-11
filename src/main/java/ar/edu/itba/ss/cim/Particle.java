package ar.edu.itba.ss.cim;

import java.util.Objects;

public class Particle {
    private final String identifier;
    private final double radius;
    private final double x;
    private final double y;

    private Particle(String identifier, double radius, double x, double y) {
        this.identifier = identifier;
        this.radius = radius;
        this.x = x;
        this.y = y;
    }

    public String getIdentifier() {
        return identifier;
    }

    public double getRadius() {
        return radius;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double distanceTo(Particle other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    public double distanceTo(Particle other, boolean borderToBorder) {
        double distance = this.distanceTo(other);
        if (!borderToBorder) {
            return distance;
        }
        return distance - this.radius - other.radius;
    }

    @Override
    public String toString() {
        return "Particle{" +
                "identifier='" + identifier + "'" +
                "radius=" + radius +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return Double.compare(radius, particle.radius) == 0 && Double.compare(x, particle.x) == 0 && Double.compare(y, particle.y) == 0 && Objects.equals(identifier, particle.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, radius, x, y);
    }

    public static class Builder {
        private String identifier;
        private Double radius;
        private Double x;
        private Double y;

        private Builder() {

        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withIdentifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder withRadius(double radius) {
            this.radius = radius;
            return this;
        }

        public Builder withX(double x) {
            this.x = x;
            return this;
        }

        public Builder withY(double y) {
            this.y = y;
            return this;
        }

        public Particle build() {
            if (this.identifier == null || this.radius == null || this.x == null || this.y == null) {
                throw new IllegalStateException();
            }

            return new Particle(
                    this.identifier,
                    this.radius,
                    this.x,
                    this.y
            );
        }
    }
}
