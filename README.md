# Cell Index Method

[![](https://jitpack.io/v/alejofl/ss-tp1.svg)](https://jitpack.io/#alejofl/ss-tp1)

## Introduction

The Cell Index Method is a computational algorithm used in the field of computational physics. It is particularly useful for simulating the behavior of particles in a system, where the interaction between particles is important. This method allows for efficient computation by dividing the simulation space into cells, hence the name. Each particle is assigned to a cell, and only interactions with particles in neighboring cells are considered. This significantly reduces the computational complexity of the simulation.

## Requirements

* Java 19
* Maven
* Python 3 (only if you want the animation to be rendered)

## Building the project

To build the project, `cd` to the root of the project and run the following command:

```bash
mvn clean package
```

This will compile and package a `.jar` file in the `target` directory.

## Executing the project

> [!NOTE]  
> The following instructions assume that you have built the project as described in the previous section and that the generated `.jar` file is in the current working directory.

The program expects to have an input file named `input.txt` in the current working directory. The input file should contain the following structure:

```text
{{ number_of_particles }}
{{ plane_length }}
{{ matrix_cell_count }}
{{ interaction_radius }}
{{ periodic_conditions }}
{{ selected_particle_index }}
{{ particle_i_radius ; for 0 <= i < number_of_particles }}
```

Where:

* `number_of_particles` is the number of particles in the simulation. Must be an integer.
* `plane_length` is the length of the square plane where the particles are located. Must be a floating point number.
* `matrix_cell_count` is the number of cells in the matrix. Let it be `-` if you want the optimum value to be calculated. Must be an integer or `-`.
* `interaction_radius` is the radius of the interaction between particles. Must be a floating point number.
* `periodic_conditions` is a boolean value indicating whether the simulation should consider periodic contour conditions. Must be `true` or `false`.
* `selected_particle_index` is the index of the particle to be analyzed in the animation. Must be an integer.
* `particle_i_radius` is the radius of the `i`-th particle. Must be a floating point number.

To execute the project, run the following command:

```bash
java -jar cell-index-method-1.0-SNAPSHOT.jar
```

This will execute the program and generate an output file named `output.txt` in the current working directory. This file will have the following structure:

```text
{{ particle_id }} {{ radius }} {{ x_position }} {{ y_position }} {{ neighbours }}
...
```

Where:

* `particle_id` is the identifier of the particle.
* `radius` is the radius of the particle.
* `x_position` is the x-coordinate of the particle.
* `y_position` is the y-coordinate of the particle.
* `neighbours` is a comma-separated list of the identifiers of the particles that are neighbors of the particle.

## Visualizing the output

> [!NOTE]  
> The following instructions assume that you have executed the project as described in the previous section and that the files `input.txt` and `output.txt` are in the current working directory.

To visualize the output, we must run a Python script. First, we need to install the required dependencies. To do so, run the following command:

```bash
python -m venv venv
source venv/bin/activate
pip install -r animation/requirements.txt
```

To visualize the output, run the following command:

```bash
python animation/animation.py
```

This will generate an animation of the particles in the simulation. The selected particle will be highlighted in red, and its neighbors will be highlighted in green. A black circle will represent the interaction radius of the selected particle.

## Final Remarks

This project was done in an academic environment, as part of the curriculum of Systems Simulation from Instituto Tecnológico de Buenos Aires (ITBA)

The project was carried out by:

* Alejo Flores Lucey
* Nehuén Gabriel Llanos
