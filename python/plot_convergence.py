import sys
import os
import csv
import matplotlib.pyplot as plt

def plot_csv(filename, output_file):
    iters = []
    costs = []

    with open(filename, 'r') as csvfile:
        reader = csv.reader(csvfile)
        # skip header
        header = next(reader)  # ["Iter", "Cost"]

        for row in reader:
            # row[0] -> Iter, row[1] -> Cost
            iters.append(int(row[0]))
            costs.append(float(row[1]))

    plt.figure(figsize=(8, 4))
    plt.plot(iters, costs, marker=None, linestyle='-')
    plt.title(placer_name + ": HPWL Cost vs Iteration")
    plt.xlabel("Iter")
    plt.ylabel("HPWL Cost")
    plt.grid(True)
    plt.ylim(bottom=0)
    plt.savefig(output_file, dpi=200, bbox_inches='tight')

if __name__ == "__main__":
    placer_path = sys.argv[1]
    placer_name = os.path.basename(placer_path)
    csv_file =  placer_path + "/printout/cost_history.csv"
    png_file =  placer_path + "/graphics/" + placer_name + "_cost_history.png"
    plot_csv(csv_file, png_file)
    print(placer_path + " plot_convergence finished")
    # plot_csv("outputs/placers/PlacerAnnealHybrid/printout/cost_history.csv", "outputs/placers/PlacerAnnealHybrid/graphics/cost_history.png")
    # plot_csv("outputs/placers/PlacerAnnealRandom/printout/cost_history.csv", "outputs/placers/PlacerAnnealRandom/graphics/cost_history.png")
