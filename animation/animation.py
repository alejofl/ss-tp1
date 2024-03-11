import csv
import matplotlib.pyplot as plt

with open("../output.txt") as particlesFile, open("../input.txt") as inputFile:
    input_data = inputFile.readlines()
    plane_length = int(input_data[1][:-1])
    interaction_radius = float(input_data[3][:-1])
    selected_particle_index = int(input_data[5][:-1])
    data = []
    for row in csv.reader(particlesFile, delimiter=" "):
        row[4] = row[4].split(", ")
        data.append(row)

    particles = []
    for index in range(len(data)):
        if index == selected_particle_index:
            color = 'r'
        elif data[index][0] in data[selected_particle_index][4]:
            color = 'g'
        else:
            color = 'b'

        particles.append(
            plt.Circle(
                (float(data[index][2]), float(data[index][3])),
                float(data[index][1]),
                color=color,
                fill=False
            )
        )

    plt.title('Cell Index Method')
    plt.xlabel('X')
    plt.ylabel('Y')
    plt.grid(True)
    axes = plt.gca()
    axes.set_aspect('equal', adjustable='box')
    axes.set_xlim(0, plane_length)
    axes.set_ylim(0, plane_length)
    for circle in particles:
        axes.add_patch(circle)
    # for particle in data:
    #     plt.annotate(particle[0], (float(particle[2]), float(particle[3])))
    axes.add_patch(
        plt.Circle(
            (float(data[selected_particle_index][2]), float(data[selected_particle_index][3])),
            float(data[selected_particle_index][1]) + interaction_radius,
            color='k',
            fill=False
        )
    )

    plt.show()
    # plt.savefig('../animation.png', dpi=300)
