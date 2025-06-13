"""
Script to combine cost_history.csv files from each placer into a single CSV
and plot the combined cost histories on a logarithmic y-scale.

Place this script in the `python` subdirectory of your project (e.g.,
`~/workspace/dev/place-and-route/python`).
It will look under `outputs/placers/*/printout/cost_history.csv`, merge them on the Iter column,
save the merged CSV to `combined_cost_history.csv` in the project root,
and save a log-scale plot to `combined_cost_history.png` in the project root.
"""
import sys
from pathlib import Path
import pandas as pd
import matplotlib.pyplot as plt

def main():
    # Determine directories
    script_dir = Path(__file__).resolve().parent  # .../project_root/python
    root_dir = script_dir.parent                  # .../project_root
    placers_dir = root_dir / "outputs" / "placers"

    if not placers_dir.is_dir():
        print(f"Error: placers directory not found at {placers_dir}", file=sys.stderr)
        sys.exit(1)

    # Find all cost_history.csv files under each placer's printout directory
    csv_files = placers_dir.glob("*/printout/cost_history.csv")
    dfs = []

    for csv_file in csv_files:
        placer_name = csv_file.parents[1].name  # two levels up: placers/<PlacerName>/printout
        try:
            df = pd.read_csv(csv_file)
        except Exception as e:
            print(f"Warning: failed to read {csv_file}: {e}", file=sys.stderr)
            continue

        # Strip whitespace from column names
        df.columns = df.columns.str.strip()

        if "Iter" not in df.columns or "Cost" not in df.columns:
            print(f"Warning: unexpected columns in {csv_file} (found: {list(df.columns)})", file=sys.stderr)
            continue

        # Keep only Iter and rename Cost column to the placer name
        df = df[["Iter", "Cost"]].rename(columns={"Cost": placer_name})
        dfs.append(df)

    if not dfs:
        print("No valid cost_history.csv files found.", file=sys.stderr)
        sys.exit(1)

    # Merge all DataFrames on Iter
    df_merged = dfs[0]
    for df in dfs[1:]:
        df_merged = df_merged.merge(df, on="Iter", how="outer")

    df_merged = df_merged.sort_values("Iter").reset_index(drop=True)

    # Save combined CSV to project root
    output_csv = root_dir / "outputs" / "combined_cost_history.csv"
    df_merged.to_csv(output_csv, index=False)
    print(f"Combined dataset saved to {output_csv}")

    # get placer columns (everything except "Iter") in alphabetical order
    placer_cols = sorted([c for c in df_merged.columns if c != "Iter"])

    # 1) Linear‐scale plot
    plt.figure(figsize=(12,8))
    for col in placer_cols:
        plt.plot(df_merged["Iter"], df_merged[col], label=col)
    plt.xlabel('Iteration')
    plt.ylabel('Cost')
    plt.title('Cost History Across Placers (Linear Scale)')
    plt.grid(True, linestyle='--', linewidth=0.5)
    plt.legend()  # entries are already alphabetical
    plt.tight_layout()
    plot_linear = root_dir / "outputs" / "combined_cost_history_linear.png"
    plt.savefig(plot_linear)
    print(f"Linear‐scale plot saved to {plot_linear}")

    # 2) Log‐scale plot
    plt.figure(figsize=(12,8))
    for col in placer_cols:
        plt.plot(df_merged["Iter"], df_merged[col], label=col)
    plt.yscale('log')
    max_cost = df_merged[placer_cols].max().max()
    plt.ylim(1, max_cost)
    plt.xlabel('Iteration')
    plt.ylabel('Cost (log scale)')
    plt.title('Cost History Across Placers (Log Scale)')
    plt.grid(True, which='both', linestyle='--', linewidth=0.5)
    plt.legend()  # still alphabetical
    plt.tight_layout()
    plot_log = root_dir / "outputs" / "combined_cost_history_log.png"
    plt.savefig(plot_log)
    print(f"Log‐scale plot saved to {plot_log}")

if __name__ == "__main__":
    main()
