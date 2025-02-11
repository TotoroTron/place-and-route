
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
    plt.title("HPWL Cost vs Iteration")
    plt.xlabel("Iter")
    plt.ylabel("HPWL Cost")
    plt.grid(True)
    plt.ylim(bottom=0)
    plt.savefig(output_file, dpi=200, bbox_inches='tight')

if __name__ == "__main__":
    plot_csv("outputs/printout/PlacerGreedyRandom3.csv", "outputs/graphics/PlacerGreedyRandom3.png")
