package ar.edu.itba.ss.cim;

public class Particle {
    private final double radius;
    private final double x;
    private final double y;

    private Particle(double radius, double x, double y) {
        this.radius = radius;
        this.x = x;
        this.y = y;
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

    public static class Builder {
        private Double radius;
        private Double x;
        private Double y;

        private Builder() {

        }

        public static Builder newBuilder() {
            return new Builder();
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
            if (this.radius == null || this.x == null || this.y == null) {
                throw new IllegalStateException();
            }

            return new Particle(
                    this.radius,
                    this.x,
                    this.y
            );
        }
    }
}
