import pandas as pd
from pathlib import Path
import matplotlib.pyplot as plt
import re


def parse_label(label):
    # match either "PlacerAnnealHybrid" or "PlacerAnnealRandom" followed by numbers
    match = re.match(r'PlacerAnneal(?:Hybrid|Random)_(\d+)_(\d+)', label)
    if match:
        temp = int(match.group(1))
        rate = int(match.group(2))
        return temp, rate
    return None, None


def cooling_to_brightness(cooling_rate, min_rate=84, max_rate=99):
    return (cooling_rate - min_rate) / (max_rate - min_rate)


def plot_group(temp_group, temp_to_color, df, output_path, title_suffix=""):
    plt.figure(figsize=(6, 5))
    for label in temp_group:
        temp, rate = parse_label(label)
        base_rgb = temp_to_color[temp]
        alpha = 0.2 + 0.6 * (1 - cooling_to_brightness(rate))
        y = pd.to_numeric(df[label], errors='coerce').dropna().values
        x = range(len(y))
        plt.plot(x, y, color=base_rgb, label=label, alpha=alpha, linewidth=1.5)

    plt.xlabel("Passes")
    plt.ylabel("Cost")
    # plt.yscale("log")
    plt.ylim(2.5e5, 1e6)
    plt.title(f"Cost History of SA Placers {title_suffix}")
    plt.legend(fontsize="small")
    plt.grid(True)
    plt.tight_layout()
    plt.savefig(output_path)
    plt.close()


def main():
    script_dir = Path(__file__).resolve().parent
    root_dir = script_dir.parent

    csv_path = root_dir / "outputs" / "combined_cost_history.csv"
    df = pd.read_csv(csv_path)

    labels = df.columns.tolist()
    base_colors = plt.colormaps.get_cmap("tab10")

    temp_to_color = {}
    used_temps = []
    temp_to_labels = {}

    # --- Combined plot ---
    plt.figure(figsize=(6, 5))
    # plt.yscale("log")
    plt.ylim(2.5e5, 1e6)

    for label in labels:
        if label.strip().lower() in ("passes", "unnamed: 0"):
            continue

        temp, rate = parse_label(label)
        if temp is None:
            print(f"Skipping label: {label}")
            continue

        print(f"Plotting: {label} (temp={temp}, rate={rate})")

        if temp not in temp_to_color:
            temp_to_color[temp] = base_colors(len(used_temps) % 10)
            used_temps.append(temp)

        temp_to_labels.setdefault(temp, []).append(label)

        base_rgb = temp_to_color[temp]
        alpha = 0.2 + 0.6 * (1 - cooling_to_brightness(rate))
        y = pd.to_numeric(df[label], errors='coerce').dropna().values
        x = range(len(y))
        plt.plot(x, y, color=base_rgb, label=label, alpha=alpha, linewidth=1.5)

    plt.xlabel("Passes")
    plt.ylabel("Cost")
    plt.title("Combined Cost History of all SA Placers")
    # plt.legend(fontsize="small")
    plt.grid(True)
    plt.tight_layout()
    plt.savefig(root_dir / "outputs" / "placers" / "combined_cost_history_cooling.png")
    plt.close()

    # --- Per-temp plots ---
    for temp, group_labels in temp_to_labels.items():
        output_path = root_dir / "outputs" / "placers" / f"cost_history_{temp}.png"
        print(f"Creating plot: {output_path}")
        plot_group(group_labels, temp_to_color, df, output_path, title_suffix=f"(Initial Temp {temp})")


if __name__ == "__main__":
    main()
